package gov.cms.madie.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlSanitizerUtilTest {

    @Test
    void sanitizeValidHtml() {
        String html = "<h1>Title</h1><p>Paragraph</p><a href=\"http://example.com\" target=\"_blank\">Link</a>";
        String sanitized = HtmlSanitizerUtil.sanitize(html);

        assertEquals("<h1>Title</h1>\n<p>Paragraph</p>\n<a href=\"http://example.com\" target=\"_blank\" rel=\"nofollow\">Link</a>", sanitized);
    }

    @Test
    void sanitizeHtmlWithDisallowedTags() {
        String html = "<script>alert('XSS');</script><h1>Title</h1>";
        String sanitized = HtmlSanitizerUtil.sanitize(html);

        assertEquals("<h1>Title</h1>", sanitized);
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

}