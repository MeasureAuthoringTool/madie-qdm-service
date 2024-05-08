package gov.cms.madie.services;

import gov.cms.madie.Exceptions.CqmConversionException;
import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.cqm.*;
import gov.cms.madie.models.cqm.datacriteria.basetypes.DataElement;
import gov.cms.madie.models.cqm.datacriteria.*;
import gov.cms.madie.models.measure.QdmMeasure;
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

  @Mapping(target = "hqmf_set_id", source = "measure.measureSetId")
  @Mapping(target = "hqmf_version_number", source = "measure.versionId")
  @Mapping(target = "cms_id", source = "measure.measureSet.cmsId")
  @Mapping(target = "title", source = "measure.measureName")
  @Mapping(target = "description", source = "measure.measureMetaData.description")
  @Mapping(target = "measure_scoring", expression = "java(measure.getScoring().toUpperCase())")
  @Mapping(target = "main_cql_library", source = "measure.cqlLibraryName")
  @Mapping(target = "calculate_sdes", source = "measure.testCaseConfiguration.sdeIncluded")
  @Mapping(target = "calculation_method", expression = "java(getCalculationMethod(measure))")
  @Mapping(target = "measure_period", expression = "java(getMeasurePeriod(measure))")
  @Mapping(target = "cql_libraries", expression = "java(getCqlLibraries(measure, elms))")
  @Mapping(
      target = "source_data_criteria",
      expression = "java(getSourceDataCriteria(dataCriteria))")
  CqmMeasure measureToCqmMeasure(
      QdmMeasure measure, List<String> elms, List<SourceDataCriteria> dataCriteria);

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
            .atZone(ZoneId.of("UTC"))
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHH")));

    JSONObject high = new JSONObject();
    high.put(
        "value",
        measure
            .getMeasurementPeriodEnd()
            .toInstant()
            .atZone(ZoneId.of("UTC"))
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHH")));

    return MeasurePeriod.builder().low(low.toString()).high(high.toString()).build();
  }

  default List<CQLLibrary> getCqlLibraries(QdmMeasure measure, List<String> elms) {

    List<StatementDependency> statements =
        ElmDependencyUtil.findDependencies(elms, measure.getCqlLibraryName());

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

  default List<DataElement> getSourceDataCriteria(List<SourceDataCriteria> dataCriteria) {
    if (CollectionUtils.isEmpty(dataCriteria)) {
      return null;
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
}
