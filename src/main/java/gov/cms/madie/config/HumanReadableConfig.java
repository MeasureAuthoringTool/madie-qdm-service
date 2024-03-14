package gov.cms.madie.config;

import freemarker.template.Template;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;

@Configuration
public class HumanReadableConfig {


    @Bean
    public FreeMarkerConfigurer freemarkerConfig() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/templates");
        return freeMarkerConfigurer;
    }

    @Bean
    public Template baseHumanReadableTemplate(
            freemarker.template.Configuration freemarkerConfiguration) {
        try {
            return freemarkerConfiguration.getTemplate("humanreadable/human_readable.ftl");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Human Readable Template", e);
        }
    }

}
