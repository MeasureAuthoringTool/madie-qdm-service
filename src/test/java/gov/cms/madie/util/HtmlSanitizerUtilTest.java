package gov.cms.madie.util;

import gov.cms.madie.models.measure.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HtmlSanitizerUtilTest {

  @Test
  void sanitizeValidHtml() {
    String html =
        "<h1>Title</h1><p>Paragraph</p><a href=\"http://example.com\" target=\"_blank\">Link</a>";
    String sanitized = HtmlSanitizerUtil.sanitize(html);

    assertEquals(
        "Title\n<p>Paragraph</p>\n<a href=\"http://example.com\" rel=\"nofollow\">Link</a>",
        sanitized);
  }

  @Test
  void sanitizeHtmlWithDisallowedTags() {
    String html = "<script>alert('XSS');</script><h1>Title</h1>";
    String sanitized = HtmlSanitizerUtil.sanitize(html);

    assertEquals("Title", sanitized);
  }

  @Test
  void sanitizeHtmlWithDisallowedAttributes() {
    String html = "<a href=\"http://example.com\" onclick=\"alert('XSS')\">Link</a>";
    String sanitized = HtmlSanitizerUtil.sanitize(html);

    assertEquals("<a href=\"http://example.com\" rel=\"nofollow\">Link</a>", sanitized);
  }

  @Test
  void sanitizeEmptyHtml() {
    String html = "";
    String sanitized = HtmlSanitizerUtil.sanitize(html);

    assertEquals("", sanitized);
  }

  @Test
  void sanitizeNullHtml() {
    String sanitized = HtmlSanitizerUtil.sanitize(null);

    assertNull(sanitized);
  }

  @Test
  void sanitizeHtmlWithDisallowedProtocols() {
    String html = "<a href=\"javascript:alert('XSS')\">Link</a>";
    String sanitized = HtmlSanitizerUtil.sanitize(html);

    assertEquals("<a rel=\"nofollow\">Link</a>", sanitized);
  }

  @Test
  void sanitizeMeasure() {
    QdmMeasure measure = new QdmMeasure();
    MeasureMetaData metaData = new MeasureMetaData();
    metaData.setDescription("<script>bad()</script>desc");
    metaData.setCopyright("<b>copy</b>");
    metaData.setDisclaimer("Not allowed <img src=x onerror=alert(1)>");
    metaData.setRationale("<h1>rat</h1>");
    metaData.setClinicalRecommendation("<a href=\"javascript:alert(1)\">rec</a>");
    metaData.setGuidance("<span>guide</span>");
    metaData.setTransmissionFormat("<div>format</div>");
    metaData.setDefinition("<u>def</u>");
    metaData.setMeasureSetTitle("<i>title</i>");
    measure.setMeasureMetaData(metaData);

    measure.setRiskAdjustmentDescription("This is <script>risky</script>");
    measure.setSupplementalDataDescription("<b>supp</b>");
    measure.setRateAggregation("This is not allowed either<img src=x>");
    measure.setImprovementNotation("<u>imp</u>");
    measure.setImprovementNotationDescription("<span>impdesc</span>");

    Group group = new Group();
    group.setGroupDescription("Test <script>group</script>");
    Population pop = new Population();
    pop.setDescription("<b>pop</b>");
    group.setPopulations(List.of(pop));
    Stratification strat = new Stratification();
    strat.setDescription("<img src=x>");
    group.setStratifications(List.of(strat));
    MeasureObservation obs = new MeasureObservation();
    obs.setDescription("<u>obs</u>");
    group.setMeasureObservations(List.of(obs));
    measure.setGroups(List.of(group));

    HtmlSanitizerUtil.sanitizeMeasure(measure);

    assertEquals("desc", metaData.getDescription());
    assertEquals("<b>copy</b>", metaData.getCopyright());
    assertEquals("Not allowed", metaData.getDisclaimer());
    assertEquals("rat", metaData.getRationale());
    assertEquals("<a rel=\"nofollow\">rec</a>", metaData.getClinicalRecommendation());
    assertEquals("<span>guide</span>", metaData.getGuidance());
    assertEquals("format", metaData.getTransmissionFormat());
    assertEquals("<u>def</u>", metaData.getDefinition());
    assertEquals("<i>title</i>", metaData.getMeasureSetTitle());

    assertEquals("This is", measure.getRiskAdjustmentDescription());
    assertEquals("<b>supp</b>", measure.getSupplementalDataDescription());
    assertEquals("This is not allowed either", measure.getRateAggregation());
    assertEquals("<u>imp</u>", measure.getImprovementNotation());
    assertEquals("<span>impdesc</span>", measure.getImprovementNotationDescription());

    assertEquals("Test", group.getGroupDescription());
    assertEquals("<b>pop</b>", pop.getDescription());
    assertEquals("", strat.getDescription());
    assertEquals("<u>obs</u>", obs.getDescription());
  }
}
