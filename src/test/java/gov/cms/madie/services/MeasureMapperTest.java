package gov.cms.madie.services;

import generated.gov.cms.madie.simplexml.DevelopersType;
import generated.gov.cms.madie.simplexml.FinalizedDateType;
import generated.gov.cms.madie.simplexml.MeasureDetailsType;
import generated.gov.cms.madie.simplexml.MeasureType;
import generated.gov.cms.madie.simplexml.PeriodType;
import generated.gov.cms.madie.simplexml.ScoringType;
import generated.gov.cms.madie.simplexml.StewardType;
import generated.gov.cms.madie.simplexml.TypesType;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.common.Organization;
import gov.cms.madie.models.common.Version;
import gov.cms.madie.models.measure.BaseConfigurationTypes;
import gov.cms.madie.models.measure.DefDescPair;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.MeasureMetaData;
import gov.cms.madie.models.measure.MeasureScoring;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.models.measure.Reference;
import gov.cms.madie.util.MadieConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    MeasureType output = measureMapper.measureToMeasureType(null);
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
                    DefDescPair.builder().description("sde1").description("first SDE").build(),
                    DefDescPair.builder().description("sde2").description("second SDE").build()))
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
    MeasureType output = measureMapper.measureToMeasureType(measure);
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
                    .build())
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
  void endorsementsToCbeid() {}

  @Test
  void endorsementsToEndorsementType() {}

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
}
