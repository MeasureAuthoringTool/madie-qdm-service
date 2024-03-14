package gov.cms.madie.services;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.measure.Group;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.PopulationType;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.model.HumanReadable;
import gov.cms.madie.model.HumanReadableCodeModel;
import gov.cms.madie.model.HumanReadableExpressionModel;
import gov.cms.madie.model.HumanReadableMeasureInformationModel;
import gov.cms.madie.model.HumanReadablePopulationCriteriaModel;
import gov.cms.madie.model.HumanReadablePopulationModel;
import gov.cms.madie.model.HumanReadableTerminologyModel;
import gov.cms.madie.model.HumanReadableValuesetModel;
//import gov.cms.mat.cql_elm_translation.dto.SourceDataCriteria;
import gov.cms.madie.util.HumanReadableDateUtil;
import gov.cms.madie.util.HumanReadableUtil;
import gov.cms.madie.dto.CQLCode;
import gov.cms.madie.dto.CQLDefinition;
import gov.cms.madie.dto.CQLValueSet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.text.Collator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
@Slf4j
public class HumanReadableService {

  private Template baseHumanReadableTemplate;

//  private final DataCriteriaService dataCriteriaService;
//  private final CqlParsingService cqlParsingService;
  private final Collator collator = Collator.getInstance(Locale.US);

  /**
   * Generates the QDM Human Readable HTML from a MADiE Measure.
   *
   * @param measure MADiE Measure
   * @return QDM Human Readable HTML
   */
  public String generate(Measure measure, CqlLookups cqlLookups) {
    StopWatch watch = new StopWatch();
    watch.start("prelim");
    collator.setStrength(Collator.PRIMARY);
    if (measure == null) {
      throw new IllegalArgumentException("Measure cannot be null.");
    }
    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());
    watch.start("generate sourceDataCriteria");



//    List<SourceDataCriteria> sourceDataCriteria =
//        dataCriteriaService.getSourceDataCriteria(measure.getCql(), accessToken);


    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());
    watch.start("generate getUsedCQLCodes");

//    Set<CQLCode> cqlCodes = dataCriteriaService.getUsedCQLCodes(measure.getCql(), accessToken);
    Set<CQLCode> cqlCodes = cqlLookups.getCodes();
//    Set<CQLDefinition> allDefinitions =
//        cqlParsingService.getAllDefinitions(measure.getCql(), accessToken);
    Set<CQLDefinition> allDefinitions = cqlLookups.getDefinitions();

    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());
    watch.start("HR model setup 1");

    HumanReadable hr =
        HumanReadable.builder()
            .measureInformation(buildMeasureInfo(measure))
            .populationCriteria(buildPopCriteria(measure, allDefinitions))
            .definitions(buildDefinitions(allDefinitions))
            .functions(buildFunctions(allDefinitions))
                .valuesetDataCriteriaList(buildValuesetDataCriteriaList(cqlLookups))
