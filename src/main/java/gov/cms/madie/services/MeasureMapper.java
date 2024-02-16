package gov.cms.madie.services;

import generated.gov.cms.madie.simplexml.*;
import gov.cms.madie.models.common.Organization;
import gov.cms.madie.models.common.Version;
import gov.cms.madie.models.measure.BaseConfigurationTypes;
import gov.cms.madie.models.measure.DefDescPair;
import gov.cms.madie.models.measure.Endorsement;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.MeasureMetaData;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.models.measure.Reference;
import gov.cms.madie.util.MadieConstants;
import gov.cms.madie.util.MappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MeasureMapper {

  @Mapping(target = "measureDetails", source = "measure")
  @Mapping(target = "supplementalDataElements", source = "supplementalData")
  @Mapping(target = "riskAdjustmentVariables", source = "riskAdjustments")
  MeasureType measureToMeasureType(QdmMeasure measure);

  @Mapping(target = "title", source = "measureName")
  @Mapping(target = "measureModel", source = "model")
  @Mapping(target = "shortTitle", source = "ecqmTitle")
  @Mapping(target = "emeasureid", source = "cmsId")
  @Mapping(target = "guid", source = "versionId")
  @Mapping(
      target = "cbeid",
      source = "measure.measureMetaData.endorsements",
      qualifiedByName = "endorsementsToCbeid")
  @Mapping(target = "period", source = "measure")
  @Mapping(target = "steward", source = "measure.measureMetaData.steward")
  @Mapping(target = "experimental", source = "measure.measureMetaData.experimental")
  @Mapping(target = "developers", source = "measure.measureMetaData")
  @Mapping(target = "endorsement", source = "measure.measureMetaData.endorsements")
  @Mapping(target = "description", source = "measure.measureMetaData.description")
  @Mapping(target = "copyright", source = "measure.measureMetaData.copyright")
  @Mapping(target = "disclaimer", source = "measure.measureMetaData.disclaimer")
  @Mapping(
      target = "patientBasedIndicator",
      expression = "java(String.valueOf(measure.isPatientBasis()))")
  @Mapping(target = "types", source = "baseConfigurationTypes")
  //    @Mapping(target = "stratification", source = "")
  @Mapping(target = "riskAdjustment", source = "riskAdjustmentDescription")
  //    @Mapping(target = "aggregation", source = "")
  @Mapping(target = "rationale", source = "riskAdjustmentDescription")
  @Mapping(target = "recommendations", source = "measure.measureMetaData.clinicalRecommendation")
  @Mapping(target = "improvementNotations", source = "measure.measureMetaData.rationale")
  @Mapping(target = "definitions", source = "measure.measureMetaData.definition")
  @Mapping(target = "guidance", source = "measure.measureMetaData.guidance")
  @Mapping(target = "transmissionFormat", source = "measure.measureMetaData.transmissionFormat")
  @Mapping(target = "initialPopDescription", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.INITIAL_POPULATION))")
  @Mapping(target = "denominatorDescription", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.DENOMINATOR))")
  @Mapping(target = "denominatorExclusionsDescription", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.DENOMINATOR_EXCLUSION))")
  @Mapping(target = "numeratorDescription", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.NUMERATOR))")
  @Mapping(target = "numeratorExclusionsDescription", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.NUMERATOR_EXCLUSION))")
  @Mapping(target = "denominatorExceptionsDescription", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.DENOMINATOR_EXCEPTION))")
  @Mapping(target = "measurePopulationDescription", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.MEASURE_POPULATION))")
  @Mapping(target = "measurePopulationExclusionsDescription", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.MEASURE_POPULATION_OBSERVATION))")
