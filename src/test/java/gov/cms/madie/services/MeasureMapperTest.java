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
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.common.Organization;
import gov.cms.madie.models.common.Version;
import gov.cms.madie.models.measure.*;
import gov.cms.madie.util.MadieConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class MeasureMapperTest {

  MeasureMapperImpl measureMapper;

  @BeforeEach()
  void setup() {
    measureMapper = new MeasureMapperImpl();
  }

  @Test
  void testMeasureToMeasureTypeNull() {
    MeasureType output = measureMapper.measureToMeasureType(null, null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testMeasureToMeasureType() {
    QdmMeasure measure =
        QdmMeasure.builder()
            .measureName("Measure1")
            .cqlLibraryName("MeasureLib1")
            .ecqmTitle("MLib1")
            .model(ModelType.QDM_5_6.getValue())
            .rateAggregation("Rate Aggr")
            .improvementNotation("Improvement Notation")
            .supplementalData(
                List.of(
                    DefDescPair.builder().definition("sde1").description("first SDE").build(),
                    DefDescPair.builder().definition("sde2").description("second SDE").build()))
            .measureMetaData(
                MeasureMetaData.builder()
                    .description("Measure Description")
                    .rationale("the rationale")
                    .clinicalRecommendation("the clinical recommendation")
                    .copyright("the copyright")
                    .developers(
                        List.of(
                            Organization.builder()
                                .id("org1Id")
                                .oid("111.222.333")
                                .name("SB")
                                .build()))
                    .steward(
                        Organization.builder().id("org2Id").oid("222.333.444").name("ICF").build())
                    .references(
                        List.of(
                            Reference.builder()
                                .referenceType("CITATION")
                                .referenceText("This is a citation")
                                .build(),
                            Reference.builder()
                                .referenceType("DOCUMENTATION")
                                .referenceText("This is documentation")
                                .build()))
                    .build())
            .build();

    CqlLookups cqlLookups =
        CqlLookups.builder()
            .definitions(
                Set.of(
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .uuid(UUID.randomUUID().toString())
                        .definitionName("sde1")
                        .build(),
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .uuid(UUID.randomUUID().toString())
                        .definitionName("sde2")
                        .build()))
            .build();

    MeasureType output = measureMapper.measureToMeasureType(measure, cqlLookups);
    assertThat(output, is(notNullValue()));
    assertThat(output.getMeasureDetails(), is(notNullValue()));
    assertThat(
        output.getMeasureDetails().getAggregation(), is(equalTo(measure.getRateAggregation())));
    assertThat(
        output.getMeasureDetails().getImprovementNotations(),
        is(equalTo(measure.getImprovementNotation())));
    assertThat(
        output.getMeasureDetails().getDescription(),
        is(equalTo(measure.getMeasureMetaData().getDescription())));
    assertThat(
        output.getMeasureDetails().getRationale(),
        is(equalTo(measure.getMeasureMetaData().getRationale())));
    assertThat(
        output.getMeasureDetails().getRecommendations(),
        is(equalTo(measure.getMeasureMetaData().getClinicalRecommendation())));
    assertThat(
        output.getMeasureDetails().getCopyright(),
        is(equalTo(measure.getMeasureMetaData().getCopyright())));
    assertThat(output.getMeasureDetails().getReferences(), is(notNullValue()));
    assertThat(output.getMeasureDetails().getReferences().getReference(), is(notNullValue()));
    assertThat(output.getMeasureDetails().getReferences().getReference().size(), is(equalTo(2)));
    assertThat(
        output.getMeasureDetails().getReferences().getReference().get(0).getReferenceType(),
        is(equalTo("CITATION")));
    assertThat(
        output.getMeasureDetails().getReferences().getReference().get(0).getReferenceText(),
        is(equalTo("This is a citation")));
    assertThat(output.getSupplementalDataElements(), is(notNullValue()));
    assertThat(output.getSupplementalDataElements().getCqldefinition(), is(notNullValue()));
    assertThat(output.getSupplementalDataElements().getCqldefinition().size(), is(equalTo(2)));
    assertThat(
        output.getSupplementalDataElements().getCqldefinition().get(0).getUuid(),
        is(notNullValue()));
    assertThat(
        output.getSupplementalDataElements().getCqldefinition().get(0).getUuid(),
        is(notNullValue()));
  }

  @Test
  void testMeasureToMeasureDetailsTypeNull() {
    MeasureDetailsType output = measureMapper.measureToMeasureDetailsType(null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void measureToMeasureDetailsType() {
    QdmMeasure measure =
        QdmMeasure.builder()
            .measureName("Measure1")
            .cqlLibraryName("MeasureLib1")
            .ecqmTitle("MLib1")
            .model(ModelType.QDM_5_6.getValue())
            .rateAggregation("Rate Aggr")
            .improvementNotation("Improvement Notation")
            .measureMetaData(
                MeasureMetaData.builder()
                    .description("Measure Description")
                    .rationale("the rationale")
                    .clinicalRecommendation("the clinical recommendation")
                    .copyright("the copyright")
                    .developers(
                        List.of(
                            Organization.builder()
                                .id("org1Id")
                                .oid("111.222.333")
                                .name("SB")
                                .build()))
                    .steward(
                        Organization.builder().id("org2Id").oid("222.333.444").name("ICF").build())
                    .endorsements(List.of(Endorsement.builder().endorsementId("cbe13").build()))
                    .build())
            .measureSet(MeasureSet.builder().cmsId(144).build())
            .build();
    MeasureDetailsType output = measureMapper.measureToMeasureDetailsType(measure);
    assertThat(output, is(notNullValue()));
    assertThat(output.getAggregation(), is(equalTo(measure.getRateAggregation())));
    assertThat(output.getImprovementNotations(), is(equalTo(measure.getImprovementNotation())));
    assertThat(output.getDescription(), is(equalTo(measure.getMeasureMetaData().getDescription())));
    assertThat(output.getRationale(), is(equalTo(measure.getMeasureMetaData().getRationale())));
    assertThat(
        output.getRecommendations(),
        is(equalTo(measure.getMeasureMetaData().getClinicalRecommendation())));
    assertThat(output.getCopyright(), is(equalTo(measure.getMeasureMetaData().getCopyright())));
    assertThat(output.getEmeasureid(), is(equalTo("144")));
    assertThat(output.getCbeid(), is(equalTo("cbe13")));
  }

  @Test
  void testMeasureToMeasureGroupingTypeReturnsNullForNullMeasure() {
    MeasureGroupingType output =
        measureMapper.measureToMeasureGroupingType(null, CqlLookups.builder().build());
    assertThat(output, is(nullValue()));
  }

  @Test
  void testMeasureToMeasureGroupingTypeReturnsNullForNullLookups() {
    MeasureGroupingType output =
        measureMapper.measureToMeasureGroupingType(QdmMeasure.builder().build(), null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testMeasureToMeasureGroupingTypeReturnsNullForNullGroupsInput() {
    QdmMeasure measure = new QdmMeasure();
    measure.setGroups(null);
    MeasureGroupingType output =
        measureMapper.measureToMeasureGroupingType(measure, CqlLookups.builder().build());
    assertThat(output, is(nullValue()));
  }

  @Test
  void testMeasureToMeasureGroupingTypeReturnsNullForEmptyGroupsInput() {
    QdmMeasure measure = QdmMeasure.builder().groups(List.of()).build();
    MeasureGroupingType output =
        measureMapper.measureToMeasureGroupingType(measure, CqlLookups.builder().build());
    assertThat(output, is(nullValue()));
  }

  @Test
  void testMeasureToMeasureGroupingTypeReturnsNullForSingleGroupInput() {
    QdmMeasure measure =
        QdmMeasure.builder()
            .groups(
                List.of(
                    Group.builder()
                        .scoring(MeasureScoring.PROPORTION.toString())
                        .scoringUnit(
                            Map.of(
                                "label",
                                "m/s",
                                "value",
                                Map.of(
                                    "code",
                                    "m/s",
                                    "name",
                                    "",
                                    "system",
                                    "https://clinicaltables.nlm.nih.gov/")))
                        .populations(
                            List.of(
                                Population.builder()
                                    .id("id1")
                                    .name(PopulationType.INITIAL_POPULATION)
                                    .definition("ipp")
                                    .description("the IP description")
                                    .build(),
                                Population.builder()
                                    .id("id2")
                                    .name(PopulationType.DENOMINATOR)
                                    .definition("denom")
                                    .description("the denom description")
                                    .build(),
                                Population.builder()
                                    .id("id90")
                                    .name(PopulationType.DENOMINATOR_EXCLUSION)
                                    .definition("")
                                    .description("")
                                    .build(),
                                Population.builder()
                                    .id("id3")
                                    .name(PopulationType.NUMERATOR)
                                    .definition("numer")
                                    .description("the numer description")
                                    .build(),
                                Population.builder()
                                    .id("id91")
                                    .name(PopulationType.NUMERATOR_EXCLUSION)
                                    .definition("")
                                    .description("")
                                    .build(),
                                Population.builder()
                                    .id("id92")
                                    .name(PopulationType.DENOMINATOR_EXCEPTION)
                                    .definition("")
                                    .description("")
                                    .build()))
                        .stratifications(
                            List.of(
                                Stratification.builder()
                                    .id("id4")
                                    .cqlDefinition("ipp")
                                    .description("stratum1 desc")
                                    .build(),
                                Stratification.builder()
                                    .id("id4")
                                    .cqlDefinition("denom")
                                    .description("stratum2 desc")
                                    .build()))
                        .build()))
            .build();
    CqlLookups cqlLookups =
        CqlLookups.builder()
            .definitions(
                Set.of(
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("ipp")
                        .build(),
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("denom")
                        .build(),
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("numer")
                        .build()))
            .build();
    MeasureGroupingType output = measureMapper.measureToMeasureGroupingType(measure, cqlLookups);
    assertThat(output, is(notNullValue()));
    assertThat(output.getGroup(), is(notNullValue()));
    assertThat(output.getGroup().size(), is(equalTo(1)));
    GroupType firstGroup = output.getGroup().get(0);
    assertThat(firstGroup, is(notNullValue()));
    assertThat(firstGroup.getSequence(), is(equalTo("1")));
    assertThat(firstGroup.getUcum(), is(equalTo("m/s")));
    assertThat(firstGroup.getClause(), is(notNullValue()));
    assertThat(firstGroup.getClause().size(), is(equalTo(8)));
    assertThat(firstGroup.getClause().get(0).getIsInGrouping(), is(equalTo("true")));
    assertThat(firstGroup.getClause().get(0).getDisplayName(), is(equalTo("Initial Population")));
    assertThat(firstGroup.getClause().get(1).getIsInGrouping(), is(equalTo("true")));
    assertThat(firstGroup.getClause().get(1).getDisplayName(), is(equalTo("Denominator")));
    assertThat(firstGroup.getClause().get(2).getIsInGrouping(), is(equalTo("false")));
    assertThat(
        firstGroup.getClause().get(2).getDisplayName(), is(equalTo("Denominator Exclusion")));
    assertThat(firstGroup.getClause().get(6).getIsInGrouping(), is(equalTo("true")));
    assertThat(firstGroup.getClause().get(6).getDisplayName(), is(equalTo("Stratum")));
    assertThat(firstGroup.getClause().get(6).getType(), is(equalTo("stratum")));
  }

  @Test
  void testScoringToScoringTypeNull() {
    ScoringType output = measureMapper.scoringToScoringType(null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testScoringToScoringType() {
    ScoringType output = measureMapper.scoringToScoringType(MeasureScoring.PROPORTION.toString());
    assertThat(output, is(notNullValue()));
    assertThat(output.getValue(), is(equalTo(MeasureScoring.PROPORTION.toString())));
    assertThat(output.getId(), is(equalTo(MadieConstants.Scoring.PROPORTION_ABBREVIATION)));
  }

  @Test
  void supplementalDataToSupplementalData() {}

  @Test
  void supplementalDataToSupplementalDataElementsType() {}

  @Test
  void riskAdjustmentsToRiskAdjustmentVariablesType() {}

  @Test
  void testVersionToVersionNull() {
    String output = measureMapper.versionToVersion(null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testVersionToVersion() {
    String output =
        measureMapper.versionToVersion(
            Version.builder().major(2).minor(1).revisionNumber(3).build());
    assertThat(output, is(notNullValue()));
    assertThat(output, is(equalTo("2.1.003")));
  }

  @Test
  void testMeasureToPeriodType() {
    Date startDate = new Date(1676818800000L); // 2/19/2023 15:00 GMT
    Date endDate = new Date(1708354800000L); // 2/19/2024 15:00 GMT
    QdmMeasure measure =
        QdmMeasure.builder()
            .measurementPeriodStart(startDate)
            .measurementPeriodEnd(endDate)
            .build();
    PeriodType output = measureMapper.measureToPeriodType(measure);
    assertThat(output, is(notNullValue()));
    assertThat(output.getCalenderYear(), is(equalTo("false")));
    assertThat(output.getStartDate(), is(equalTo("20230219")));
    assertThat(output.getStopDate(), is(equalTo("20240219")));
    assertThat(output.getUuid(), is(notNullValue()));
  }

  @Test
  void testOrganizationToStewardTypeNull() {
    StewardType output = measureMapper.organizationToStewardType(null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testOrganizationToStewardType() {
    StewardType output =
        measureMapper.organizationToStewardType(
            Organization.builder().oid("111.222.333").name("ICF").build());
    assertThat(output, is(notNullValue()));
    assertThat(output.getId(), is(equalTo("111.222.333")));
    assertThat(output.getValue(), is(equalTo("ICF")));
  }

  @Test
  void testMeasureMetaDataToDevelopersTypeNull() {
    DevelopersType output = measureMapper.measureMetaDataToDevelopersType(null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testMeasureMetaDataToDevelopersTypeEmptyDevelopers() {
    MeasureMetaData metaData = MeasureMetaData.builder().build();
    DevelopersType output = measureMapper.measureMetaDataToDevelopersType(metaData);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testMeasureMetaDataToDevelopersType() {
    MeasureMetaData metaData =
        MeasureMetaData.builder()
            .developers(
                List.of(
                    Organization.builder().oid("555.666.777").name("SB").build(),
                    Organization.builder().oid("999.888.777").name("ICF").build()))
            .build();
    DevelopersType output = measureMapper.measureMetaDataToDevelopersType(metaData);
    assertThat(output, is(notNullValue()));
    assertThat(output.getDeveloper(), is(notNullValue()));
    assertThat(output.getDeveloper().size(), is(equalTo(2)));
    assertThat(output.getDeveloper().get(0).getId(), is(equalTo("555.666.777")));
    assertThat(output.getDeveloper().get(0).getValue(), is(equalTo("SB")));
    assertThat(output.getDeveloper().get(1).getId(), is(equalTo("999.888.777")));
    assertThat(output.getDeveloper().get(1).getValue(), is(equalTo("ICF")));
  }

  @Test
  void testEndorsementsToCbeidReturnsNullForNullInput() {
    String output = measureMapper.endorsementsToCbeid(null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testEndorsementsToCbeidReturnsNullForEmptyInput() {
    String output = measureMapper.endorsementsToCbeid(List.of());
    assertThat(output, is(nullValue()));
  }

  @Test
  void testEndorsementsToCbeidReturnsNullForEmptyEndorsementId() {
    String output =
        measureMapper.endorsementsToCbeid(
            List.of(Endorsement.builder().endorsementId(null).build()));
    assertThat(output, is(nullValue()));
  }

  @Test
  void testEndorsementsToCbeidReturnsEndorsementId() {
    String output =
        measureMapper.endorsementsToCbeid(
            List.of(
                Endorsement.builder()
                    .endorser("ICF")
                    .endorserSystemId("123")
                    .endorsementId("110402B")
                    .build()));
    assertThat(output, is(equalTo("110402B")));
  }

  @Test
  void testEndorsementsToEndorsementTypeReturnsNullForNullInput() {
    EndorsementType output = measureMapper.endorsementsToEndorsementType(null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testEndorsementsToEndorsementTypeReturnsNullForEmptyInput() {
    EndorsementType output = measureMapper.endorsementsToEndorsementType(List.of());
    assertThat(output, is(nullValue()));
  }

  @Test
  void testEndorsementsToEndorsementTypeReturnsNullForEmptyEndorsementId() {
    EndorsementType output =
        measureMapper.endorsementsToEndorsementType(
            List.of(Endorsement.builder().endorsementId(null).build()));
    assertThat(output, is(nullValue()));
  }

  @Test
  void testEndorsementsToEndorsementTypeReturnsEndorsemenType() {
    EndorsementType output =
        measureMapper.endorsementsToEndorsementType(
            List.of(
                Endorsement.builder()
                    .endorser("ICF")
                    .endorserSystemId("123")
                    .endorsementId("110402B")
                    .build()));
    assertThat(output, is(notNullValue()));
    assertThat(output.getId(), is(equalTo("110402B")));
    assertThat(output.getValue(), is(equalTo("ICF")));
  }

  @Test
  void organizationToEndorsementType() {}

  @Test
  void organizationToDeveloperType() {}

  @Test
  void defDescPairToCqldefinitionType() {}

  @Test
  void testBaseConfigurationTypesToTypesTypesNull() {
    TypesType output = measureMapper.baseConfigurationTypesToTypesTypes(null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testBaseConfigurationTypesToTypesTypesEmpty() {
    TypesType output = measureMapper.baseConfigurationTypesToTypesTypes(List.of());
    assertThat(output, is(nullValue()));
  }

  @Test
  void testBaseConfigurationTypesToTypesTypes() {
    TypesType output =
        measureMapper.baseConfigurationTypesToTypesTypes(
            List.of(BaseConfigurationTypes.PERFORMANCE, BaseConfigurationTypes.OUTCOME));
    assertThat(output, is(notNullValue()));
    assertThat(output.getType(), is(notNullValue()));
    assertThat(
        output.getType().get(0).getValue(), is(BaseConfigurationTypes.PERFORMANCE.toString()));
    assertThat(output.getType().get(0).getId(), is(MadieConstants.MeasureType.PERFORMANCE));
    assertThat(output.getType().get(1).getValue(), is(BaseConfigurationTypes.OUTCOME.toString()));
    assertThat(output.getType().get(1).getId(), is(MadieConstants.MeasureType.OUTCOME));
  }

  @Test
  void baseConfigurationTypeToTypesType() {}

  @Test
  void referencesToReferencesType() {}

  @Test
  void referenceToReferenceType() {}

  @Test
  void testInstantToFinalizedDateTypeNullMetaData() {
    Measure measure = Measure.builder().measureMetaData(null).build();
    FinalizedDateType output = measureMapper.instantToFinalizedDateType(measure);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testInstantToFinalizedDateTypeDraft() {
    Measure measure =
        Measure.builder().measureMetaData(MeasureMetaData.builder().draft(true).build()).build();
    FinalizedDateType output = measureMapper.instantToFinalizedDateType(measure);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testInstantToFinalizedDateNullLastModified() {
    Measure measure =
        Measure.builder()
            .measureMetaData(MeasureMetaData.builder().draft(false).build())
            .lastModifiedAt(null)
            .build();
    FinalizedDateType output = measureMapper.instantToFinalizedDateType(measure);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testInstantToFinalizedDate() {
    Measure measure =
        Measure.builder()
            .measureMetaData(MeasureMetaData.builder().draft(false).build())
            .lastModifiedAt(Instant.ofEpochSecond(1708358014))
            .build();
    FinalizedDateType output = measureMapper.instantToFinalizedDateType(measure);
    assertThat(output, is(notNullValue()));
    assertThat(output.getValueAttribute(), is(equalTo("20240219")));
  }

  @Test
  void testScoringUnitToUcumReturnsScoringUnit() {
    Object scoringUnit = Map.of("value", Map.of("code", "m/s", "url", "http://blah"));
    String output = measureMapper.scoringUnitToUcum(scoringUnit);
    assertThat(output, is(equalTo("m/s")));
  }

  @Test
  void testScoringUnitToUcumReturnsNullForNullInput() {
    Object scoringUnit = null;
    String output = measureMapper.scoringUnitToUcum(scoringUnit);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testScoringUnitToUcumReturnsNullForEmptyStringInput() {
    Object scoringUnit = "";
    String output = measureMapper.scoringUnitToUcum(scoringUnit);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testScoringUnitToUcumReturnsNullForMapMissingValue() {
    Object scoringUnit = Map.of("otherData", Map.of("not", "real"));
    String output = measureMapper.scoringUnitToUcum(scoringUnit);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testScoringUnitToUcumReturnsNullForMapMissing() {
    Object scoringUnit = Map.of("otherData", Map.of("not", "real"));
    String output = measureMapper.scoringUnitToUcum(scoringUnit);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testGroupToClauseTypesForCvGroup() {
    Group group =
        Group.builder()
            .scoring(MeasureScoring.CONTINUOUS_VARIABLE.toString())
            .populations(
                List.of(
                    Population.builder()
                        .id("id1")
                        .name(PopulationType.INITIAL_POPULATION)
                        .definition("Initial Population")
                        .description("the IP description")
                        .build(),
                    Population.builder()
                        .id("id2")
                        .name(PopulationType.MEASURE_POPULATION)
                        .definition("Measure Population")
                        .description("the denom description")
                        .build(),
                    Population.builder()
                        .id("id3")
                        .name(PopulationType.MEASURE_POPULATION_EXCLUSION)
                        .definition("")
                        .description("")
                        .build()))
            .measureObservations(
                List.of(
                    MeasureObservation.builder()
                        .id("obs1")
                        .definition("Measure Observations")
                        .criteriaReference("id2")
                        .aggregateMethod("Average")
                        .build()))
            .build();

    CqlLookups cqlLookups =
        CqlLookups.builder()
            .definitions(
                Set.of(
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("Initial Population")
                        .build(),
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("Measure Population")
                        .build(),
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("Measure Observations")
                        .build()))
            .build();

    List<ClauseType> clauses = measureMapper.groupToClauseTypes(group, cqlLookups);
    assertThat(clauses.size(), is(equalTo(4)));
    ClauseType observationType = clauses.get(3);
    assertThat(observationType.getType(), is(equalTo("measureObservation")));
    assertThat(observationType.getAssociatedPopulationUUID(), is(equalTo("")));
    assertThat(observationType.getCqlaggfunction(), is(notNullValue()));
  }

  @Test
  void testGroupToClauseTypesForRatioGroup() {
    Population denominator =
        Population.builder()
            .id("id2")
            .name(PopulationType.DENOMINATOR)
            .definition("Denominator")
            .description("the denom description")
            .build();
    Population numerator =
        Population.builder()
            .id("id3")
            .name(PopulationType.NUMERATOR)
            .definition("Numerator")
            .description("")
            .build();
    Group group =
        Group.builder()
            .scoring(MeasureScoring.RATIO.toString())
            .populations(
                List.of(
                    Population.builder()
                        .id("id1")
                        .name(PopulationType.INITIAL_POPULATION)
                        .definition("Initial Population")
                        .description("the IP description")
                        .build(),
                    denominator,
                    numerator))
            .measureObservations(
                List.of(
                    MeasureObservation.builder()
                        .id("obs1")
                        .definition("Denominator Observations")
                        .criteriaReference("id2")
                        .aggregateMethod("Average")
                        .build(),
                    MeasureObservation.builder()
                        .id("obs1")
                        .definition("Numerator Observations")
                        .criteriaReference("id3")
                        .aggregateMethod("Average")
                        .build()))
            .build();

    CqlLookups cqlLookups =
        CqlLookups.builder()
            .definitions(
                Set.of(
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("Initial Population")
                        .build(),
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("Denominator")
                        .build(),
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("Numerator")
                        .build(),
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("Denominator Observations")
                        .build(),
                    CQLDefinition.builder()
                        .id(UUID.randomUUID().toString())
                        .definitionName("Numerator Observations")
                        .build()))
            .build();

    List<ClauseType> clauses = measureMapper.groupToClauseTypes(group, cqlLookups);
    assertThat(clauses.size(), is(equalTo(5)));
    ClauseType denomObservationType = clauses.get(3);
    assertThat(denomObservationType.getType(), is(equalTo("measureObservation")));
    assertThat(
        denomObservationType.getAssociatedPopulationUUID(), is(equalTo(denominator.getId())));

    ClauseType numerObservationType = clauses.get(4);
    assertThat(numerObservationType.getType(), is(equalTo("measureObservation")));
    assertThat(numerObservationType.getAssociatedPopulationUUID(), is(equalTo(numerator.getId())));
  }

  @Test
  void testElementLookupsToElementLookupTypeForNullInput() {
    assertThat(measureMapper.elementLookupsToElementLookupType(null), is(nullValue()));
  }

  @Test
  void testElementLookupsToElementLookupTypeForEmptyInput() {
    assertThat(measureMapper.elementLookupsToElementLookupType(Set.of()), is(nullValue()));
  }

  @Test
  void testElementLookupsToElementLookupType() {
    ElementLookup lookup1 =
        ElementLookup.builder()
            .code(true)
            .id("7d87e17439ea")
            .name("digoxin 0.125 MG Oral Tablet")
            .oid("197604")
            .taxonomy("RXNORM")
            .codeName("digoxin 0.125 MG Oral Tablet")
            .codeSystemOID("urn:oid:2.16.840.1.113883.6.88")
            .build();
    ElementLookup lookup2 =
        ElementLookup.builder()
            .code(true)
            .datatype("Medication, Order")
            .id("ed773b5f11d8")
            .name("1 ML digoxin 0.1 MG/ML Injection")
            .oid("204504")
            .taxonomy("RXNORM")
            .codeName("1 ML digoxin 0.1 MG/ML Injection")
            .codeSystemOID("urn:oid:2.16.840.1.113883.6.88")
            .build();
    ElementLookup lookup3 =
        ElementLookup.builder()
            .code(false)
            .datatype("Medication, Order")
            .id("3e1ef08235c2")
            .name("Digoxin Medications")
            .oid("2.16.840.1.113883.3.464.1003.1065")
            .taxonomy("Grouping")
            .build();
    ElementLookUpType elementLookUpType =
        measureMapper.elementLookupsToElementLookupType(Set.of(lookup1, lookup2, lookup3));
    List<QdmType> qdmTypes = elementLookUpType.getQdm();
    assertThat(qdmTypes.size(), is(equalTo(3)));
    QdmType type1 =
        qdmTypes.stream()
            .filter(qdmType -> Objects.equals(qdmType.getOid(), lookup1.getOid()))
            .findFirst()
            .orElse(null);
    assertThat(type1, is(notNullValue()));
    assertThat(type1.getCode(), is(equalTo("true")));
    assertThat(type1.getDatatype(), is(nullValue()));
    assertThat(type1.getOid(), is(equalTo(lookup1.getOid())));
    assertThat(type1.getCodeName(), is(equalTo(lookup1.getCodeName())));
    assertThat(type1.getIsCodeSystemVersionIncluded(), is(equalTo("false")));

    QdmType type2 =
        qdmTypes.stream()
            .filter(qdmType -> Objects.equals(qdmType.getOid(), lookup2.getOid()))
            .findFirst()
            .orElse(null);

    assertThat(type2, is(notNullValue()));
    assertThat(type2.getCode(), is(equalTo(String.valueOf(lookup2.isCode()))));
    assertThat(type2.getDatatype(), is(equalTo("Medication, Order")));
    assertThat(type2.getOid(), is(equalTo(lookup2.getOid())));
    assertThat(type2.getName(), is(equalTo(lookup2.getName())));
    assertThat(type2.getIsCodeSystemVersionIncluded(), is(equalTo("false")));
    assertThat(type2.getTaxonomy(), is(equalTo(lookup2.getTaxonomy())));
  }

  @Test
  void testFunctionArgumentsToArgumentsTypeForNullInput() {
    assertThat(measureMapper.functionArgumentsToArgumentsType(null), is(nullValue()));
  }

  @Test
  void testFunctionArgumentsToArgumentsTypeForEmptyInput() {
    assertThat(measureMapper.functionArgumentsToArgumentsType(List.of()), is(nullValue()));
  }

  @Test
  void testFunctionArgumentsToArgumentsType() {
    CQLFunctionArgument arg1 =
        CQLFunctionArgument.builder()
            .id("51e3a1669fad")
            .argumentName("QualifyingEncounter")
            .argumentType("QDM Datatype")
            .qdmDataType("Encounter, Performed")
            .build();
    ArgumentsType argumentsTypes = measureMapper.functionArgumentsToArgumentsType(List.of(arg1));
    assertThat(argumentsTypes, is(notNullValue()));
    assertThat(argumentsTypes.getArgument().size(), is(equalTo(1)));
    ArgumentType argumentsType = argumentsTypes.getArgument().get(0);
    assertThat(argumentsType.getType(), is(equalTo(arg1.getArgumentType())));
    assertThat(argumentsType.getQdmDataType(), is(equalTo(arg1.getQdmDataType())));
  }

  @Test
  void testCqlIncludeLibrariesToIncludeLibrariesTypeForEmptyInput() {
    assertThat(measureMapper.cqlIncludeLibrariesToIncludeLibrarysType(Set.of()), is(nullValue()));
  }

  @Test
  void testCqlIncludeLibrariesToIncludeLibrariesTypeForNullInput() {
    assertThat(measureMapper.cqlIncludeLibrariesToIncludeLibrarysType(null), is(nullValue()));
  }

  @Test
  void testCqlIncludeLibrariesToIncludeLibrariesType() {
    CQLIncludeLibrary includeLibrary =
        CQLIncludeLibrary.builder()
            .aliasName("Commons")
            .cqlLibraryName("MADiECommonFunctions")
            .version("1.0.000")
            .qdmVersion("5.6")
            .build();
    IncludeLibrarysType librariesType =
        measureMapper.cqlIncludeLibrariesToIncludeLibrarysType(Set.of(includeLibrary));
    assertThat(librariesType, is(notNullValue()));
    IncludeLibraryType includeLibraryType = librariesType.getIncludeLibrary().get(0);
    assertThat(includeLibraryType.getName(), is(equalTo(includeLibrary.getAliasName())));
    assertThat(
        includeLibraryType.getCqlLibRefName(), is(equalTo(includeLibrary.getCqlLibraryName())));
    assertThat(includeLibraryType.getCqlLibRefId(), is(equalTo(includeLibrary.getId())));
  }

  @Test
  void testValueSetsToValueSetsTypeForNullInput() {
    assertThat(measureMapper.valueSetsToValuesetsType(null), is(nullValue()));
  }

  @Test
  void testValueSetsToValueSetsType() {
    CQLValueSet cqlValueSet =
        CQLValueSet.builder()
            .name("Bilateral Mastectomy")
            .oid("2.16.840.1.113883.3.464.1003.198.12.1005")
            .build();
    ValuesetsType valueSetsType = measureMapper.valueSetsToValuesetsType(Set.of(cqlValueSet));
    assertThat(valueSetsType, is(notNullValue()));
    assertThat(valueSetsType.getValueset().size(), is(equalTo(1)));
    ValuesetType valueSetType = valueSetsType.getValueset().get(0);
    assertThat(valueSetType.getName(), is(equalTo(cqlValueSet.getName())));
    assertThat(valueSetType.getOid(), is(equalTo(cqlValueSet.getOid())));
  }

  @Test
  void testCqlCodeSystemsToCodeSystemsTypeNullInput() {
    assertThat(measureMapper.cqlCodeSystemsToCodeSystemsType(null), is(nullValue()));
  }

  @Test
  void testCqlCodeSystemsToCodeSystemsType() {
    CQLCodeSystem cqlCodeSystem =
        CQLCodeSystem.builder()
            .codeSystem("2.16.840.1.113883.6.96")
            .codeSystemName("SNOMEDCT")
            .codeSystemVersion("2023-01-12")
            .build();
    CodeSystemsType codeSystemsType =
        measureMapper.cqlCodeSystemsToCodeSystemsType(Set.of(cqlCodeSystem));
    assertThat(codeSystemsType, is(notNullValue()));
    assertThat(codeSystemsType.getCodeSystem().size(), is(equalTo(1)));
    CodeSystemType codeSystemType = codeSystemsType.getCodeSystem().get(0);
    assertThat(codeSystemType.getCodeSystem(), is(equalTo(cqlCodeSystem.getCodeSystem())));
    assertThat(codeSystemType.getCodeSystemName(), is(equalTo(cqlCodeSystem.getCodeSystemName())));
    assertThat(
        codeSystemType.getCodeSystemVersion(), is(equalTo(cqlCodeSystem.getCodeSystemVersion())));
  }

  @Test
  void testCqlCodesToCodesTypeForNullInput() {
    assertThat(measureMapper.cqlCodesToCodesType(null), is(nullValue()));
  }

  @Test
  void testCqlCodesToCodesType() {
    CQLCode cqlCode =
        CQLCode.builder().id("225337009").codeSystemOID("2.16.840.1.113883.6.96").build();
    CodesType codesType = measureMapper.cqlCodesToCodesType(Set.of(cqlCode));
    assertThat(codesType, is(notNullValue()));
    assertThat(codesType.getCode().size(), is(equalTo(1)));
    CodeType codeType = codesType.getCode().get(0);
    assertThat(codeType.getCodeOID(), is(equalTo(cqlCode.getId())));
    assertThat(codeType.getCodeSystemOID(), is(equalTo(cqlCode.getCodeSystemOID())));
    assertThat(codeType.getIsCodeSystemVersionIncluded(), is(equalTo("false")));
  }

  @Test
  void testParametersTypeForNullInput() {
    assertThat(measureMapper.cqlParametersToParametersType(null), is(nullValue()));
  }

  @Test
  void testParametersTypeFor() {
    CQLParameter cqlParameter =
        CQLParameter.builder()
            .parameterName("Measurement Period")
            .parameterLogic("interval<DateTime>")
            .build();
    ParametersType parametersType =
        measureMapper.cqlParametersToParametersType(Set.of(cqlParameter));
    assertThat(parametersType, is(notNullValue()));
    assertThat(parametersType.getParameter().size(), is(equalTo(1)));
    ParameterType parameterType = parametersType.getParameter().get(0);
    assertThat(parameterType.getName(), is(equalTo(cqlParameter.getParameterName())));
    assertThat(parameterType.getLogic(), is(equalTo(cqlParameter.getParameterLogic())));
  }

  @Test
  void testCqlDefinitionsToFunctionsTypeForNullInput() {
    assertThat(measureMapper.cqlDefinitionsToFunctionsType(null), is(nullValue()));
  }

  @Test
  void testCqlDefinitionsToFunctionsType() {
    CQLDefinition cqlDefinition1 =
        CQLDefinition.builder()
            .id(UUID.randomUUID().toString())
            .uuid(UUID.randomUUID().toString())
            .definitionName("Measure Population")
            .isFunction(false)
            .build();
    CQLFunctionArgument arg1 =
        CQLFunctionArgument.builder()
            .id("51e3a1669fad")
            .argumentName("QualifyingEncounter")
            .argumentType("QDM Datatype")
            .qdmDataType("Encounter, Performed")
            .build();
    CQLDefinition cqlDefinition2 =
        CQLDefinition.builder()
            .id(UUID.randomUUID().toString())
            .uuid(UUID.randomUUID().toString())
            .definitionName("Measure Observation")
            .functionArguments(List.of(arg1))
            .definitionLogic("duration in days of QualifyingEncounter.relevantPeriod")
            .isFunction(true)
            .build();
    Set<CQLDefinition> cqlDefinitions = Set.of(cqlDefinition1, cqlDefinition2);
    FunctionsType functionsType = measureMapper.cqlDefinitionsToFunctionsType(cqlDefinitions);
    assertThat(functionsType, is(notNullValue()));
    assertThat(functionsType.getFunction().size(), is(equalTo(1)));
    FunctionType functionType = functionsType.getFunction().get(0);
    assertThat(functionType.getName(), is(equalTo(cqlDefinition2.getDefinitionName())));
    assertThat(functionType.getLogic(), is(equalTo(cqlDefinition2.getDefinitionLogic())));
    assertThat(functionType.getArguments().getArgument().size(), is(equalTo(1)));
    List<ArgumentType> argumentTypes = functionType.getArguments().getArgument();
    assertThat(argumentTypes.get(0).getQdmDataType(), is(equalTo(arg1.getQdmDataType())));
    assertThat(argumentTypes.get(0).getArgumentName(), is(equalTo(arg1.getArgumentName())));
  }

  @Test
  void testGetImprovementNotationsForOther() {
    String customNotation = "Custom improvement notation";
    QdmMeasure measure =
        QdmMeasure.builder()
            .improvementNotation("Other")
            .improvementNotationOther(customNotation)
            .build();
    assertThat(measureMapper.getImprovementNotations(measure), is(equalTo(customNotation)));
  }

  @Test
  void testGetImprovementNotationsForIncreaseScore() {
    String notation = "Increased score indicates improvement";
    QdmMeasure measure =
        QdmMeasure.builder().improvementNotation(notation).improvementNotationOther("").build();
    assertThat(measureMapper.getImprovementNotations(measure), is(equalTo(notation)));
  }
}
