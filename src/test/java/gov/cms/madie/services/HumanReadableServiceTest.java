package gov.cms.madie.services;

import freemarker.template.Template;
import gov.cms.madie.dto.CQLFunctionArgument;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.dto.ElementLookup;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.common.Organization;
import gov.cms.madie.models.common.Version;
import gov.cms.madie.models.measure.*;
import gov.cms.madie.model.HumanReadableCodeModel;
import gov.cms.madie.model.HumanReadableExpressionModel;
import gov.cms.madie.model.HumanReadableMeasureInformationModel;
import gov.cms.madie.model.HumanReadablePopulationCriteriaModel;
import gov.cms.madie.model.HumanReadablePopulationModel;
import gov.cms.madie.model.HumanReadableTerminologyModel;
import gov.cms.madie.model.HumanReadableValuesetModel;
import gov.cms.madie.dto.CQLCode;
import gov.cms.madie.dto.CQLDefinition;
import gov.cms.madie.dto.CQLValueSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.text.Collator;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class HumanReadableServiceTest {
  @InjectMocks HumanReadableService humanReadableService;

  @Mock Template template;
  @Mock Collator collator;

  private QdmMeasure measure;
  private final Date now = new Date();
  private Set<CQLDefinition> allDefinitions;
  private Set<CQLDefinition> onlyDefinitions;
  private CQLDefinition function1;
  private CQLDefinition function2;

  @BeforeEach
  void setUp() {
    measure =
        QdmMeasure.builder()
            .id("1234")
            .model(ModelType.QDM_5_6.getValue())
            .cql("test CQL")
            .cqlLibraryName("Measure1Lib")
            .measureSetId("7890")
            .versionId("56")
            .measureName("measure1")
            .ecqmTitle("ecqm-title")
            .version(Version.parse("0.0.000"))
            .measurementPeriodStart(now)
            .measurementPeriodEnd(now)
            .measureSet(MeasureSet.builder().cmsId(88).build())
            .measureMetaData(
                MeasureMetaData.builder()
                    .draft(true)
                    .clinicalRecommendation("clinical recommendation")
                    .copyright("copyright")
                    .description("a decent description")
                    .disclaimer("a disclaiming disclaimer")
                    .guidance("a helpful guidance")
                    .rationale("here's why we do")
                    .developers(
                        List.of(
                            Organization.builder().name("org1").build(),
                            Organization.builder().name("org2").build()))
                    .references(
                        List.of(
                            Reference.builder()
                                .id("ref-123")
                                .referenceType("Citation")
                                .referenceText("Example Citation Reference Text")
                                .build(),
                            Reference.builder()
                                .id("ref-xyz")
                                .referenceType("Justification")
                                .referenceText("Example < Justification Reference Text")
                                .build()))
                    .steward(Organization.builder().name("stewardOrg").build())
                    .definition("test definition")
                    .endorsements(
                        List.of(
                            Endorsement.builder()
                                .endorsementId("test endorsement id")
                                .endorser("test endorser")
                                .build()))
                    .measureSetTitle("test measure title")
                    .build())
            .groups(
                List.of(
                    Group.builder()
                        .populationBasis("boolean")
                        .scoring("proportion")
                        .populations(
                            List.of(
                                Population.builder()
                                    .id("p1")
                                    .name(PopulationType.INITIAL_POPULATION)
                                    .definition("Initial Population")
                                    .build(),
                                Population.builder()
                                    .id("p2")
                                    .name(PopulationType.DENOMINATOR)
                                    .build()))
                        .stratifications(
                            List.of(
                                Stratification.builder()
                                    .id("testStrat1Id")
                                    .cqlDefinition(PopulationType.INITIAL_POPULATION.name())
                                    .build(),
                                Stratification.builder()
                                    .id("testStrat2Id")
                                    .cqlDefinition("")
                                    .build()))
                        .measureObservations(
                            List.of(
                                MeasureObservation.builder()
                                    .definition("Local Function")
                                    .aggregateMethod("Average")
                                    .criteriaReference("p1")
                                    .build()))
                        .build()))
            .baseConfigurationTypes(List.of(BaseConfigurationTypes.OUTCOME))
            .riskAdjustmentDescription("test risk adjustment")
            .supplementalDataDescription("test supplemental data elements description")
            .rateAggregation("test rate aggregation")
            .improvementNotation("test improvment notation")
            .supplementalData(
                List.of(
                    DefDescPair.builder()
                        .definition("SDE Ethnicity")
                        .description("test SDE Ethnicity")
                        .build()))
            .riskAdjustments(
                List.of(
                    DefDescPair.builder()
                        .definition("SDE Ethnicity")
                        .description("test SDE Ethnicity")
                        .build()))
            .build();

    CQLDefinition definition1 =
        CQLDefinition.builder()
            .id("Initial Population")
            .definitionName("Initial Population")
            .parentLibrary(null)
            .definitionLogic(
                "define \"Initial Population\":\n  \"Encounter with Opioid Administration Outside of Operating Room\"")
            .build();
    CQLDefinition definition2 =
        CQLDefinition.builder()
            .id("Opioid Administration")
            .definitionName("Opioid Administration")
            .definitionLogic(
                "define \"Opioid Administration\":\n  [\"Medication, Administered\": \"Opioids, All\"]")
            .parentLibrary(null)
            .build();
    CQLDefinition definition3 =
        CQLDefinition.builder()
            .id("SDE Ethnicity")
            .definitionName("SDE Ethnicity")
            .definitionLogic(
                "define \"SDE Ethnicity\":\n  [\"Patient Characteristic Ethnicity\": \"Ethnicity\"]")
            .parentLibrary(null)
            .build();
    CQLDefinition definition4 =
        CQLDefinition.builder()
            .id("MATGlobalCommonFunctionsQDM-1.0.000|Global|Fake")
            .definitionName("Fake")
            .definitionLogic(
                "define \"Fake\":\n  [\"Patient Characteristic Ethnicity\": \"Ethnicity\"]")
            .parentLibrary("MATGlobalCommonFunctionsQDM")
            .libraryDisplayName("Global")
            .build();
    function1 =
        CQLDefinition.builder()
            .id("MATGlobalCommonFunctionsQDM-1.0.000|Global|NormalizeInterval")
            .definitionName("NormalizeInterval")
            .definitionLogic(
                "define function \"NormalizeInterval\"(pointInTime DateTime, period Interval<DateTime> ):\n  if pointInTime is not null then Interval[pointInTime, pointInTime]\n    else if period is not null then period \n    else null as Interval<DateTime>")
            .parentLibrary("MATGlobalCommonFunctionsQDM")
            .libraryDisplayName("Global")
            .functionArguments(
                List.of(
                    CQLFunctionArgument.builder()
                        .argumentName("pointInTime")
                        .argumentType("DateTime")
                        .build(),
                    CQLFunctionArgument.builder()
                        .argumentName("period")
                        .argumentType("Others")
                        .otherType("Interval<DateTime>")
                        .build()))
            .isFunction(true)
            .build();

    function2 =
        CQLDefinition.builder()
            .id("Local Function")
            .definitionName("Local Function")
            .definitionLogic(
                "define function \"Local Function\"(MedDispense \"Medication, Dispensed\"):\n  date from Coalesce(MedDispense.relevantPeriod.low, MedDispense.relevantDatetime, MedDispense.authorDatetime)")
            .isFunction(true)
            .functionArguments(
                List.of(
                    CQLFunctionArgument.builder()
                        .argumentName("MedDispense")
                        .argumentType("QDM Datatype")
                        .qdmDataType("Medication, Dispensed")
                        .build()))
            .build();
    onlyDefinitions =
        new HashSet<>(Arrays.asList(definition1, definition2, definition3, definition4));
    allDefinitions =
        new HashSet<>(
            Arrays.asList(
                definition1, definition2, function1, function2, definition3, definition4));
  }

  @Test
  public void generateHumanReadableThrowsIllegalArgumentException() {
    CqlLookups cqlLookups = CqlLookups.builder().build();
    assertThrows(
        IllegalArgumentException.class, () -> humanReadableService.generate(null, cqlLookups));
  }

  // result is an empty string, Mocking Template doesn't yield expected results.
  @Test
  public void generateHumanReadableSuccessfully() {
    Set<CQLDefinition> defs =
        Set.of(
            CQLDefinition.builder()
                .id("Def1_ID")
                .definitionName("ip1")
                .definitionLogic("exists [\"Encounter, Performed\": \"Encounter Inpatient\"]")
                .parentLibrary("Measure1Lib")
                .build(),
            CQLDefinition.builder()
                .id("Def2_ID")
                .definitionName("denom")
                .definitionLogic("ip1")
                .build(),
            CQLDefinition.builder()
                .id("Def3_ID")
                .definitionName("numer")
                .definitionLogic(
                    "[\"Encounter, Performed\"] E where E.relevantPeriod starts during day of \"Measurement Period\"")
                .build());
    CqlLookups cqlLookups = CqlLookups.builder().definitions(allDefinitions).build();
    var result = humanReadableService.generate(measure, cqlLookups);
    assertNotNull(result);
  }

  @Test
  void testGetCmsIdReturnsNullForNullMeasure() {
    var output = humanReadableService.getCmsId(null);
    assertThat(output, is(nullValue()));
  }

  @Test
  void testGetCmsIdReturnsNullForNullMeasureSet() {
    var output = humanReadableService.getCmsId(Measure.builder().measureSet(null).build());
    assertThat(output, is(nullValue()));
  }

  @Test
  void testGetCmsIdReturnsNullForNullMeasureSetCmsId() {
    var output =
        humanReadableService.getCmsId(
            Measure.builder().measureSet(MeasureSet.builder().cmsId(null).build()).build());
    assertThat(output, is(nullValue()));
  }

  @Test
  void testGetCmsIdReturnsNullForZeroMeasureSetCmsId() {
    var output =
        humanReadableService.getCmsId(
            Measure.builder().measureSet(MeasureSet.builder().cmsId(0).build()).build());
    assertThat(output, is(nullValue()));
  }

  @Test
  public void canBuildMeasureInfoFromMeasure() {
    HumanReadableMeasureInformationModel measureInfoModel =
        humanReadableService.buildMeasureInfo(measure);

    assertThat(measureInfoModel.getQdmVersion(), equalTo(5.6));
    assertThat(measureInfoModel.getEcqmTitle(), equalTo(measure.getMeasureName()));
    assertThat(
        measureInfoModel.getEcqmVersionNumber(),
        equalTo("Draft based on " + measure.getVersion().toString()));
    assertThat(measureInfoModel.getCmsId(), is(equalTo("88")));
    assertThat(measureInfoModel.isCalendarYear(), equalTo(false));
    assertThat(measureInfoModel.getGuid(), equalTo(measure.getMeasureSetId()));
    assertThat(
        measureInfoModel.isPatientBased(),
        equalTo(measure.getGroups().get(0).getPopulationBasis().equals("boolean")));
    assertThat(
        measureInfoModel.getMeasurementPeriodStartDate(),
        equalTo(DateFormat.getDateInstance().format(measure.getMeasurementPeriodStart())));
    assertThat(
        measureInfoModel.getMeasurementPeriodEndDate(),
        equalTo(DateFormat.getDateInstance().format(measure.getMeasurementPeriodEnd())));
    assertThat(
        measureInfoModel.getMeasureScoring(),
        equalTo(
            measure.getGroups().get(0).getScoring())); // All groups expected to have same scoring;
    assertThat(
        measureInfoModel.getDescription(), equalTo(measure.getMeasureMetaData().getDescription()));
    assertThat(
        measureInfoModel.getCopyright(), equalTo(measure.getMeasureMetaData().getCopyright()));
    assertThat(
        measureInfoModel.getDisclaimer(), equalTo(measure.getMeasureMetaData().getDisclaimer()));
    assertThat(
        measureInfoModel.getRationale(), equalTo(measure.getMeasureMetaData().getRationale()));
    assertThat(
        measureInfoModel.getClinicalRecommendationStatement(),
        equalTo(measure.getMeasureMetaData().getClinicalRecommendation()));
    assertThat(
        measureInfoModel.getMeasureDevelopers().size(),
        equalTo(measure.getMeasureMetaData().getDevelopers().size()));
    assertThat(
        measureInfoModel.getMeasureSteward(),
        equalTo(measure.getMeasureMetaData().getSteward().getName()));
    assertThat(
        measureInfoModel.getRiskAdjustment(), equalTo(measure.getRiskAdjustmentDescription()));
    assertThat(
        measureInfoModel.getSupplementalDataElements(),
        equalTo(measure.getSupplementalDataDescription()));
    assertThat(measureInfoModel.getRateAggregation(), equalTo(measure.getRateAggregation()));
    assertThat(
        measureInfoModel.getImprovementNotation(), equalTo(measure.getImprovementNotation()));
    assertThat(
        measureInfoModel.getReferences().size(),
        equalTo(measure.getMeasureMetaData().getReferences().size()));
    assertThat(
        measureInfoModel.getDefinition(), equalTo(measure.getMeasureMetaData().getDefinition()));
    assertThat(measureInfoModel.getGuidance(), equalTo(measure.getMeasureMetaData().getGuidance()));
    assertThat(
        measureInfoModel.getCbeNumber(),
        equalTo(measure.getMeasureMetaData().getEndorsements().get(0).getEndorsementId()));
    assertThat(
        measureInfoModel.getEndorsedBy(),
        equalTo(measure.getMeasureMetaData().getEndorsements().get(0).getEndorser()));
    assertThat(
        measureInfoModel.getMeasureSet(),
        equalTo(measure.getMeasureMetaData().getMeasureSetTitle()));
  }

  @Test
  public void testBuildMeasureInfoSomeMeasureMetaDataNull() {
    measure.getMeasureMetaData().setSteward(null);
    measure.getMeasureMetaData().setGuidance(null);
    HumanReadableMeasureInformationModel measureInfoModel =
        humanReadableService.buildMeasureInfo(measure);
    assertNull(measureInfoModel.getMeasureSteward());
    assertNull(measureInfoModel.getGuidance());

    assertThat(
        measureInfoModel.getDefinition(), equalTo(measure.getMeasureMetaData().getDefinition()));
    assertThat(
        measureInfoModel.getReferences().get(0),
        is(equalTo(measure.getMeasureMetaData().getReferences().get(0))));
    // assertNotEquals as the "<" will be escaped and replaced by &lt
    assertThat(
        measureInfoModel.getReferences().get(1),
        is(not(equalTo(measure.getMeasureMetaData().getReferences().get(1)))));
  }

  @Test
  public void testBuildMeasureInfoWhenReferenceIsNull() {
    measure.getMeasureMetaData().setReferences(null);
    HumanReadableMeasureInformationModel measureInfoModel =
        humanReadableService.buildMeasureInfo(measure);
    assertNull(measureInfoModel.getReferences());
  }

  @Test
  public void testBuildMeasureInfoWhenReferenceTextIsEmpty() {
    measure.getMeasureMetaData().getReferences().get(0).setReferenceText("");
    HumanReadableMeasureInformationModel measureInfoModel =
        humanReadableService.buildMeasureInfo(measure);
    assertThat(measureInfoModel.getReferences().get(0).getReferenceText(), is(equalTo("")));
  }

  @Test
  public void testBuildMeasureInfoWhenGroupIsEmpty() {
    measure.setGroups(Collections.emptyList());
    HumanReadableMeasureInformationModel measureInfoModel =
        humanReadableService.buildMeasureInfo(measure);
    assertThat(measureInfoModel.getMeasureScoring(), is(equalTo("")));
  }

  @Test
  public void canBuildPopulationCriteriaModelFromMeasure() {

    List<HumanReadablePopulationCriteriaModel> populationCriteriaModels =
        humanReadableService.buildPopCriteria(measure, allDefinitions);
    assertThat(populationCriteriaModels.size(), is(equalTo(1)));

    Group group = measure.getGroups().get(0);
    HumanReadablePopulationCriteriaModel popCriteriaModel = populationCriteriaModels.get(0);
    assertThat(popCriteriaModel.getName(), is(equalTo("Population Criteria 1")));
    assertThat(
        popCriteriaModel.getPopulations().size(),
        is(
            group.getPopulations().size()
                + group.getStratifications().size()
                + group.getMeasureObservations().size()));

    Population measurePopulation = group.getPopulations().get(0);
    HumanReadablePopulationModel populationModel = popCriteriaModel.getPopulations().get(0);
    assertThat(populationModel.getDisplay(), is(measurePopulation.getName().getDisplay()));
    assertThat(
        populationModel.getLogic(),
        is(equalTo("  \"Encounter with Opioid Administration Outside of Operating Room\"")));
    assertThat(populationModel.getExpressionName(), is(equalTo(measurePopulation.getDefinition())));
  }

  @Test
  public void testBuildDefinitions() {
    List<HumanReadableExpressionModel> definitions =
        humanReadableService.buildDefinitions(onlyDefinitions);
    assertThat(definitions.size(), is(equalTo(4)));
  }

  @Test
  public void testBuildDefinitionsWithFunctionsRemoved() {
    List<HumanReadableExpressionModel> definitions =
        humanReadableService.buildDefinitions(allDefinitions);
    assertThat(definitions.size(), is(equalTo(4)));
    assertThat(definitions.get(0).getName(), is(equalTo("Global.Fake")));
    assertThat(definitions.get(1).getName(), is(equalTo("Initial Population")));
    assertThat(definitions.get(2).getName(), is(equalTo("Opioid Administration")));
    assertThat(definitions.get(3).getName(), is(equalTo("SDE Ethnicity")));
  }

  @Test
  public void testBuildFunctions() {
    List<HumanReadableExpressionModel> functions =
        humanReadableService.buildFunctions(allDefinitions);
    assertThat(functions.size(), is(equalTo(2)));
    assertThat(functions.get(0), is(notNullValue()));
    assertThat(
        functions.get(0).getName(),
        is(equalTo("Global.NormalizeInterval(pointInTime DateTime, period Interval<DateTime>)")));
    assertThat(
        functions.get(1).getName(),
        is(equalTo("Local Function(MedDispense \"Medication, Dispensed\")")));
  }

  @Test
  public void testBuildFunctionsNotFound() {
    List<HumanReadableExpressionModel> functions =
        humanReadableService.buildFunctions(onlyDefinitions);
    assertThat(functions.size(), is(equalTo(0)));
  }

  @Test
  public void testBuildSupplementalDataElements() {
    List<HumanReadableExpressionModel> definitions =
        humanReadableService.buildDefinitions(allDefinitions);
    List<HumanReadableExpressionModel> supplementalData =
        humanReadableService.buildSupplementalDataElements(measure, definitions);
    assertThat(supplementalData.size(), is(equalTo(1)));
  }

  @Test
  public void testBuildSupplementalDataElementsNull() {
    List<HumanReadableExpressionModel> definitions =
        humanReadableService.buildDefinitions(allDefinitions);
    measure.setSupplementalData(null);
    List<HumanReadableExpressionModel> supplementalData =
        humanReadableService.buildSupplementalDataElements(measure, definitions);
    assertNull(supplementalData);
  }

  @Test
  public void testBuildRiskAdjustmentVariablesNull() {
    List<HumanReadableExpressionModel> definitions =
        humanReadableService.buildDefinitions(allDefinitions);
    measure.setRiskAdjustments(null);
    List<HumanReadableExpressionModel> riskAdjustment =
        humanReadableService.buildRiskAdjustmentVariables(measure, definitions);
    assertNull(riskAdjustment);
  }

  @Test
  public void testBuildCodeDataCriteriaList() {
    CqlLookups cqlLookups =
        CqlLookups.builder()
            .elementLookups(
                Set.of(
                    ElementLookup.builder()
                        .code(true)
                        .datatype("TestDT")
                        .codeName("Delivery date Estimated")
                        .oid("111.222.33.44.555")
                        .codeSystemName("LOINC")
                        .build()))
            .build();
    List<HumanReadableCodeModel> result =
        humanReadableService.buildCodeDataCriteriaList(cqlLookups);
    assertThat(result.size(), is(equalTo(1)));
  }

  @Test
  public void testBuildCodeDataCriteriaListNull() {
    CqlLookups cqlLookups = CqlLookups.builder().build();
    List<HumanReadableCodeModel> result =
        humanReadableService.buildCodeDataCriteriaList(cqlLookups);
    assertThat(result.size(), is(equalTo(0)));
  }

  @Test
  public void testBuildValueSetTerminologyList() {
    CQLValueSet cqlValueSet1 =
        CQLValueSet.builder()
            .name("Opioid Antagonist")
            .oid("2.16.840.1.113762.1.4.1248.119")
            .build();
    CQLValueSet cqlValueSet2 =
        CQLValueSet.builder()
            .name("Routes of Administration for Opioid Antagonists")
            .oid("2.16.840.1.113762.1.4.1248.187")
            .build();

    List<HumanReadableTerminologyModel> terminologyModels =
        humanReadableService.buildValueSetTerminology(Set.of(cqlValueSet1, cqlValueSet2));
    assertThat(terminologyModels.size(), is(equalTo(2)));
    assertThat(terminologyModels.get(0).getName(), is(equalTo("Opioid Antagonist")));
    assertThat(
        terminologyModels.get(1).getName(),
        is(equalTo("Routes of Administration for Opioid Antagonists")));
  }

  @Test
  public void testbuildStratificationWithNoStratifications() {
    Group group = measure.getGroups().get(0);
    group.setStratifications(null);
    List<HumanReadablePopulationModel> model =
        humanReadableService.buildStratification(group, allDefinitions);
    assertThat(CollectionUtils.isEmpty(model), is(true));
  }

  @Test
  public void testBuildMeasureObservationWithoutMeasureObservations() {
    Group group = measure.getGroups().get(0);
    group.setMeasureObservations(null);
    List<HumanReadablePopulationModel> model =
        humanReadableService.buildMeasureObservation(group, allDefinitions);
    assertThat(CollectionUtils.isEmpty(model), is(true));
  }

  @Test
  public void testBuildValueSetDataCriteriaList() {
    CqlLookups cqlLookups =
        CqlLookups.builder()
            .elementLookups(
                Set.of(
                    ElementLookup.builder()
                        .code(false)
                        .datatype("TestDT")
                        .codeName("Delivery date Estimated")
                        .oid("111.222.33.44.555")
                        .codeSystemName("LOINC")
                        .build()))
            .build();
    List<HumanReadableValuesetModel> result =
        humanReadableService.buildValueSetDataCriteriaList(cqlLookups);
    assertThat(result.size(), is(equalTo(1)));
  }

  @Test
  public void testBuildValueSetDataCriteriaListNull() {
    CqlLookups cqlLookups = CqlLookups.builder().build();
    List<HumanReadableValuesetModel> result =
        humanReadableService.buildValueSetDataCriteriaList(cqlLookups);
    assertThat(result.size(), is(equalTo(0)));
  }

  @Test
  public void testBuildCodeTerminologyList() {
    CQLCode cqlCode1 =
        CQLCode.builder()
            .codeName("Opioid Antagonist")
            .id("2.16.840.1.113762.1.4.1248.119")
            .codeSystemName("Opioid Antagonist")
            .build();
    CQLCode cqlCode2 =
        CQLCode.builder()
            .codeName("Routes of Administration for Opioid Antagonists")
            .id("2.16.840.1.113762.1.4.1248.187")
            .codeSystemName("Routes of Administration for Opioid Antagonists")
            .build();

    List<HumanReadableTerminologyModel> terminologyModels =
        humanReadableService.buildCodeTerminology(Set.of(cqlCode1, cqlCode2));
    assertThat(terminologyModels.size(), is(equalTo(2)));
    assertThat(terminologyModels.get(0).getName(), is(equalTo("Opioid Antagonist")));
    assertThat(
        terminologyModels.get(1).getName(),
        is(equalTo("Routes of Administration for Opioid Antagonists")));
  }

  @Test
  public void testBuildCodeTerminologyListEmpty() {
    List<HumanReadableTerminologyModel> terminologyModels =
        humanReadableService.buildCodeTerminology(Collections.emptySet());
    assertThat(terminologyModels.size(), is(equalTo(0)));
  }

  @Test
  public void generateHumanReadableSuccessfullyCqlLookupsNull() {
    var result = humanReadableService.generate(measure, null);
    assertNotNull(result);
  }

  @Test
  public void testBuildMeasureObservationForRatio() {
    Group group = measure.getGroups().get(0);
    group.setScoring("Ratio");
    List<HumanReadablePopulationModel> model =
        humanReadableService.buildMeasureObservation(group, allDefinitions);
    assertThat(CollectionUtils.isEmpty(model), is(false));
  }

  @Test
  public void testBuildMeasureObservationForRatioNoAssociation() {
    Group group = measure.getGroups().get(0);
    group.setScoring("Ratio");
    group.getMeasureObservations().get(0).setCriteriaReference(null);
    List<HumanReadablePopulationModel> model =
        humanReadableService.buildMeasureObservation(group, allDefinitions);
    assertThat(CollectionUtils.isEmpty(model), is(false));
    assertTrue(model.get(0).getDisplay().contains("None"));
  }

  @Test
  public void testBuildMeasureObservationForRatioAssociatedPopulationHasNoName() {
    Group group = measure.getGroups().get(0);
    group.setScoring("Ratio");

    group.getPopulations().get(0).setName(null);

    List<HumanReadablePopulationModel> model =
        humanReadableService.buildMeasureObservation(group, allDefinitions);
    assertThat(CollectionUtils.isEmpty(model), is(false));
    assertTrue(model.get(0).getDisplay().contains("None"));
  }

  @Test
  public void testBuildPopulationsNull() {
    Group group = measure.getGroups().get(0);
    group.setPopulations(null);
    List<HumanReadablePopulationModel> model =
        humanReadableService.buildPopulations(group, allDefinitions);
    assertThat(CollectionUtils.isEmpty(model), is(true));
  }
}
