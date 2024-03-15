package gov.cms.madie.services;

import generated.gov.cms.madie.simplexml.*;
import gov.cms.madie.dto.CQLCode;
import gov.cms.madie.dto.CQLCodeSystem;
import gov.cms.madie.dto.CQLDefinition;
import gov.cms.madie.dto.CQLFunctionArgument;
import gov.cms.madie.dto.CQLIncludeLibrary;
import gov.cms.madie.dto.CQLParameter;
import gov.cms.madie.dto.CQLValueSet;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.dto.ElementLookup;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MeasureMapper {

  @Mapping(target = "cqlLookUp", source = "cqlLookups")
  @Mapping(target = "measureDetails", source = "measure")
  @Mapping(
      target = "measureGrouping",
      expression = "java(measureToMeasureGroupingType(measure, cqlLookups))")
  @Mapping(target = "elementLookUp", source = "cqlLookups.elementLookups")
  @Mapping(
      target = "supplementalDataElements",
      expression =
          "java(supplementalDataToSupplementalDataElementsType(measure.getSupplementalData(), cqlLookups.getDefinitions()))")
  @Mapping(
      target = "riskAdjustmentVariables",
      expression =
          "java(riskAdjustmentsToRiskAdjustmentVariablesType(measure.getRiskAdjustments(), cqlLookups.getDefinitions()))")
  @Mapping(target = "allUsedCQLLibs", source = "cqlLookups.includeLibraries")
  MeasureType measureToMeasureType(QdmMeasure measure, CqlLookups cqlLookups);

  @Mapping(target = "uuid", source = "versionId")
  @Mapping(target = "cqlUUID", expression = "java(java.util.UUID.randomUUID().toString())")
  @Mapping(target = "title", source = "measureName")
  @Mapping(target = "measureModel", source = "model")
  @Mapping(target = "shortTitle", source = "ecqmTitle")
  @Mapping(target = "emeasureid", source = "measure.measureSet.cmsId")
  @Mapping(target = "guid", source = "measureSetId")
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
  @Mapping(target = "improvementNotations", expression = "java(getImprovementNotations(measure))")
  @Mapping(target = "references", source = "measure.measureMetaData.references")
  @Mapping(target = "definitions", source = "measure.measureMetaData.definition")
  @Mapping(target = "guidance", source = "measure.measureMetaData.guidance")
  @Mapping(target = "transmissionFormat", source = "measure.measureMetaData.transmissionFormat")
  @Mapping(
      target = "initialPopDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, PopulationType.INITIAL_POPULATION))")
  @Mapping(
      target = "denominatorDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, PopulationType.DENOMINATOR))")
  @Mapping(
      target = "denominatorExclusionsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, PopulationType.DENOMINATOR_EXCLUSION))")
  @Mapping(
      target = "numeratorDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, PopulationType.NUMERATOR))")
  @Mapping(
      target = "numeratorExclusionsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, PopulationType.NUMERATOR_EXCLUSION))")
  @Mapping(
      target = "denominatorExceptionsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, PopulationType.DENOMINATOR_EXCEPTION))")
  @Mapping(
      target = "measurePopulationDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, PopulationType.MEASURE_POPULATION))")
  @Mapping(
      target = "measurePopulationExclusionsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, PopulationType.MEASURE_POPULATION_OBSERVATION))")
  @Mapping(
      target = "measureObservationsDescription",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getPopulationDescription(measure, PopulationType.MEASURE_OBSERVATION))")
  @Mapping(
      target = "stratification",
      expression =
          "java(gov.cms.madie.util.MappingUtil.getStratificationDescription(measure.getGroups()))")
  @Mapping(target = "supplementalData", source = "supplementalDataDescription")
  @Mapping(target = "finalizedDate", source = "measure")
  @Mapping(target = "qualityMeasureSet", source = "measure")
  MeasureDetailsType measureToMeasureDetailsType(QdmMeasure measure);

  @Mapping(target = "uuid", source = "measureSetId")
  QualityMeasureSetType measureToQualityMeasureSet(QdmMeasure measure);

  default String getImprovementNotations(QdmMeasure measure) {
    if ("Other".equals(measure.getImprovementNotation())) {
      return measure.getImprovementNotationOther();
    }
    return measure.getImprovementNotation();
  }

  // measureGrouping mappings
  default MeasureGroupingType measureToMeasureGroupingType(
      QdmMeasure measure, CqlLookups cqlLookups) {
    if (measure == null || CollectionUtils.isEmpty(measure.getGroups()) || cqlLookups == null) {
      return null;
    }

    MeasureGroupingType measureGroupingType = new MeasureGroupingType();
    final List<Group> groups = measure.getGroups();
    measureGroupingType
        .getGroup()
        .addAll(
            IntStream.range(0, groups.size())
                .mapToObj(i -> groupToGroupType(groups.get(i), i + 1, cqlLookups))
                .toList());

    return measureGroupingType;
  }

  @Mapping(target = "sequence", source = "sequence")
  @Mapping(target = "clause", expression = "java(groupToClauseTypes(group, cqlLookups))")
  @Mapping(target = "ucum", source = "group.scoringUnit", qualifiedByName = "scoringUnitToUcum")
  GroupType groupToGroupType(Group group, int sequence, CqlLookups cqlLookups);

  default List<ClauseType> groupToClauseTypes(Group group, CqlLookups cqlLookups) {
    if (group == null) {
      return null;
    }
    Set<CQLDefinition> cqlDefinitions = cqlLookups.getDefinitions();
    // Clauses are listed in the order Populations, Observations, Stratums
    List<ClauseType> clauses = new ArrayList<>();
    if (!CollectionUtils.isEmpty(group.getPopulations())) {
      clauses.addAll(
          group.getPopulations().stream()
              .map(
                  population -> {
                    CQLDefinition cqlDefinition =
                        getCqlDefinition(population.getDefinition(), cqlDefinitions);
                    return populationToClauseType(population, cqlDefinition);
                  })
              .toList());
    }
    if (!CollectionUtils.isEmpty(group.getMeasureObservations())) {
      clauses.addAll(
          group.getMeasureObservations().stream()
              .map(
                  observation -> {
                    CQLDefinition cqlDefinition =
                        getCqlDefinition(observation.getDefinition(), cqlDefinitions);
                    String associatedPopulationUUID = "";
                    if ("Ratio".equals(group.getScoring())) {
                      Population associatedPopulation =
                          group.getPopulations().stream()
                              .filter(
                                  population ->
                                      population.getId().equals(observation.getCriteriaReference()))
                              .findFirst()
                              .orElse(null);
                      associatedPopulationUUID =
                          associatedPopulation != null ? associatedPopulation.getId() : "";
                    }
                    return observationToClauseType(
                        observation, cqlDefinition, associatedPopulationUUID);
                  })
              .toList());
    }
    if (!CollectionUtils.isEmpty(group.getStratifications())) {
      clauses.addAll(
          group.getStratifications().stream()
              .map(
                  stratification -> {
                    CQLDefinition cqlDefinition =
                        getCqlDefinition(stratification.getCqlDefinition(), cqlDefinitions);
                    return stratificationToClauseType(stratification, cqlDefinition);
                  })
              .toList());
    }
    return CollectionUtils.isEmpty(clauses) ? null : clauses;
  }

  default CQLDefinition getCqlDefinition(String definition, Set<CQLDefinition> cqlDefinitions) {
    return cqlDefinitions.stream()
        .filter(cqlDefinition -> definition.equals(cqlDefinition.getDefinitionName()))
        .findFirst()
        .orElse(null);
  }

  // population mappings
  @Mapping(
      target = "isInGrouping",
      expression =
          "java(String.valueOf(org.apache.commons.lang3.StringUtils.isNotBlank(population.getDefinition())))")
  @Mapping(target = "uuid", source = "population.id")
  @Mapping(
      target = "cqldefinition",
      expression = "java(populationToCqlDefinition(population, cqlDefinition))")
  @Mapping(
      target = "type",
      expression = "java(gov.cms.madie.util.MappingUtil.getPopulationType(population.getName()))")
  @Mapping(target = "displayName", expression = "java(population.getName().getDisplay())")
  ClauseType populationToClauseType(Population population, CQLDefinition cqlDefinition);

  @Mapping(target = "displayName", source = "population.name.display")
  @Mapping(target = "uuid", source = "cqlDefinition.uuid")
  CqldefinitionType populationToCqlDefinition(Population population, CQLDefinition cqlDefinition);

  // observation mappings
  @Mapping(
      target = "isInGrouping",
      expression =
          "java(String.valueOf(org.apache.commons.lang3.StringUtils.isNotBlank(observation.getDefinition())))")
  @Mapping(target = "uuid", source = "observation.id")
  @Mapping(target = "type", constant = "measureObservation")
  @Mapping(target = "displayName", constant = "Measure Observation")
  @Mapping(target = "associatedPopulationUUID", source = "associatedPopulationUUID")
  @Mapping(
      target = "cqlaggfunction",
      expression = "java(observationToCqlAggFunction(observation, cqlDefinition))")
  ClauseType observationToClauseType(
      MeasureObservation observation, CQLDefinition cqlDefinition, String associatedPopulationUUID);

  @Mapping(target = "displayName", source = "observation.aggregateMethod")
  @Mapping(
      target = "cqlfunction",
      expression = "java(observationToCqlFunction(observation, cqlDefinition))")
  CqlaggfunctionType observationToCqlAggFunction(
      MeasureObservation observation, CQLDefinition cqlDefinition);

  @Mapping(target = "displayName", source = "observation.definition")
  @Mapping(target = "uuid", source = "cqlDefinition.uuid")
  CqlfunctionType observationToCqlFunction(
      MeasureObservation observation, CQLDefinition cqlDefinition);

  // stratification mappings
  @Mapping(
      target = "isInGrouping",
      expression =
          "java(String.valueOf(org.apache.commons.lang3.StringUtils.isNotBlank(stratification.getCqlDefinition())))")
  @Mapping(target = "uuid", source = "stratification.id")
  @Mapping(target = "type", constant = "stratum")
  @Mapping(target = "displayName", constant = "Stratum")
  @Mapping(
      target = "cqldefinition",
      expression = "java(getStratificationDefinition(stratification, cqlDefinition))")
  ClauseType stratificationToClauseType(Stratification stratification, CQLDefinition cqlDefinition);

  @Mapping(target = "displayName", source = "stratification.cqlDefinition")
  @Mapping(target = "uuid", source = "cqlDefinition.uuid")
  CqldefinitionType getStratificationDefinition(
      Stratification stratification, CQLDefinition cqlDefinition);

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
      List<DefDescPair> supplementalData, Set<CQLDefinition> cqlDefinitions) {
    List<CqldefinitionType> defs =
        Optional.ofNullable(supplementalData).orElse(List.of()).stream()
            .map(
                sde -> {
                  CQLDefinition cqlDefinition =
                      getCqlDefinition(sde.getDefinition(), cqlDefinitions);
                  return defDescPairToCqldefinitionType(sde, cqlDefinition);
                })
            .toList();
    SupplementalDataElementsType supplementalDataElementsType = new SupplementalDataElementsType();
    supplementalDataElementsType.getCqldefinition().addAll(defs);
    return supplementalDataElementsType;
  }

  default ElementLookUpType elementLookupsToElementLookupType(Set<ElementLookup> elementLookups) {
    if (CollectionUtils.isEmpty(elementLookups)) {
      return null;
    }
    ElementLookUpType elementLookUp = new ElementLookUpType();
    elementLookUp.getQdm().addAll(elementLookupsToQdmTypes(elementLookups));
    return elementLookUp;
  }

  List<QdmType> elementLookupsToQdmTypes(Set<ElementLookup> elementLookups);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "uuid", source = "id")
  @Mapping(target = "code", source = "code")
  @Mapping(target = "datatype", source = "datatype")
  @Mapping(target = "originalName", source = "name")
  @Mapping(target = "suppDataElement", constant = "false")
  @Mapping(target = "version", constant = "")
  @Mapping(
      target = "codeSystemOID",
      expression =
          "java(org.apache.commons.lang3.StringUtils.replaceChars(elementLookup.getCodeSystemOID(), \"urn:oid:\",\"\"))")
  @Mapping(
      target = "codeSystemVersion",
      expression =
          "java(elementLookup.getCodeSystemVersion() == null ? \"\" : elementLookup.getCodeSystemVersion())")
  @Mapping(
      target = "isCodeSystemVersionIncluded",
      expression = "java(String.valueOf(elementLookup.getCodeSystemVersion() != null))")
  QdmType elementLookupToQdmType(ElementLookup elementLookup);

  default RiskAdjustmentVariablesType riskAdjustmentsToRiskAdjustmentVariablesType(
      List<DefDescPair> riskAdjustments, Set<CQLDefinition> cqlDefinitions) {
    List<CqldefinitionType> defs =
        Optional.ofNullable(riskAdjustments).orElse(List.of()).stream()
            .map(
                rav -> {
                  CQLDefinition cqlDefinition =
                      getCqlDefinition(rav.getDefinition(), cqlDefinitions);
                  return defDescPairToCqldefinitionType(rav, cqlDefinition);
                })
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
        : endorsementToEndorsementType(endorsements.get(0));
  }

  @Mapping(target = "id", source = "endorsementId")
  @Mapping(target = "value", source = "endorser")
  EndorsementType endorsementToEndorsementType(Endorsement endorsement);

  @Mapping(target = "value", source = "organization.name")
  @Mapping(target = "id", source = "organization.oid")
  DeveloperType organizationToDeveloperType(Organization organization);

  @Mapping(target = "displayName", source = "defDescPair.definition")
  @Mapping(target = "uuid", source = "definition.uuid")
  CqldefinitionType defDescPairToCqldefinitionType(
      DefDescPair defDescPair, CQLDefinition definition);

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

  @Mapping(target = "valuesets", source = "valueSets")
  @Mapping(target = "parameters", source = "parameters")
  @Mapping(target = "definitions", source = "definitions")
  @Mapping(target = "functions", source = "definitions")
  @Mapping(target = "includeLibrarys", source = "includeLibraries")
  CqlLookUpType cqlLookupsToCqlLookUpType(CqlLookups cqlLookups);

  default ValuesetsType valueSetsToValuesetsType(Set<CQLValueSet> cqlValueSets) {
    if (CollectionUtils.isEmpty(cqlValueSets)) {
      return null;
    }
    ValuesetsType valuesetsType = new ValuesetsType();
    valuesetsType.getValueset().addAll(valueSetsToValuesetType(cqlValueSets));
    return valuesetsType;
  }

  List<ValuesetType> valueSetsToValuesetType(Set<CQLValueSet> cqlValueSets);

  @Mapping(target = "datatype", constant = "")
  @Mapping(target = "suppDataElement", constant = "false")
  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "oid", source = "oid")
  @Mapping(target = "originalName", source = "name")
  ValuesetType cqlValueSetToValuesetType(CQLValueSet cqlValueSet);

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

  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
  @Mapping(target = "name", source = "parameterName")
  @Mapping(target = "logic", source = "parameterLogic")
  @Mapping(target = "readOnly", constant = "true")
  ParameterType cqlParameterToParameterType(CQLParameter cqlParameter);

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

  @Mapping(target = "id", source = "uuid")
  @Mapping(target = "name", source = "definitionName")
  @Mapping(target = "logic", source = "definitionLogic")
  DefinitionType cqlDefinitionToDefinitionType(CQLDefinition cqlDefinition);

  @Mapping(target = "id", source = "uuid")
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

  default IncludeLibrarysType cqlIncludeLibrariesToIncludeLibrarysType(
      Set<CQLIncludeLibrary> cqlIncludeLibraries) {
    if (!CollectionUtils.isEmpty(cqlIncludeLibraries)) {
      IncludeLibrarysType includeLibrarysType = new IncludeLibrarysType();
      includeLibrarysType
          .getIncludeLibrary()
          .addAll(cqlIncludeLibrariesToIncludeLibraryType(cqlIncludeLibraries));
      return includeLibrarysType;
    }
    return null;
  }

  List<IncludeLibraryType> cqlIncludeLibrariesToIncludeLibraryType(
      Set<CQLIncludeLibrary> cqlIncludeLibraries);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "cqlLibRefId", source = "id")
  @Mapping(target = "cqlLibRefName", source = "cqlLibraryName")
  @Mapping(target = "cqlVersion", source = "version")
  @Mapping(target = "name", source = "aliasName")
  @Mapping(target = "qdmVersion", source = "qdmVersion")
  IncludeLibraryType cQLIncludeLibraryToIncludeLibraryType(CQLIncludeLibrary cqlIncludeLibrary);

  default AllUsedCQLLibsType includeLibrariesToAllUsedCqlLibsType(
      Set<CQLIncludeLibrary> includeLibraries) {
    if (!CollectionUtils.isEmpty(includeLibraries)) {
      AllUsedCQLLibsType allUsedCQLLibsType = new AllUsedCQLLibsType();
      allUsedCQLLibsType.getLib().addAll(includeLibrariesToLibTypes(includeLibraries));
      return allUsedCQLLibsType;
    }
    return null;
  }

  List<LibType> includeLibrariesToLibTypes(Set<CQLIncludeLibrary> includeLibraries);

  @Mapping(target = "name", source = "cqlLibraryName")
  @Mapping(target = "alias", source = "aliasName")
  LibType cqlIncludeLibraryToLibType(CQLIncludeLibrary cqlIncludeLibrary);
}