//  @Mapping(target = "measureObservationsDescription", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.MEASURE_POPULATION_OBSERVATION))")
  @Mapping(target = "supplementalData", source = "supplementalDataDescription")
  @Mapping(target = "finalizedDate", source = "measure")
  MeasureDetailsType measureToMeasureDetailsType(QdmMeasure measure);

  @Mapping(
      target = "id",
      expression = "java(gov.cms.madie.util.MappingUtil.getScoringAbbr(scoring))")
  @Mapping(target = "value", source = "scoring")
  ScoringType scoringToScoringType(String scoring);

  default String supplementalDataToSupplementalData(List<DefDescPair> supplementalData) {
    return Optional.ofNullable(supplementalData).orElse(List.of()).stream()
        .map(DefDescPair::getDescription)
        .reduce((acc, t) -> acc + "\n" + t)
        .orElse(null);
  }

  default SupplementalDataElementsType supplementalDataToSupplementalDataElementsType(
      List<DefDescPair> supplementalData) {
    List<CqldefinitionType> defs =
        Optional.ofNullable(supplementalData).orElse(List.of()).stream()
            .map(this::defDescPairToCqldefinitionType)
            .toList();
    SupplementalDataElementsType supplementalDataElementsType = new SupplementalDataElementsType();
    supplementalDataElementsType.getCqldefinition().addAll(defs);
    return supplementalDataElementsType;
  }

  default RiskAdjustmentVariablesType riskAdjustmentsToRiskAdjustmentVariablesType(
      List<DefDescPair> riskAdjustments) {
    List<CqldefinitionType> defs =
        Optional.ofNullable(riskAdjustments).orElse(List.of()).stream()
            .map(this::defDescPairToCqldefinitionType)
            .toList();
    RiskAdjustmentVariablesType riskAdjustmentVariablesType = new RiskAdjustmentVariablesType();
    riskAdjustmentVariablesType.getCqldefinition().addAll(defs);
    return riskAdjustmentVariablesType;
  }

  default String versionToVersion(Version version) {
    return version.toString();
  }

  @Mapping(target = "calenderYear", constant = "false")
  @Mapping(target = "startDate", source = "measure.measurementPeriodStart", dateFormat = "yyyyMMdd")
  @Mapping(target = "stopDate", source = "measure.measurementPeriodEnd", dateFormat = "yyyyMMdd")
  PeriodType measureToPeriodType(Measure measure);

  @Mapping(target = "value", source = "organization.name")
  @Mapping(target = "id", source = "organization.oid")
  StewardType organizationToStewardType(Organization organization);

  default DevelopersType measureMetaDataToDevelopersType(MeasureMetaData measureMetaData) {
    if (CollectionUtils.isEmpty(measureMetaData.getDevelopers())) {
      return null;
    }
    DevelopersType developersType = new DevelopersType();
    developersType
        .getDeveloper()
        .addAll(
            measureMetaData.getDevelopers().stream()
                .map(this::organizationToDeveloperType)
                .toList());
    return developersType;
  }

  // Map the list of endorsements from MADiE model to a single CBE ID.
  // Due to bad data, it is possible to have a list with a single endorsement with all
  // null/empty-string values
  @Named("endorsementsToCbeid")
  default String endorsementsToCbeid(List<Endorsement> endorsements) {
    return CollectionUtils.isEmpty(endorsements)
            || StringUtils.isEmpty(endorsements.get(0).getEndorsementId())
        ? null
        : endorsements.get(0).getEndorsementId();
  }

  default EndorsementType endorsementsToEndorsementType(List<Endorsement> endorsements) {
    return CollectionUtils.isEmpty(endorsements)
            || StringUtils.isEmpty(endorsements.get(0).getEndorsementId())
        ? null
        : organizationToEndorsementType(endorsements.get(0));
  }

  @Mapping(target = "id", source = "endorsementId")
  @Mapping(target = "value", source = "endorser")
  EndorsementType organizationToEndorsementType(Endorsement endorsement);

  @Mapping(target = "value", source = "organization.name")
  @Mapping(target = "id", source = "organization.oid")
  DeveloperType organizationToDeveloperType(Organization organization);

  @Mapping(target = "value", source = "description")
  @Mapping(target = "displayName", source = "definition")
  CqldefinitionType defDescPairToCqldefinitionType(DefDescPair defDescPair);

  default TypesType baseConfigurationTypesToTypesTypes(
      List<BaseConfigurationTypes> baseConfigurationTypes) {
    if (CollectionUtils.isEmpty(baseConfigurationTypes)) {
      return null;
    }
    TypesType typesType = new TypesType();
    typesType
        .getType()
        .addAll(
            baseConfigurationTypes.stream().map(this::baseConfigurationTypeToTypesType).toList());
    return typesType;
  }

  default TypeType baseConfigurationTypeToTypesType(BaseConfigurationTypes baseConfigurationType) {
    if (baseConfigurationType == null) {
      return null;
    }
    TypeType type = new TypeType();
    type.setId(MappingUtil.getMeasureTypeId(baseConfigurationType));
    type.setValue(baseConfigurationType.toString());
    return type;
  }

  default ReferencesType referencesToReferencesType(List<Reference> references) {
    if (CollectionUtils.isEmpty(references)) {
      return null;
    }
    ReferencesType referencesType = new ReferencesType();
    referencesType
        .getReference()
        .addAll(references.stream().map(this::referenceToReferenceType).toList());
    return referencesType;
  }

  ReferenceType referenceToReferenceType(Reference reference);

  default FinalizedDateType instantToFinalizedDateType(Measure measure) {
    if (measure.getMeasureMetaData() == null || measure.getMeasureMetaData().isDraft()) {
      return null;
    }
    FinalizedDateType finalizedDateType = new FinalizedDateType();
    finalizedDateType.setValue(DateTimeFormatter.ofPattern(MadieConstants.DATE_FORMAT)
            .withZone(ZoneId.systemDefault()).format(measure.getLastModifiedAt()));
    return finalizedDateType;
  }
}