//            .valuesetDataCriteriaList(
//                buildValuesetDataCriteriaList(sourceDataCriteria, measure, accessToken))
            .codeDataCriteriaList(buildCodeDataCriteriaList(new ArrayList<>(cqlCodes)))
            .build();

    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());
    watch.start("HR model setup 2");

    hr.setValuesetAndCodeDataCriteriaList(new ArrayList<>(hr.getValuesetDataCriteriaList()));


    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());
    watch.start("HR model setup 3");

    hr.setValuesetTerminologyList(
        buildValuesetTerminologyList(
            hr.getValuesetDataCriteriaList(), cqlLookups.getValueSets(), cqlLookups.getSourceDataCriteria().stream().toList()));

    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());
    watch.start("HR model setup 4");

    hr.setCodeTerminologyList(buildCodeTerminologyList(hr.getCodeDataCriteriaList()));


    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());
    watch.start("HR model setup 5");


    hr.setSupplementalDataElements(buildSupplementalDataElements(measure, hr.getDefinitions()));


    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());
    watch.start("HR model setup 6");


    hr.setRiskAdjustmentVariables(buildRiskAdjustmentVariables(measure, hr.getDefinitions()));


    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());
    watch.start("generate HR string");

    String output = generate(hr);



    watch.stop();
    log.info("Generate for section [{}] took [{}ms]", watch.getLastTaskName(), watch.getLastTaskTimeMillis());


    return output;
  }

  private String generate(HumanReadable model) {
    Map<String, Object> paramsMap = new HashMap<>();
    paramsMap.put("model", model);
    setMeasurementPeriodForQdm(model.getMeasureInformation());
    try {
      return FreeMarkerTemplateUtils.processTemplateIntoString(
          baseHumanReadableTemplate, paramsMap);
    } catch (IOException | TemplateException e) {
      throw new IllegalStateException("Unable to process Human Readable from Measure", e);
    }
  }

  private void setMeasurementPeriodForQdm(HumanReadableMeasureInformationModel model) {
    boolean isCalendarYear = model.isCalendarYear();
    String measurementPeriodStartDate = model.getMeasurementPeriodStartDate();
    String measurementPeriodEndDate = model.getMeasurementPeriodEndDate();
    model.setMeasurementPeriod(
        HumanReadableDateUtil.getFormattedMeasurementPeriod(
            isCalendarYear, measurementPeriodStartDate, measurementPeriodEndDate));
  }

  HumanReadableMeasureInformationModel buildMeasureInfo(Measure measure) {
    // TODO Needs safety checks
    HumanReadableMeasureInformationModel modelTemp =
        HumanReadableMeasureInformationModel.builder()
            .qdmVersion(5.6) // TODO Replace hardcode
            .ecqmTitle(measure.getEcqmTitle())
            .ecqmVersionNumber(measure.getVersion().toString())
            .calendarYear(false) // Unsupported MAT feature, default to false
            .guid(measure.getMeasureSetId())
            .cbeNumber(HumanReadableUtil.getCbeNumber(measure))
            .endorsedBy(HumanReadableUtil.getEndorsedBy(measure))
            // TODO needs safety check
            .patientBased(measure.getGroups().get(0).getPopulationBasis().equals("boolean"))
            .measurementPeriodStartDate(
                DateFormat.getDateInstance().format(measure.getMeasurementPeriodStart()))
            .measurementPeriodEndDate(
                DateFormat.getDateInstance().format(measure.getMeasurementPeriodEnd()))
            .measureScoring(
                measure.getGroups().get(0).getScoring()) // All groups expected to have same scoring
            .description(measure.getMeasureMetaData().getDescription())
            .copyright(measure.getMeasureMetaData().getCopyright())
            .disclaimer(measure.getMeasureMetaData().getDisclaimer())
            .rationale(measure.getMeasureMetaData().getRationale())
            .clinicalRecommendationStatement(
                measure.getMeasureMetaData().getClinicalRecommendation())
            .measureDevelopers(HumanReadableUtil.getMeasureDevelopers(measure))
            .measureSteward(
                measure.getMeasureMetaData().getSteward() != null
                    ? measure.getMeasureMetaData().getSteward().getName()
                    : null)
            .measureTypes(HumanReadableUtil.getMeasureTypes(measure))
            .stratification(HumanReadableUtil.getStratification(measure))
            .measureObservations(HumanReadableUtil.getMeasureObservationDescriptions(measure))
            .riskAdjustment(measure.getRiskAdjustmentDescription())
            .supplementalDataElements(measure.getSupplementalDataDescription())
            .rateAggregation(((QdmMeasure) measure).getRateAggregation())
            .improvementNotation(((QdmMeasure) measure).getImprovementNotation())
            .guidance(measure.getMeasureMetaData().getGuidance())
            .transmissionFormat(measure.getMeasureMetaData().getTransmissionFormat())
            .definition(
                HumanReadableUtil.escapeHtmlString(measure.getMeasureMetaData().getDefinition()))
            .references(HumanReadableUtil.buildReferences(measure.getMeasureMetaData()))
            .measureSet(measure.getMeasureMetaData().getMeasureSetTitle())
            .build();
    generatePopulations(measure, modelTemp);
    return modelTemp;
  }

  private void generatePopulations(
      Measure measure, HumanReadableMeasureInformationModel modelTemp) {
    modelTemp.setInitialPopulation(
        HumanReadableUtil.getPopulationDescription(
            measure, PopulationType.INITIAL_POPULATION.name()));
    modelTemp.setDenominator(
        HumanReadableUtil.getPopulationDescription(measure, PopulationType.DENOMINATOR.name()));
    modelTemp.setDenominatorExclusions(
        HumanReadableUtil.getPopulationDescription(
            measure, PopulationType.DENOMINATOR_EXCLUSION.name()));
    modelTemp.setNumerator(
        HumanReadableUtil.getPopulationDescription(measure, PopulationType.NUMERATOR.name()));
    modelTemp.setNumeratorExclusions(
        HumanReadableUtil.getPopulationDescription(
            measure, PopulationType.NUMERATOR_EXCLUSION.name()));
    modelTemp.setDenominatorExceptions(
        HumanReadableUtil.getPopulationDescription(
            measure, PopulationType.DENOMINATOR_EXCEPTION.name()));
    modelTemp.setMeasurePopulation(
        HumanReadableUtil.getPopulationDescription(
            measure, PopulationType.MEASURE_POPULATION.name()));
    modelTemp.setMeasurePopulationExclusions(
        HumanReadableUtil.getPopulationDescription(
            measure, PopulationType.MEASURE_POPULATION_EXCLUSION.name()));
  }

  List<HumanReadablePopulationCriteriaModel> buildPopCriteria(
      Measure measure, Set<CQLDefinition> allDefinitions) {
    return measure.getGroups().stream()
        .map(
            group ->
                HumanReadablePopulationCriteriaModel.builder()
                    .id(group.getId())
                    .name(group.getGroupDescription())
                    .populations(
                        Stream.concat(
                                Stream.concat(
                                    buildPopulations(group, allDefinitions).stream(),
                                    buildStratification(group, allDefinitions).stream()),
                                buildMeasureObservation(group, allDefinitions).stream())
                            .toList())
                    .build())
        .collect(Collectors.toList());
  }

  List<HumanReadablePopulationModel> buildPopulations(
      Group group, Set<CQLDefinition> allDefinitions) {
    return group.getPopulations().stream()
        .map(
            population ->
                HumanReadablePopulationModel.builder()
                    .name(population.getName().name())
                    .id(population.getId())
                    .display(population.getName().getDisplay())
                    .logic(
                        HumanReadableUtil.getCQLDefinitionLogic(
                            population.getDefinition(), allDefinitions))
                    .expressionName(population.getDefinition())
                    .inGroup(!StringUtils.isBlank(population.getDefinition()))
                    .build())
        .collect(Collectors.toList());
  }

  List<HumanReadablePopulationModel> buildStratification(
      Group group, Set<CQLDefinition> allDefinitions) {
    if (!CollectionUtils.isEmpty(group.getStratifications())) {
      return group.getStratifications().stream()
          .map(
              stratification ->
                  HumanReadablePopulationModel.builder()
                      .name("Stratification")
                      .id(stratification.getId())
                      .display("Stratification")
                      .logic(
                          HumanReadableUtil.getCQLDefinitionLogic(
                              stratification.getCqlDefinition(), allDefinitions))
                      .expressionName(stratification.getCqlDefinition())
                      .inGroup(!StringUtils.isBlank(stratification.getCqlDefinition()))
                      .build())
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  List<HumanReadablePopulationModel> buildMeasureObservation(
      Group group, Set<CQLDefinition> allDefinitions) {
    if (!CollectionUtils.isEmpty(group.getMeasureObservations())) {
      return group.getMeasureObservations().stream()
          .map(
              measureObservation ->
                  HumanReadablePopulationModel.builder()
                      .name(measureObservation.getDefinition())
                      .id(measureObservation.getId())
                      .display(measureObservation.getDefinition())
                      .logic(
                          HumanReadableUtil.getCQLDefinitionLogic(
                              measureObservation.getDefinition(), allDefinitions))
                      .expressionName(measureObservation.getDefinition())
                      .inGroup(!StringUtils.isBlank(measureObservation.getDefinition()))
                      .build())
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  List<HumanReadableExpressionModel> buildDefinitions(Set<CQLDefinition> allDefinitions) {
    List<CQLDefinition> definitions =
        allDefinitions.stream()
            .filter(definition -> definition.getParentLibrary() == null && !definition.isFunction())
            .collect(Collectors.toList());

    List<HumanReadableExpressionModel> expressions =
        definitions.stream()
            .map(
                definition ->
                    HumanReadableExpressionModel.builder()
                        .id(definition.getId())
                        .name(definition.getDefinitionName())
                        .logic(
                            definition
                                .getLogic()
                                .substring(definition.getLogic().indexOf('\n') + 1))
                        .build())
            .collect(Collectors.toList());
    expressions.sort(Comparator.comparing(HumanReadableExpressionModel::getName));
    return expressions;
  }

  List<HumanReadableExpressionModel> buildFunctions(Set<CQLDefinition> allDefinitions) {
    List<CQLDefinition> functions =
        allDefinitions.stream()
            .filter(
                definition ->
                    definition.isFunction()
            )
            .toList();

    List<HumanReadableExpressionModel> expressions =
        functions.stream()
            .map(
                definition ->
                    HumanReadableExpressionModel.builder()
                        .id(definition.getId())
                        .name(definition.getDefinitionName())
                        .logic(
                            definition
                                .getLogic()
                                .substring(definition.getLogic().indexOf('\n') + 1))
                        .build())
            .collect(Collectors.toList());
    expressions.sort(Comparator.comparing(HumanReadableExpressionModel::getName));
    return expressions;
  }

//  boolean isUsedFunction(Measure measure, String accessToken, String id) {
//    Map<String, Set<String>> usedFunctions =
//        cqlParsingService.getUsedFunctions(measure.getCql(), accessToken);
//    return usedFunctions != null && !usedFunctions.isEmpty() && usedFunctions.containsKey(id);
//  }

  List<HumanReadableValuesetModel> buildValuesetDataCriteriaList(CqlLookups cqlLookups) {
    List<SourceDataCriteria> valuesetsSourceDataCriteria =
            cqlLookups.getSourceDataCriteria().stream()
                    .filter(valueset -> StringUtils.isBlank(valueset.getCodeId()))
                    .toList();
    return valuesetsSourceDataCriteria.stream()
                    .map(
                            criteria ->
                                    new HumanReadableValuesetModel(
                                            criteria.getName(),
//                                            criteria.getTitle(),
                                            criteria.getOid(),
                                            "",
                                            criteria.getDescription().split(":")[0]))
                    .distinct()
                    .sorted(Comparator.comparing(HumanReadableValuesetModel::getDataCriteriaDisplay, collator))
            .toList();
  }

  List<HumanReadableValuesetModel> buildValuesetDataCriteriaList(
      List<SourceDataCriteria> sourceDataCriteria, Set<CQLValueSet> usedValuesets) {
    List<SourceDataCriteria> valuesetsSourceDataCriteria =
        sourceDataCriteria.stream()
            .filter(
                valueset ->
                    StringUtils.isBlank(valueset.getCodeId())
                        && findUsedValueset(usedValuesets, valueset.getName()))
            .collect(Collectors.toList());
    Set<HumanReadableValuesetModel> valuesets =
        valuesetsSourceDataCriteria.stream()
            .map(
                criteria ->
                    new HumanReadableValuesetModel(
                        criteria.getTitle(),
                        criteria.getOid(),
                        "",
                        criteria.getDescription().split(":")[0]))
            .collect(Collectors.toSet());
    List<HumanReadableValuesetModel> valuesetList = new ArrayList<>(valuesets);
    valuesetList.sort(
        Comparator.comparing(HumanReadableValuesetModel::getDataCriteriaDisplay, collator));
    return valuesetList;
  }

  boolean findUsedValueset(Set<CQLValueSet> usedCqlValuesets, String id) {
    Set<String> usedValuesets = usedCqlValuesets.stream().map(CQLValueSet::getName).collect(Collectors.toSet());
    return usedValuesets != null && !usedValuesets.isEmpty() && usedValuesets.contains(id);
  }

  List<HumanReadableCodeModel> buildCodeDataCriteriaList(List<CQLCode> cqlCodes) {
    if (!CollectionUtils.isEmpty(cqlCodes)) {
      List<HumanReadableCodeModel> codeList =
          cqlCodes.stream()
              .map(
                  cqlCode ->
                      HumanReadableCodeModel.builder()
                          .name(cqlCode.getCodeName())
                          .oid(cqlCode.getId())
                          .codesystemName(cqlCode.getCodeSystemName())
                          .codesystemVersion(cqlCode.getCodeSystemVersion())
                          .isCodesystemVersionIncluded(cqlCode.isCodeSystemVersionIncluded())
                          .build())
              .collect(Collectors.toList());
      codeList.sort(Comparator.comparing(HumanReadableCodeModel::getDataCriteriaDisplay, collator));
      return codeList;
    }
    return new ArrayList<>();
  }

  List<HumanReadableTerminologyModel> buildValuesetTerminologyList(
      List<HumanReadableValuesetModel> valuesetModel,
      Set<CQLValueSet> usedValuesets,
      List<SourceDataCriteria> sourceDataCriteria) {
    List<HumanReadableTerminologyModel> valuesetList = new ArrayList<>(valuesetModel);

    Set<HumanReadableValuesetModel> usedValueSets =
        findUsedCQLValueSet(usedValuesets, sourceDataCriteria);
    if (!usedValueSets.isEmpty()) {
      valuesetList.addAll(usedValueSets);
    }

    valuesetList.sort(
        Comparator.comparing(HumanReadableTerminologyModel::getTerminologyDisplay, collator));
    return valuesetList;
  }

  Set<HumanReadableValuesetModel> findUsedCQLValueSet(
          Set<CQLValueSet> usedValuesets, List<SourceDataCriteria> sourceDataCriteria) {
//    Set<CQLValueSet> usedValuesets =
//        dataCriteriaService.getUsedCQLValuesets(measure.getCql(), accessToken);

    List<CQLValueSet> otherValueSets =
        usedValuesets.stream()
            .filter(
                vs ->
                    sourceDataCriteria.stream()
                        .noneMatch(sdc -> sdc.getOid().equalsIgnoreCase(vs.getOid())))
            .toList();
    Set<HumanReadableValuesetModel> valuesets =
        otherValueSets.stream()
            .map(vs -> new HumanReadableValuesetModel(vs.getName(), vs.getOid(), "", vs.getName()))
            .collect(Collectors.toSet());
    return valuesets;
  }

  List<HumanReadableTerminologyModel> buildCodeTerminologyList(
      List<HumanReadableCodeModel> codeModel) {
    List<HumanReadableTerminologyModel> codeList = new ArrayList<>(codeModel);
    codeList.sort(
        Comparator.comparing(HumanReadableTerminologyModel::getTerminologyDisplay, collator));
    return codeList;
  }

  List<HumanReadableExpressionModel> buildSupplementalDataElements(
      Measure measure, List<HumanReadableExpressionModel> definitions) {
    if (!CollectionUtils.isEmpty(measure.getSupplementalData())) {
      return measure.getSupplementalData().stream()
          .map(
              supplementalData ->
                  HumanReadableExpressionModel.builder()
                      .id(UUID.randomUUID().toString())
                      .name(supplementalData.getDefinition())
                      .logic(
                          HumanReadableUtil.getLogic(supplementalData.getDefinition(), definitions))
                      .build())
          .collect(Collectors.toList());
    }
    return null;
  }

  List<HumanReadableExpressionModel> buildRiskAdjustmentVariables(
      Measure measure, List<HumanReadableExpressionModel> definitions) {
    if (!CollectionUtils.isEmpty(measure.getRiskAdjustments())) {
      return measure.getRiskAdjustments().stream()
          .map(
              riskAdjustment ->
                  HumanReadableExpressionModel.builder()
                      .id(UUID.randomUUID().toString())
                      .name(riskAdjustment.getDefinition())
                      .logic(
                          "["
                              + HumanReadableUtil.getLogic(
                                      riskAdjustment.getDefinition(), definitions)
                                  .trim()
                              + "]")
                      .build())
          .collect(Collectors.toList());
    }
    return null;
  }
}
