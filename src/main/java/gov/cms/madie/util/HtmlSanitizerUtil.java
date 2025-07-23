package gov.cms.madie.util;

import gov.cms.madie.models.measure.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.util.CollectionUtils;

public class HtmlSanitizerUtil {
  public static final String UNKNOWN = "UNKNOWN";
  private static final Safelist RICH_TEXT_SAFE_LIST =
      Safelist.basic()
          .addTags("s", "br", "table", "tbody", "td", "th", "thead", "tr", "col", "colgroup")
          .addAttributes("table", "style", "class", "id")
          .addAttributes("th", "rowspan", "colspan", "style", "colwidth")
          .addAttributes("td", "rowspan", "colspan", "style", "colwidth")
          .addAttributes("col", "style");

  public static String sanitize(String val) {
    if (StringUtils.isBlank(val)) {
      return val;
    }
    String safeHtml = Jsoup.clean(val, RICH_TEXT_SAFE_LIST);
    // col tags are not self-closing in html, so we need to make them wel-formed
    return safeHtml.replaceAll("<col ([^/>]*)>", "<col $1 />");
  }

  public static void sanitizeMeasure(QdmMeasure measure) {
    if (measure != null) {
      MeasureMetaData measureMetaData = measure.getMeasureMetaData();
      if (measureMetaData != null) {
        measureMetaData.setDescription(sanitize(measureMetaData.getDescription()));
        measureMetaData.setCopyright(sanitize(measureMetaData.getCopyright()));
        measureMetaData.setDisclaimer(sanitize(measureMetaData.getDisclaimer()));
        measureMetaData.setRationale(sanitize(measureMetaData.getRationale()));
        measureMetaData.setClinicalRecommendation(
            sanitize(measureMetaData.getClinicalRecommendation()));
        measureMetaData.setGuidance(sanitize(measureMetaData.getGuidance()));
        measureMetaData.setTransmissionFormat(sanitize(measureMetaData.getTransmissionFormat()));
        measureMetaData.setDefinition(sanitize(measureMetaData.getDefinition()));
        measureMetaData.setMeasureSetTitle(sanitize(measureMetaData.getMeasureSetTitle()));
      }
      measure.setRiskAdjustmentDescription(sanitize(measure.getRiskAdjustmentDescription()));
      measure.setSupplementalDataDescription(sanitize(measure.getSupplementalDataDescription()));
      measure.setRateAggregation(sanitize(measure.getRateAggregation()));
      measure.setImprovementNotation(sanitize(measure.getImprovementNotation()));
      measure.setImprovementNotationDescription(
          sanitize(measure.getImprovementNotationDescription()));
      if (!CollectionUtils.isEmpty(measure.getGroups())) {
        for (Group group : measure.getGroups()) {
          group.setGroupDescription(sanitize(group.getGroupDescription()));
          if (!CollectionUtils.isEmpty(group.getPopulations())) {
            for (Population population : group.getPopulations()) {
              population.setDescription(sanitize(population.getDescription()));
            }
          }
          if (!CollectionUtils.isEmpty(group.getStratifications())) {
            for (Stratification stratification : group.getStratifications()) {
              stratification.setDescription(sanitize(stratification.getDescription()));
            }
          }
          if (!CollectionUtils.isEmpty(group.getMeasureObservations())) {
            for (MeasureObservation observation : group.getMeasureObservations()) {
              observation.setDescription(sanitize(observation.getDescription()));
            }
          }
        }
      }
    }
  }
}
