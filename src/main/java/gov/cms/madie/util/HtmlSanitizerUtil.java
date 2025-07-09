package gov.cms.madie.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizerUtil {
  public static String sanitize(String html) {
    if(StringUtils.isEmpty(html)) {
      return html;
    }
    Safelist safelist =
        Safelist.basic()
            .addTags("h1", "h2", "h3")
            .addAttributes("a", "target")
            .addProtocols("a", "href", "http", "https");

    return Jsoup.clean(html, safelist);
  }
}
