package gov.cms.madie.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.madie.Exceptions.CqmConversionException;
import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.cqm.*;
import gov.cms.madie.models.cqm.datacriteria.basetypes.DataElement;
import gov.cms.madie.models.cqm.datacriteria.*;
import gov.cms.madie.models.measure.*;
import gov.cms.madie.util.ElmDependencyUtil;
import org.json.JSONObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CqmMeasureMapper {
  ObjectMapper mapper = new ObjectMapper();

  @Mapping(target = "hqmf_set_id", source = "measure.measureSetId")
  @Mapping(target = "hqmf_version_number", source = "measure.versionId")
  @Mapping(target = "cms_id", source = "measure.measureSet.cmsId")
  @Mapping(target = "title", source = "measure.measureName")
  @Mapping(target = "description", source = "measure.measureMetaData.description")
  @Mapping(
      target = "measure_scoring",
      expression =
          "java(measure.getScoring() == null || measure.getScoring().isEmpty() ? null: measure.getScoring().toUpperCase())")
  @Mapping(target = "main_cql_library", source = "measure.cqlLibraryName")
  @Mapping(target = "calculate_sdes", source = "measure.testCaseConfiguration.sdeIncluded")
  @Mapping(target = "calculation_method", expression = "java(getCalculationMethod(measure))")
  @Mapping(target = "measure_period", expression = "java(getMeasurePeriod(measure))")
  @Mapping(target = "cql_libraries", expression = "java(getCqlLibraries(measure, elms))")
  @Mapping(target = "population_sets", expression = "java(getPopulationSets(measure))")
  @Mapping(
      target = "source_data_criteria",
      expression = "java(getSourceDataCriteria(dataCriteria))")
  CqmMeasure measureToCqmMeasure(
      QdmMeasure measure, List<String> elms, List<SourceDataCriteria> dataCriteria);

  default String getCalculationMethod(QdmMeasure measure) {
    return measure.isPatientBasis() ? "PATIENT" : "EPISODE_OF_CARE";
  }

  default MeasurePeriod getMeasurePeriod(QdmMeasure measure) {
    PeriodPoint low =
        PeriodPoint.builder()
            .value(
                measure
                    .getMeasurementPeriodStart()
                    .toInstant()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHH"))
                    .toString())
            .build();
    PeriodPoint high =
        PeriodPoint.builder()
            .value(
                measure
                    .getMeasurementPeriodEnd()
                    .toInstant()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHH"))
                    .toString())
            .build();

    return MeasurePeriod.builder().low(low).high(high).build();
  }

  default List<CQLLibrary> getCqlLibraries(QdmMeasure measure, List<String> elms) {

    Map<String, List<StatementDependency>> statements =
        ElmDependencyUtil.findDependencies(elms, measure.getCqlLibraryName());

    return elms.stream()
        .map(elm -> buildCQLLibrary(elm, measure.getCqlLibraryName(), statements))
        .collect(Collectors.toList());
  }

  default CQLLibrary buildCQLLibrary(
      String elm,
      String measureLibraryName,
      Map<String, List<StatementDependency>> statementDependencies) {

    JSONObject elmJson = new JSONObject(elm);
    if (elmJson.has("library") && elmJson.getJSONObject("library").has("valueSets")) {
      elmJson
          .getJSONObject("library")
          .getJSONObject("valueSets")
          .getJSONArray("def")
          .forEach(
              item -> {
                JSONObject valueSet = (JSONObject) item;
                valueSet.put("id", valueSet.getString("id").replace("urn:oid:", ""));
              });
    }
    if (elmJson.has("library") && elmJson.getJSONObject("library").has("codeSystems")) {
      elmJson
          .getJSONObject("library")
          .getJSONObject("codeSystems")
          .getJSONArray("def")
          .forEach(
              item -> {
                JSONObject codeSystem = (JSONObject) item;
                codeSystem.put("id", codeSystem.getString("id").replace("urn:oid:", ""));
              });
    }
    CQLLibrary cqlLibrary =
        CQLLibrary.builder()
            .library_name(
                elmJson.getJSONObject("library").getJSONObject("identifier").getString("id"))
            .library_version(
                elmJson.getJSONObject("library").getJSONObject("identifier").getString("version"))
            // true for all non-composite measures
            .is_top_level(true)
            .statement_dependencies(
                statementDependencies.get(
                    elmJson.getJSONObject("library").getJSONObject("identifier").getString("id")))
            .build();

    cqlLibrary.setElm(elmJson.toMap());
    cqlLibrary.set_main_library(measureLibraryName.equals(cqlLibrary.getLibrary_name()));

    return cqlLibrary;
  }

  default List<DataElement> getSourceDataCriteria(List<SourceDataCriteria> dataCriteria) {
    if (CollectionUtils.isEmpty(dataCriteria)) {
      return Collections.emptyList();
    }

    return dataCriteria.stream()
        .map(
            criteria -> {
              DataElement element = instantiateModel(criteria.getType());

              element.setCodeListId(criteria.getOid());
              element.setDescription(criteria.getDescription());
              return element;
            })
        .toList();
  }

  default DataElement instantiateModel(String type) {

    switch (type) {
      case "AdverseEvent":
        return new AdverseEvent();
      case "Allergy/Intolerance":
        return new AllergyIntolerance();
      case "AssessmentOrder":
        return new AssessmentOrder();
      case "AssessmentPerformed":
        return new AssessmentPerformed();
      case "AssessmentRecommended":
        return new AssessmentRecommended();
      case "CareGoal":
        return new CareGoal();
      case "CommunicationNotPerformed":
        return new CommunicationPerformed();
      case "DeviceOrder":
        return new DeviceOrder();
      case "DeviceRecommended":
        return new DeviceRecommended();
      case "Diagnosis":
        return new Diagnosis();
      case "DiagnosticStudyOrder":
        return new DiagnosticStudyOrder();
      case "DiagnosticStudyPerformed":
        return new DiagnosticStudyPerformed();
      case "DiagnosticStudyRecommended":
        return new DiagnosticStudyRecommended();
      case "EncounterOrder":
        return new EncounterOrder();
      case "EncounterPerformed":
        return new EncounterPerformed();
      case "EncounterRecommended":
        return new EncounterRecommended();
      case "FamilyHistory":
        return new FamilyHistory();
      case "ImmunizationAdministered":
        return new ImmunizationAdministered();
      case "ImmunizationOrder":
        return new ImmunizationOrder();
      case "InterventionOrder":
        return new InterventionOrder();
      case "InterventionPerformed":
        return new InterventionPerformed();
      case "InterventionRecommended":
        return new InterventionRecommended();
      case "LaboratoryTestOrder":
        return new LaboratoryTestOrder();
      case "LaboratoryTestPerformed":
        return new LaboratoryTestPerformed();
      case "LaboratoryTestRecommended":
        return new LaboratoryTestRecommended();
      case "MedicationActive":
        return new MedicationActive();
      case "MedicationAdministered":
        return new MedicationAdministered();
      case "MedicationDischarge":
        return new MedicationDischarge();
      case "MedicationDispensed":
        return new MedicationDispensed();
      case "MedicationOrder":
        return new MedicationOrder();
      case "Participation":
        return new Participation();
      case "PatientCareExperience":
        return new PatientCareExperience();
      case "PatientCharacteristic":
        return new PatientCharacteristic();
      case "PatientCharacteristicBirthdate":
        return new PatientCharacteristicBirthdate();
      case "PatientCharacteristicClinicalTrialParticipant":
        return new PatientCharacteristicClinicalTrialParticipant();
      case "PatientCharacteristicEthnicity":
        return new PatientCharacteristicEthnicity();
      case "PatientCharacteristicExpired":
        return new PatientCharacteristicExpired();
      case "PatientCharacteristicPayer":
        return new PatientCharacteristicPayer();
      case "PatientCharacteristicRace":
        return new PatientCharacteristicRace();
      case "PatientCharacteristicSex":
        return new PatientCharacteristicSex();
      case "PhysicalExamOrder":
        return new PhysicalExamOrder();
      case "PhysicalExamPerformed":
        return new PhysicalExamPerformed();
      case "PhysicalExamRecommended":
        return new PhysicalExamRecommended();
      case "ProcedurePerformed":
        return new ProcedurePerformed();
      case "ProcedureRecommended":
        return new ProcedureRecommended();
      case "ProviderCareExperience":
        return new ProviderCareExperience();
      case "ProcedureOrder":
        return new ProcedureOrder();
      case "RelatedPerson":
        return new RelatedPerson();
      case "SubstanceAdministered":
        return new SubstanceAdministered();
      case "SubstanceOrder":
        return new SubstanceOrder();
      case "SubstanceRecommended":
        return new SubstanceRecommended();
      case "Symptom":
        return new Symptom();
      default:
        throw new CqmConversionException("Unsupported data type: " + type);
    }
  }

  default List<PopulationSet> getPopulationSets(QdmMeasure measure) {
    if (CollectionUtils.isEmpty(measure.getGroups())) {
      return null;
    }

    String measureScoring = measure.getScoring().replaceAll(" +", "");
    List<PopulationSet> populationSets = new ArrayList<>();
    for (int i = 0; i < measure.getGroups().size(); i++) {
      String groupId = measure.getGroups().get(i).getId();
      PopulationSet populationSet =
          PopulationSet.builder()
              .id(groupId)
              .title("Population Criteria Section")
              .population_set_id(groupId)
              .populations(
                  generateCqmPopulations(
                      measure.getGroups().get(i).getPopulations(),
                      measure.getCqlLibraryName(),
                      measureScoring))
              .stratifications(
                  generateCqmStratifications(
                      measure.getGroups().get(i).getStratifications(),
                      measure.getCqlLibraryName(),
                      i))
              .supplemental_data_elements(
                  generateCqmSupplementalDataElements(
                      measure.getSupplementalData(), measure.getCqlLibraryName()))
              .build();
      if (measureScoring.equals("ContinuousVariable") || measureScoring.equals("Ratio")) {
        populationSet.setObservations(generateCqmObservations(measure));
      }
      populationSets.add(populationSet);
    }
    return populationSets;
  }

  default String mapPopulationName(PopulationType populationName) {
    switch (populationName) {
      case INITIAL_POPULATION:
        return "IPP";
      case DENOMINATOR:
        return "DENOM";
      case DENOMINATOR_EXCLUSION:
        return "DENEX";
      case DENOMINATOR_EXCEPTION:
        return "DENEXCEP";
      case NUMERATOR:
        return "NUMER";
      case NUMERATOR_EXCLUSION:
        return "NUMEX";
      case MEASURE_POPULATION:
        return "MSRPOPL";
      case MEASURE_POPULATION_EXCLUSION:
        return "MSRPOPLEX";
      default:
        return populationName.name();
    }
  }

  default PopulationMap determinePopulationType(Map<String, Object> acc, String measureScoring) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    try {
      switch (measureScoring) {
        case "Cohort":
          acc.put("@class", CohortPopulationMap.class);
          return mapper.readValue(mapper.writeValueAsString(acc), CohortPopulationMap.class);
        case "ContinuousVariable":
          acc.put("@class", ContinuousVariablePopulationMap.class);
          return mapper.readValue(
              mapper.writeValueAsString(acc), ContinuousVariablePopulationMap.class);
        case "Proportion":
          acc.put("@class", ProportionPopulationMap.class);
          return mapper.readValue(mapper.writeValueAsString(acc), ProportionPopulationMap.class);
        case "Ratio":
          acc.put("@class", RatioPopulationMap.class);
          return mapper.readValue(mapper.writeValueAsString(acc), RatioPopulationMap.class);
        default:
          return null;
      }

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  default PopulationMap generateCqmPopulations(
      List<Population> populations, String cqlLibraryName, String measureScoring) {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> acc = new HashMap<>();
    for (Population population : populations) {
      String key = mapPopulationName(population.getName());
      acc.put(
          key,
          StatementReference.builder()
              .id(population.getId())
              .library_name(cqlLibraryName)
              .statement_name(population.getDefinition())
              .hqmf_id(null)
              .build());
    }
    return determinePopulationType(acc, measureScoring);
  }

  default List<CqmStratification> generateCqmStratifications(
      List<Stratification> stratifications, String cqlLibraryName, int groupIndex) {
    List<CqmStratification> cqmStratifications = new ArrayList<>();
    if (stratifications != null) {
      for (int i = 0; i < stratifications.size(); i++) {
        Stratification stratification = stratifications.get(i);
        CqmStratification cqmStratification =
            CqmStratification.builder()
                .id(UUID.randomUUID().toString())
                .hqmf_id(null)
                .stratification_id(
                    String.format("PopulationSet_%d_Stratification_%d", groupIndex + 1, i + 1))
                .title(String.format("PopSet%d Stratification %d", groupIndex + 1, i + 1))
                .statement(
                    StatementReference.builder()
                        .id(stratification.getId())
                        .library_name(cqlLibraryName)
                        .statement_name(stratification.getCqlDefinition())
                        .hqmf_id(null)
                        .build())
                .build();
        cqmStratifications.add(cqmStratification);
      }
    }
    return cqmStratifications;
  }

  default List<StatementReference> generateCqmSupplementalDataElements(
      List<DefDescPair> supplementalDataElements, String cqlLibraryName) {
    List<StatementReference> statementReferences = new ArrayList<>();
    for (DefDescPair element : supplementalDataElements) {
      statementReferences.add(
          StatementReference.builder()
              .id(UUID.randomUUID().toString())
              .library_name(cqlLibraryName)
              .statement_name(element.getDefinition())
              .hqmf_id(null)
              .build());
    }
    return statementReferences;
  }

  default List<Observation> generateCqmObservations(QdmMeasure measure) {
    List<Observation> cqmObservations = new ArrayList<>();
    if (!CollectionUtils.isEmpty(measure.getGroups())) {
      for (Group group : measure.getGroups()) {
        if (!CollectionUtils.isEmpty(group.getMeasureObservations())) {
          for (MeasureObservation observation : group.getMeasureObservations()) {
            cqmObservations.add(
                Observation.builder()
                    .id(observation.getId())
                    .hqmf_id(null)
                    .aggregation_type(observation.getAggregateMethod())
                    .observation_function(
                        StatementReference.builder()
                            .id(UUID.randomUUID().toString())
                            .library_name(measure.getCqlLibraryName())
                            .statement_name(observation.getDefinition())
                            .hqmf_id(null)
                            .build())
                    .observation_parameter(
                        StatementReference.builder()
                            .id(UUID.randomUUID().toString())
                            .library_name(measure.getCqlLibraryName())
                            .statement_name(
                                getAssociatedPopulationDefinition(
                                    observation.getCriteriaReference(), group.getPopulations()))
                            .hqmf_id(null)
                            .build())
                    .build());
          }
        }
      }
    }
    return cqmObservations;
  }

  default String getAssociatedPopulationDefinition(
      String criteriaReference, List<Population> populations) {
    for (Population population : populations) {
      if (population.getId().equals(criteriaReference)) {
        return population.getDefinition();
      }
    }
    return null;
  }
}
