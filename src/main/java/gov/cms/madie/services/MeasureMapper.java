package gov.cms.madie.services;

import generated.gov.cms.madie.simplexml.*;
import gov.cms.madie.dto.CQLCode;
import gov.cms.madie.dto.CQLCodeSystem;
import gov.cms.madie.dto.CQLDefinition;
import gov.cms.madie.dto.CQLFunctionArgument;
import gov.cms.madie.dto.CQLParameter;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.models.common.Organization;
import gov.cms.madie.models.common.Version;
import gov.cms.madie.models.measure.BaseConfigurationTypes;
import gov.cms.madie.models.measure.DefDescPair;
import gov.cms.madie.models.measure.Endorsement;
import gov.cms.madie.models.measure.Group;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.MeasureMetaData;
import gov.cms.madie.models.measure.MeasureObservation;
import gov.cms.madie.models.measure.Population;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.models.measure.Reference;
import gov.cms.madie.models.measure.Stratification;
import gov.cms.madie.util.MadieConstants;
import gov.cms.madie.util.MappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MeasureMapper {

  @Mapping(target = "cqlLookUp", source = "cqlLookups")
  @Mapping(target = "measureDetails", source = "measure")
  @Mapping(target = "measureGrouping", source = "measure")
  @Mapping(target = "supplementalDataElements", source = "measure.supplementalData")
  @Mapping(target = "riskAdjustmentVariables", source = "measure.riskAdjustments")
  MeasureType measureToMeasureType(QdmMeasure measure, CqlLookups cqlLookups);

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
  @Mapping(target = "aggregation", source = "rateAggregation")
  @Mapping(target = "rationale", source = "measure.measureMetaData.rationale")
  @Mapping(target = "recommendations", source = "measure.measureMetaData.clinicalRecommendation")
  @Mapping(target = "improvementNotations", source = "measure.improvementNotation")
  @Mapping(target = "references", source = "measure.measureMetaData.references")
  @Mapping(target = "definitions", source = "measure.measureMetaData.definition")
  @Mapping(target = "guidance", source = "measure.measureMetaData.guidance")
  @Mapping(target = "transmissionFormat", source = "measure.measureMetaData.transmissionFormat")
  @Mapping(
      target = "initialPopDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.INITIAL_POPULATION))")
  @Mapping(
      target = "denominatorDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.DENOMINATOR))")
  @Mapping(
      target = "denominatorExclusionsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.DENOMINATOR_EXCLUSION))")
  @Mapping(
      target = "numeratorDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.NUMERATOR))")
  @Mapping(
      target = "numeratorExclusionsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.NUMERATOR_EXCLUSION))")
  @Mapping(
      target = "denominatorExceptionsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.DENOMINATOR_EXCEPTION))")
  @Mapping(
      target = "measurePopulationDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.MEASURE_POPULATION))")
  @Mapping(
      target = "measurePopulationExclusionsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.MEASURE_POPULATION_OBSERVATION))")
  @Mapping(
      target = "measureObservationsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, gov.cms.madie.models.measure.PopulationType.MEASURE_OBSERVATION))")
  @Mapping(target = "supplementalData", source = "supplementalDataDescription")
  @Mapping(target = "finalizedDate", source = "measure")
  MeasureDetailsType measureToMeasureDetailsType(QdmMeasure measure);

  default MeasureGroupingType measureToMeasureGroupingType(QdmMeasure measure) {
    if (measure == null || CollectionUtils.isEmpty(measure.getGroups())) {
      return null;
    }

    MeasureGroupingType measureGroupingType = new MeasureGroupingType();
    final List<Group> groups = measure.getGroups();
    measureGroupingType
        .getGroup()
        .addAll(
            IntStream.range(0, groups.size())
                .mapToObj(i -> groupToGroupType(groups.get(i), i + 1))
                .toList());

    return measureGroupingType;
  }

  @Mapping(target = "sequence", source = "sequence")
  @Mapping(target = "clause", source = "group")
  @Mapping(target = "ucum", source = "group.scoringUnit", qualifiedByName = "scoringUnitToUcum")
  GroupType groupToGroupType(Group group, int sequence);

  default List<ClauseType> groupToClauseTypes(Group group) {
    if (group == null) {
      return null;
    }

    // Clauses are listed in the order Populations, Observations, Stratums
    List<ClauseType> clauses = new ArrayList<>();
    if (!CollectionUtils.isEmpty(group.getPopulations())) {
      clauses.addAll(group.getPopulations().stream().map(this::populationToClauseType).toList());
    }
    if (!CollectionUtils.isEmpty(group.getMeasureObservations())) {
      clauses.addAll(
          group.getMeasureObservations().stream().map(this::observationToClauseType).toList());
    }
    if (!CollectionUtils.isEmpty(group.getStratifications())) {
      clauses.addAll(
          group.getStratifications().stream().map(this::stratificationToClauseType).toList());
    }
    return CollectionUtils.isEmpty(clauses) ? null : clauses;
  }

  @Mapping(
      target = "isInGrouping",
      expression =
          "java(String.valueOf(org.apache.commons.lang3.StringUtils.isNotBlank(population.getDefinition())))")
  @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID().toString())")
  @Mapping(target = "cqldefinitionOrCqlaggfunction", source = "population")
  @Mapping(target = "type", expression = "java(gov.cms.madie.util.MappingUtil.getPopulationType(population.getName()))")
  // TODO: clause type display name
  @Mapping(target = "displayName", source = "name")
  ClauseType populationToClauseType(Population population);

  default List<Object> populationToDefOrAgg(Population population) {
    if (population == null || StringUtils.isBlank(population.getDefinition())) {
      return null;
    }
    CqldefinitionType cqldefinitionType = new CqldefinitionType();
    cqldefinitionType.setUuid(UUID.randomUUID().toString());
    cqldefinitionType.setDisplayName(population.getDefinition());
    return List.of(cqldefinitionType);
  }

  @Mapping(
      target = "isInGrouping",
      expression =
          "java(String.valueOf(org.apache.commons.lang3.StringUtils.isNotBlank(observation.getDefinition())))")
  @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID().toString())")
  // TODO: clause type display name
  ClauseType observationToClauseType(MeasureObservation observation);

  // TODO: map observation to definition/aggregate function

  @Mapping(
      target = "isInGrouping",
      expression =
          "java(String.valueOf(org.apache.commons.lang3.StringUtils.isNotBlank(stratification.getCqlDefinition())))")
  @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID().toString())")
  // TODO: clause type display name
  ClauseType stratificationToClauseType(Stratification stratification);

  // TODO: map stratification to definition/aggregate function

  @Named("scoringUnitToUcum")
  default String scoringUnitToUcum(Object scoringUnit) {
    if (scoringUnit == null || "".equals(scoringUnit)) {
      return null;
    }

    Map<String, Object> scoringUnitMap = (Map<String, Object>) scoringUnit;
    if (!scoringUnitMap.containsKey("value")) {
      return null;
    }

    Map<String, String> values = (Map<String, String>) scoringUnitMap.get("value");
    return values.get("code");
  }

  @Mapping(
      target = "id",
      expression = "java(gov.cms.madie.util.MappingUtil.getScoringAbbr(scoring))")
  @Mapping(target = "value", source = "scoring")
  ScoringType scoringToScoringType(String scoring);

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
    return version == null ? null : version.toString();
  }

  @Mapping(target = "calenderYear", constant = "false")
  @Mapping(target = "startDate", source = "measure.measurementPeriodStart", dateFormat = "yyyyMMdd")
  @Mapping(target = "stopDate", source = "measure.measurementPeriodEnd", dateFormat = "yyyyMMdd")
  @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID().toString())")
  PeriodType measureToPeriodType(Measure measure);

  @Mapping(target = "value", source = "organization.name")
  @Mapping(target = "id", source = "organization.oid")
  StewardType organizationToStewardType(Organization organization);

  default DevelopersType measureMetaDataToDevelopersType(MeasureMetaData measureMetaData) {
    if (measureMetaData == null || CollectionUtils.isEmpty(measureMetaData.getDevelopers())) {
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

  @Mapping(target = "displayName", source = "definition")
  @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID().toString())")
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
    if (measure.getMeasureMetaData() == null
        || measure.getMeasureMetaData().isDraft()
        || measure.getLastModifiedAt() == null) {
      return null;
    }
    FinalizedDateType finalizedDateType = new FinalizedDateType();
    finalizedDateType.setValueAttribute(
        DateTimeFormatter.ofPattern(MadieConstants.DATE_FORMAT)
            .withZone(ZoneId.systemDefault())
            .format(measure.getLastModifiedAt()));
    return finalizedDateType;
  }

  @Mapping(target = "definitions", source = "definitions")
  @Mapping(target = "functions", source = "definitions")
  CqlLookUpType cqlLookupsToCqlLookUpType(CqlLookups cqlLookups);

  default CodeSystemsType cqlCodeSystemsToCodeSystemsType(Set<CQLCodeSystem> codeSystems) {
    if (!CollectionUtils.isEmpty(codeSystems)) {
      CodeSystemsType codeSystemsType = new CodeSystemsType();
      codeSystemsType.getCodeSystem().addAll(cqlCodeSystemsToCodeSystems(codeSystems));
      return codeSystemsType;
    }

    return null;
  }

  default CodesType cqlCodesToCodesType(Set<CQLCode> cqlCodes) {
    if (!CollectionUtils.isEmpty(cqlCodes)) {
      CodesType codesType = new CodesType();
      codesType.getCode().addAll(cqlCodesToCodes(cqlCodes));
      return codesType;
    }

    return null;
  }

  default ParametersType cqlParametersToParametersType(Set<CQLParameter> cqlParameters) {
    if (!CollectionUtils.isEmpty(cqlParameters)) {
      ParametersType parametersType = new ParametersType();
      parametersType.getParameter().addAll(cqlParametersToParameterTypes(cqlParameters));
      return parametersType;
    }
    return null;
  }

  List<ParameterType> cqlParametersToParameterTypes(Set<CQLParameter> cqlParameters);

  default DefinitionsType cqlDefinitionsToDefinitionsType(Set<CQLDefinition> cqlDefinitions) {
    if (!CollectionUtils.isEmpty(cqlDefinitions)) {
      Set<CQLDefinition> defsWithoutFuncs =
          cqlDefinitions.stream().filter(d -> !d.isFunction()).collect(Collectors.toSet());
      if (!CollectionUtils.isEmpty(defsWithoutFuncs)) {
        DefinitionsType definitionsType = new DefinitionsType();
        definitionsType.getDefinition().addAll(cqlDefinitionsToDefinitionTypes(defsWithoutFuncs));
        return definitionsType;
      }
    }

    return null;
  }

  default FunctionsType cqlDefinitionsToFunctionsType(Set<CQLDefinition> cqlDefinitions) {
    if (!CollectionUtils.isEmpty(cqlDefinitions)) {
      Set<CQLDefinition> defsOnlyFuncs =
          cqlDefinitions.stream().filter(CQLDefinition::isFunction).collect(Collectors.toSet());
      if (!CollectionUtils.isEmpty(defsOnlyFuncs)) {
        FunctionsType functionsType = new FunctionsType();
        functionsType.getFunction().addAll(cqlDefinitionsToFunctionTypes(defsOnlyFuncs));
        return functionsType;
      }
    }

    return null;
  }

  List<CodeSystemType> cqlCodeSystemsToCodeSystems(Set<CQLCodeSystem> codeSystems);

  List<CodeType> cqlCodesToCodes(Set<CQLCode> cqlCodes);

  @Mapping(
      target = "codeSystemOID",
      expression =
          "java(org.apache.commons.lang3.StringUtils.replaceChars(cqlCode.getCodeSystemOID(), \"urn:oid:\",\"\"))")
  @Mapping(target = "codeOID", source = "id")
  @Mapping(target = "isValidatedWithVsac", source = "isValidatedWithVsac")
  @Mapping(
      target = "isCodeSystemVersionIncluded",
      expression = "java(String.valueOf(cqlCode.isCodeSystemVersionIncluded()))")
  CodeType cqlCodeToCodeType(CQLCode cqlCode);

  List<DefinitionType> cqlDefinitionsToDefinitionTypes(Set<CQLDefinition> cqlDefinitions);

  List<FunctionType> cqlDefinitionsToFunctionTypes(Set<CQLDefinition> cqlDefinitions);

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
  @Mapping(target = "name", source = "definitionName")
  @Mapping(target = "logic", source = "definitionLogic")
  DefinitionType cqlDefinitionToDefinitionType(CQLDefinition cqlDefinition);

  @Mapping(target = "logic", source = "definitionLogic")
  @Mapping(target = "name", source = "definitionName")
  @Mapping(target = "arguments", source = "functionArguments")
  FunctionType cqlDefinitionToFunctionType(CQLDefinition cqlDefinition);

  default ArgumentsType functionArgumentsToArgumentsType(
      List<CQLFunctionArgument> functionArguments) {
    if (!CollectionUtils.isEmpty(functionArguments)) {
      ArgumentsType argumentsType = new ArgumentsType();
      argumentsType.getArgument().addAll(functionArgumentsToArgumentTypes(functionArguments));
      return argumentsType;
    }
    return null;
  }

  List<ArgumentType> functionArgumentsToArgumentTypes(List<CQLFunctionArgument> functionArguments);

  @Mapping(target = "type", source = "argumentType")
  ArgumentType functionArgumentToArgumentType(CQLFunctionArgument functionArguments);
}
