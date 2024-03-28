package gov.cms.madie.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class QrdaClientConfig {

  @Value("${madie.qrda.service.base-url}")
  private String baseUrl;

  @Value("${madie.qrda.service.qrda}")
  private String qrda;

  @Bean
  public RestTemplate qrdaRestTemplate() {
    return new RestTemplate();
  }
}
