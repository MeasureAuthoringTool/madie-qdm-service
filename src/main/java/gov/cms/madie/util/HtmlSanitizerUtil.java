package gov.cms.madie.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizerUtil {
  public static String sanitize(String html) {
    if (StringUtils.isEmpty(html)) {
      return html;
    }
    Safelist safelist =
        Safelist.basic()
            .addTags(
                "p",
                "strong",
                "em",
                "u",
                "s",
                "ul",
                "ol",
                "li",
                "br",
                "table",
                "tbody",
                "td",
                "tfoot",
                "th",
                "thead",
                "tr",
                "col",
                "colgroup")
            .addAttributes("table", "style")
            .addAttributes("th", "rowspan", "colspan", "style", "colwidth")
            .addAttributes("td", "rowspan", "colspan", "style", "colwidth")
            .addAttributes("col", "style");

    return Jsoup.clean(html, safelist);
  }
}
