package gov.cms.madie.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class CqlElmTranslatorClientConfig {

  @Value("${madie.cql-elm.service.base-url}")
  private String baseUrl;

  @Value("${madie.cql-elm.service.cql-elm-urn}")
  private String cqlElmUrn;

  @Value("${madie.cql-elm.service.human-readable}")
  private String humanReadableUrl;

  @Value("${madie.cql-elm.service.cql-lookups}")
  private String cqlLookups;

  @Value("${madie.cql-elm.service.relevant-data-elements}")
  private String relevantDataElements;

  @Bean
  public RestTemplate elmTranslatorRestTemplate() {
    return new RestTemplate();
  }
}
