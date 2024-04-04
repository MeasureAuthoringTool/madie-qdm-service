package gov.cms.madie.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.madie.Exceptions.TranslationServiceException;
import gov.cms.madie.config.QrdaClientConfig;
import gov.cms.madie.dto.QRDADto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class QrdaClient {

  private final QrdaClientConfig qrdaClientConfig;
  private final RestTemplate qrdaRestTemplate;

  @Autowired private ObjectMapper mapper;

  public List<String> getQRDA(QRDADto dto, String accessToken) {
    URI uri = URI.create(qrdaClientConfig.getBaseUrl() + qrdaClientConfig.getQrda());
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, accessToken);
    headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
    HttpEntity<String> entity = null;
    try {
      entity = new HttpEntity<>(mapper.writeValueAsString(dto), headers);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {};
    try {
      log.info("fetching the qrda for measure");
      String result =
          qrdaRestTemplate.exchange(uri, HttpMethod.PUT, entity, responseType).getBody();

      // Splits the individual documents apart for packaging
      return Arrays.stream(result.split("</ClinicalDocument>"))
          .map(s -> s + "\n</ClinicalDocument>")
          .toList();
    } catch (Exception ex) {
      String msg = "An issue occurred while fetching the qrda for measure";
      log.error(msg, ex);
      throw new TranslationServiceException(msg, ex);
    }
  }
}
