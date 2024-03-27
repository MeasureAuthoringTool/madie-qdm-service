package gov.cms.madie.services;

import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.measure.*;
import gov.cms.madie.qrda.*;
import gov.cms.madie.util.ElmDependencyUtil;
import org.json.JSONObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CqmMeasureMapper {

  //TODO waiting for SME feedback
  @Mapping(target = "hqmf_set_id", source = "measure.measureSetId")
  @Mapping(target = "cms_id", source = "measure.cmsId")
  @Mapping(target = "title", source = "measure.measureName")
  @Mapping(target = "description", source = "measure.measureMetaData.description")
  @Mapping(target = "measure_scoring", expression = "java(measure.getScoring().toUpperCase())")
  @Mapping(target = "main_cql_library", source = "measure.cqlLibraryName")
  @Mapping(target = "calculate_sdes", source = "measure.testCaseConfiguration.sdeIncluded")
  @Mapping(target = "calculation_method", expression = "java(getCalculationMethod(measure))")
  @Mapping(target = "measure_period", expression = "java(getMeasurePeriod(measure))")
  @Mapping(target = "cql_libraries", expression = "java(getCqlLibraries(measure, elms))")
  //  @Mapping(target = "population_sets", expression = "java(getPopulationSets(measure))")
  //    @Mapping(target = "source_data_criteria", expression =
  // "java(getSourceDataCriteria(measure))")
  CqmMeasure measureToCqmMeasure(
      QdmMeasure measure, List<String> elms, List<SourceDataCriteria> sourceDataCriteria);

  default String getCalculationMethod(QdmMeasure measure) {
    return measure.isPatientBasis() ? "PATIENT" : "EPISODE_OF_CARE";
  }

  default MeasurePeriod getMeasurePeriod(QdmMeasure measure) {
    JSONObject low = new JSONObject();
    low.put(
        "value",
        measure
            .getMeasurementPeriodStart()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

    JSONObject high = new JSONObject();
    high.put(
        "value",
        measure
            .getMeasurementPeriodEnd()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

    return MeasurePeriod.builder().low(low.toString()).high(high.toString()).build();
  }

  default List<CQLLibrary> getCqlLibraries(QdmMeasure measure, List<String> elms) {

    List<StatementDependency> statements = new ArrayList<>();
    try {
      statements = ElmDependencyUtil.findDependencies(elms, measure.getCqlLibraryName());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    List<StatementDependency> finalStatements = statements;
    return elms.stream()
        .map(elm -> buildCQLLibrary(elm, measure.getCqlLibraryName(), finalStatements))
        .collect(Collectors.toList());
  }

  default CQLLibrary buildCQLLibrary(
      String elm, String measureLibraryName, List<StatementDependency> statementDependencies) {

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
                statementDependencies.stream()
                    .filter(
                        statementDependency ->
                            statementDependency
                                .getStatement_name()
                                .equals(
                                    elmJson
                                        .getJSONObject("library")
                                        .getJSONObject("identifier")
                                        .getString("id")))
                    .collect(Collectors.toList()))
            .build();

    cqlLibrary.set_main_library(measureLibraryName.equals(cqlLibrary.getLibrary_name()));

    return cqlLibrary;
  }

  //TODO waiting for SME feedback
  //  default List<PopulationSet> getPopulationSets(QdmMeasure measure) {
  //    if (Collections.isEmpty(measure.getGroups())) {
  //      return null;
  //    }
  //
  //    String measureScoring = measure.getScoring().replaceAll(" +", "");
  //    List<PopulationSet> populationSets = new ArrayList<>();
  //    for (int i = 0; i < measure.getGroups().size(); i++) {
  //      String groupId = measure.getGroups().get(i).getId();
  //      PopulationSet populationSet =
  //          PopulationSet.builder()
  //              .id(groupId)
  //              .title("Population Criteria Section")
  //              .population_set_id(groupId)
  //              .populations(
  //                  generateCqmPopulations(
  //                      measure.getGroups().get(i).getPopulations(), measure.getCqlLibraryName(),
  // measureScoring))
  //              .stratifications(
  //                  generateCqmStratifications(
  //                      measure.getGroups().get(i).getStratifications(),
  //                      measure.getCqlLibraryName(),
  //                      i))
  //              .supplemental_data_elements(
  //                  generateCqmSupplementalDataElements(
  //                      measure.getSupplementalData(), measure.getCqlLibraryName()))
  //              .build();
  //      if (measureScoring.equals("ContinuousVariable") || measureScoring.equals("Ratio")) {
  //        populationSet.setObservations(generateCqmObservations(measure));
  //      }
  //      populationSets.add(populationSet);
  //    }
  //    return populationSets;
  //  }
  //
  //  default String mapPopulationName(PopulationType populationName) {
  //    switch (populationName) {
  //      case INITIAL_POPULATION:
  //        return "IPP";
  //      case DENOMINATOR:
  //        return "DENOM";
  //      case DENOMINATOR_EXCLUSION:
  //        return "DENEX";
  //      case DENOMINATOR_EXCEPTION:
  //        return "DENEXCEP";
  //      case NUMERATOR:
  //        return "NUMER";
  //      case NUMERATOR_EXCLUSION:
  //        return "NUMEX";
  //      case MEASURE_POPULATION:
  //        return "MSRPOPL";
  //      case MEASURE_POPULATION_EXCLUSION:
  //        return "MSRPOPLEX";
  //      default:
  //        return populationName.name();
  //    }
  //  }
  //
  //  default PopulationMap determinePopulationType(Map<String, Object> acc, String measureScoring)
  // {
  //    ObjectMapper mapper = new ObjectMapper();
  //    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
  //    switch (measureScoring){
  //      case "Cohort":
  //        return mapper.convertValue(acc, CohortPopulationMap.class);
  //      case "ContinuousVariable":
  //        return mapper.convertValue(acc, ContinuousVariablePopulationMap.class);
  //      case "Proportion":
  //        return mapper.convertValue(acc, ProportionPopulationMap.class);
  //      case "Ratio":
  //        return mapper.convertValue(acc, RatioPopulationMap.class);
  //      default:
  //        return null;
  //    }
  //  }
  //
  //  default PopulationMap generateCqmPopulations(
  //      List<Population> populations, String cqlLibraryName, String measureScoring) {
  //    ObjectMapper mapper = new ObjectMapper();
  //    Map<String, Object> acc = new HashMap<>();
  //    for (Population population : populations) {
  //      String key = mapPopulationName(population.getName());
  //      acc.put(
  //          key,
  //          StatementReference.builder()
  //              .id(population.getId())
  //              .library_name(cqlLibraryName)
  //              .statement_name(population.getDefinition())
  //              .hqmf_id(null)
  //              .build());
  //    }
  //    return determinePopulationType(acc, measureScoring);
  //  }
  //
  //  default List<CqmStratification> generateCqmStratifications(
  //      List<Stratification> stratifications, String cqlLibraryName, int groupIndex) {
  //    List<CqmStratification> cqmStratifications = new ArrayList<>();
  //    if (stratifications != null) {
  //      for (int i = 0; i < stratifications.size(); i++) {
  //        Stratification stratification = stratifications.get(i);
  //        CqmStratification cqmStratification =
  //            CqmStratification.builder()
  //                .id(UUID.randomUUID().toString())
  //                .hqmf_id(null)
  //                .stratification_id(
  //                    String.format("PopulationSet_%d_Stratification_%d", groupIndex + 1, i + 1))
  //                .title(String.format("PopSet%d Stratification %d", groupIndex + 1, i + 1))
  //                .statement(
  //                    StatementReference.builder()
  //                        .id(stratification.getId())
  //                        .library_name(cqlLibraryName)
  //                        .statement_name(stratification.getCqlDefinition())
  //                        .hqmf_id(null)
  //                        .build())
  //                .build();
  //        cqmStratifications.add(cqmStratification);
  //      }
  //    }
  //    return cqmStratifications;
  //  }
  //
  //  default List<StatementReference> generateCqmSupplementalDataElements(
  //      List<DefDescPair> supplementalDataElements, String cqlLibraryName) {
  //    List<StatementReference> statementReferences = new ArrayList<>();
  //    for (DefDescPair element : supplementalDataElements) {
  //      statementReferences.add(
  //          StatementReference.builder()
  //              .id(UUID.randomUUID().toString())
  //              .library_name(cqlLibraryName)
  //              .statement_name(element.getDefinition())
  //              .hqmf_id(null)
  //              .build());
  //    }
  //    return statementReferences;
  //  }
  //
  //  default List<Observation> generateCqmObservations(QdmMeasure measure) {
  //    List<Observation> cqmObservations = new ArrayList<>();
  //    if (!Collections.isEmpty(measure.getGroups())) {
  //      for (Group group : measure.getGroups()) {
  //        if (!Collections.isEmpty(group.getMeasureObservations())) {
  //          for (MeasureObservation observation : group.getMeasureObservations()) {
  //            cqmObservations.add(
  //                Observation.builder()
  //                    .id(observation.getId())
  //                    .hqmf_id(null)
  //                    .aggregation_type(observation.getAggregateMethod())
  //                    .observation_function(
  //                        StatementReference.builder()
  //                            .id(UUID.randomUUID().toString())
  //                            .library_name(measure.getCqlLibraryName())
  //                            .statement_name(observation.getDefinition())
  //                            .hqmf_id(null)
  //                            .build())
  //                    .observation_parameter(
  //                        StatementReference.builder()
  //                            .id(UUID.randomUUID().toString())
  //                            .library_name(measure.getCqlLibraryName())
  //                            .statement_name(
  //                                getAssociatedPopulationDefinition(
  //                                    observation.getCriteriaReference(), group.getPopulations()))
  //                            .hqmf_id(null)
  //                            .build())
  //                    .build());
  //          }
  //        }
  //      }
  //    }
  //    return cqmObservations;
  //  }
  //
  //  default String getAssociatedPopulationDefinition(
  //      String criteriaReference, List<Population> populations) {
  //    for (Population population : populations) {
  //      if (population.getId().equals(criteriaReference)) {
  //        return population.getDefinition();
  //      }
  //    }
  //    return null;
  //  }
}
