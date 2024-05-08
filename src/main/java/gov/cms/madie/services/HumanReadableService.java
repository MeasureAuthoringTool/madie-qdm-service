package gov.cms.madie.services;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.cms.madie.dto.CQLCode;
import gov.cms.madie.dto.CQLDefinition;
import gov.cms.madie.dto.CQLValueSet;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.model.HumanReadable;
import gov.cms.madie.model.HumanReadableCodeModel;
import gov.cms.madie.model.HumanReadableExpressionModel;
import gov.cms.madie.model.HumanReadableMeasureInformationModel;
import gov.cms.madie.model.HumanReadablePopulationCriteriaModel;
import gov.cms.madie.model.HumanReadablePopulationModel;
import gov.cms.madie.model.HumanReadableTerminologyModel;
import gov.cms.madie.model.HumanReadableValuesetModel;
import gov.cms.madie.models.measure.Group;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.Population;
import gov.cms.madie.models.measure.PopulationType;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.util.HumanReadableDateUtil;
import gov.cms.madie.util.HumanReadableUtil;
import gov.cms.madie.util.MeasureUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.Collator;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
@Slf4j
public class HumanReadableService {

  private Template baseHumanReadableTemplate;
  private final Collator collator = Collator.getInstance(Locale.US);

  /**
   * Generates the QDM Human Readable HTML from a MADiE Measure.
   *
   * @param measure MADiE Measure
   * @param cqlLookups Object holding information from the translator about the CQL
   * @return QDM Human Readable HTML
   */
  public String generate(Measure measure, CqlLookups cqlLookups) {
    collator.setStrength(Collator.PRIMARY);
    if (measure == null) {
      throw new IllegalArgumentException("Measure cannot be null.");
    }
    if (cqlLookups == null) {
      cqlLookups = CqlLookups.builder().build();
    }
    Set<CQLDefinition> allDefinitions = cqlLookups.getDefinitions();
    if (allDefinitions == null) {
      allDefinitions = new HashSet<>();
    }

    HumanReadable hr =
        HumanReadable.builder()
            .measureInformation(buildMeasureInfo(measure))
            .populationCriteria(buildPopCriteria(measure, allDefinitions))
            .definitions(buildDefinitions(allDefinitions))
            .functions(buildFunctions(allDefinitions))
            // TODO: can we combine these two?
            .valuesetDataCriteriaList(buildValueSetDataCriteriaList(cqlLookups))
            .codeDataCriteriaList(buildCodeDataCriteriaList(cqlLookups))
            .build();

    // value sets criteria
    hr.setValuesetAndCodeDataCriteriaList(new ArrayList<>(hr.getValuesetDataCriteriaList()));
    // code criteria
    hr.getValuesetAndCodeDataCriteriaList().addAll(new ArrayList<>(hr.getCodeDataCriteriaList()));

    // terminology
    hr.setValuesetTerminologyList(buildValueSetTerminology(cqlLookups.getValueSets()));
    hr.setCodeTerminologyList(buildCodeTerminology(cqlLookups.getCodes()));

    // SDE & RAV
    hr.setSupplementalDataElements(buildSupplementalDataElements(measure, hr.getDefinitions()));
    hr.setRiskAdjustmentVariables(buildRiskAdjustmentVariables(measure, hr.getDefinitions()));

    return generate(hr);
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

  String getCmsId(Measure measure) {
    return measure == null
            || measure.getMeasureSet() == null
            || measure.getMeasureSet().getCmsId() == null
            || measure.getMeasureSet().getCmsId() == 0
        ? null
        : measure.getMeasureSet().getCmsId().toString();
  }

  HumanReadableMeasureInformationModel buildMeasureInfo(Measure measure) {
    boolean patientBased = false;
    String measureScoring = "";
    measure.setGroups(null);
    // edge case for testers
    if (measure.getGroups() == null) {
      Group emptyGroup = Group.builder().populationBasis("").build();
      List<Group> groupList = new ArrayList<>();
      groupList.add(emptyGroup);
      measure.setGroups(groupList);
    }
    if (!CollectionUtils.isEmpty(measure.getGroups())) {
      patientBased = measure.getGroups().get(0).getPopulationBasis().equals("boolean");
      measureScoring = measure.getGroups().get(0).getScoring();
    }
    HumanReadableMeasureInformationModel modelTemp =
        HumanReadableMeasureInformationModel.builder()
            .qdmVersion(5.6) // TODO Replace hardcode
            .ecqmTitle(measure.getMeasureName())
            .cmsId(getCmsId(measure))
            .ecqmVersionNumber(measure.getVersion().toString())
            .calendarYear(false) // Unsupported MAT feature, default to false
            .guid(measure.getMeasureSetId())
            .cbeNumber(HumanReadableUtil.getCbeNumber(measure))
            .endorsedBy(HumanReadableUtil.getEndorsedBy(measure))
            // TODO needs safety check
            .patientBased(patientBased)
            .measurementPeriodStartDate(
                DateFormat.getDateInstance().format(measure.getMeasurementPeriodStart()))
            .measurementPeriodEndDate(
                DateFormat.getDateInstance().format(measure.getMeasurementPeriodEnd()))
            .measureScoring(measureScoring) // All groups expected to have same scoring
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
            .improvementNotation(MeasureUtils.getImprovementNotation((QdmMeasure) measure))
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
    AtomicInteger indexHolder = new AtomicInteger(1);
    return measure.getGroups().stream()
        .map(
            group ->
                HumanReadablePopulationCriteriaModel.builder()
                    .id(group.getId())
                    .name("Population Criteria " + indexHolder.getAndIncrement())
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

    return (group.getPopulations() != null)
        ? group.getPopulations().stream()
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
            .collect(Collectors.toList())
        : Collections.emptyList();
  }

  List<HumanReadablePopulationModel> buildStratification(
      Group group, Set<CQLDefinition> allDefinitions) {
    if (CollectionUtils.isEmpty(group.getStratifications())) {
      return Collections.emptyList();
    }

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

  List<HumanReadablePopulationModel> buildMeasureObservation(
      Group group, Set<CQLDefinition> allDefinitions) {
    if (CollectionUtils.isEmpty(group.getMeasureObservations())) {
      return Collections.emptyList();
    }

    return group.getMeasureObservations().stream()
        .map(
            measureObservation -> {
              String display = measureObservation.getDefinition();
              if ("Ratio".equals(group.getScoring())) {
                Population population =
                    HumanReadableUtil.getObservationAssociation(
                        measureObservation.getCriteriaReference(), group.getPopulations());
                display = display + " (Association: " + population.getName().getDisplay() + ")";
              }
              return HumanReadablePopulationModel.builder()
                  .name(measureObservation.getDefinition())
                  .id(measureObservation.getId())
                  .display(display)
                  .logic(
                      measureObservation.getAggregateMethod()
                          + " (\n"
                          + HumanReadableUtil.getCQLDefinitionLogic(
                              measureObservation.getDefinition(), allDefinitions)
                          + "\n)")
                  .expressionName(measureObservation.getDefinition())
                  .inGroup(!StringUtils.isBlank(measureObservation.getDefinition()))
                  .build();
            })
        .collect(Collectors.toList());
  }

  List<HumanReadableExpressionModel> buildDefinitions(Set<CQLDefinition> allDefinitions) {
    List<CQLDefinition> definitions =
        allDefinitions.stream().filter(definition -> !definition.isFunction()).toList();

    return definitions.stream()
        .map(
            definition ->
                HumanReadableExpressionModel.builder()
                    .id(definition.getId())
                    .name(HumanReadableUtil.getDefinitionName(definition))
                    .logic(definition.getLogic().substring(definition.getLogic().indexOf('\n') + 1))
                    .build())
        .sorted(Comparator.comparing(HumanReadableExpressionModel::getName))
        .collect(Collectors.toList());
  }

  List<HumanReadableExpressionModel> buildFunctions(Set<CQLDefinition> allDefinitions) {
    List<CQLDefinition> functions =
        allDefinitions.stream().filter(CQLDefinition::isFunction).toList();

    return functions.stream()
        .map(
            definition ->
                HumanReadableExpressionModel.builder()
                    .id(definition.getId())
                    .name(HumanReadableUtil.getFunctionSignature(definition))
                    .logic(definition.getLogic().substring(definition.getLogic().indexOf('\n') + 1))
                    .build())
        .sorted(Comparator.comparing(HumanReadableExpressionModel::getName))
        .toList();
  }

  List<HumanReadableValuesetModel> buildValueSetDataCriteriaList(CqlLookups cqlLookups) {
    if (CollectionUtils.isEmpty(cqlLookups.getElementLookups())) {
      return Collections.emptyList();
    }
    return cqlLookups.getElementLookups().stream()
        .filter(lookup -> !lookup.isCode() && StringUtils.isNotBlank(lookup.getDatatype()))
        .map(
            retrieve ->
                new HumanReadableValuesetModel(
                    retrieve.getName(), retrieve.getOid(), "", retrieve.getDatatype()))
        .sorted(Comparator.comparing(HumanReadableValuesetModel::getDataCriteriaDisplay, collator))
        .toList();
  }

  List<HumanReadableCodeModel> buildCodeDataCriteriaList(CqlLookups cqlLookups) {
    if (CollectionUtils.isEmpty(cqlLookups.getElementLookups())) {
      return Collections.emptyList();
    }
    return cqlLookups.getElementLookups().stream()
        .filter(lookup -> lookup.isCode() && StringUtils.isNotBlank(lookup.getDatatype()))
        .map(
            retrieve ->
                HumanReadableCodeModel.builder()
                    .name(retrieve.getCodeName())
                    .oid(retrieve.getOid())
                    .datatype(retrieve.getDatatype())
                    .codesystemName(retrieve.getCodeSystemName())
                    .codesystemVersion(retrieve.getCodeSystemVersion())
                    .isCodesystemVersionIncluded(
                        StringUtils.isNotBlank(retrieve.getCodeSystemVersion()))
                    .build())
        .sorted(Comparator.comparing(HumanReadableCodeModel::getDataCriteriaDisplay, collator))
        .toList();
  }

  List<HumanReadableTerminologyModel> buildValueSetTerminology(Set<CQLValueSet> cqlValueSets) {
    if (CollectionUtils.isEmpty(cqlValueSets)) {
      return Collections.emptyList();
    }
    List<HumanReadableValuesetModel> valueSetTerminology =
        cqlValueSets.stream()
            .map(
                cqlValueSet ->
                    new HumanReadableValuesetModel(
                        cqlValueSet.getName(), cqlValueSet.getOid(), cqlValueSet.getVersion(), ""))
            .sorted(
                Comparator.comparing(HumanReadableValuesetModel::getDataCriteriaDisplay, collator))
            .toList();
    return new ArrayList<>(valueSetTerminology);
  }

  List<HumanReadableTerminologyModel> buildCodeTerminology(Set<CQLCode> cqlCodes) {
    if (CollectionUtils.isEmpty(cqlCodes)) {
      return Collections.emptyList();
    }
    List<HumanReadableCodeModel> codeTerminology =
        cqlCodes.stream()
            .map(
                cqlCode ->
                    HumanReadableCodeModel.builder()
                        .name(cqlCode.getCodeName())
                        .oid(cqlCode.getId())
                        .codesystemName(cqlCode.getCodeSystemName())
                        .codesystemVersion(cqlCode.getCodeSystemVersion())
                        .isCodesystemVersionIncluded(
                            StringUtils.isNotBlank(cqlCode.getCodeSystemVersion()))
                        .build())
            .sorted(Comparator.comparing(HumanReadableCodeModel::getDataCriteriaDisplay, collator))
            .toList();
    return new ArrayList<>(codeTerminology);
  }

  List<HumanReadableExpressionModel> buildSupplementalDataElements(
      Measure measure, List<HumanReadableExpressionModel> definitions) {
    if (CollectionUtils.isEmpty(measure.getSupplementalData())) {
      return null;
    }
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

  List<HumanReadableExpressionModel> buildRiskAdjustmentVariables(
      Measure measure, List<HumanReadableExpressionModel> definitions) {
    if (CollectionUtils.isEmpty(measure.getRiskAdjustments())) {
      return null;
    }
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
}
