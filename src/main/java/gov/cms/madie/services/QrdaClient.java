package gov.cms.madie.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.madie.Exceptions.TranslationServiceException;
import gov.cms.madie.config.QrdaClientConfig;
import gov.cms.madie.dto.QRDADto;
import gov.cms.madie.dto.QrdaResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
@AllArgsConstructor
public class QrdaClient {

  private final QrdaClientConfig qrdaClientConfig;
  private final RestTemplate qrdaRestTemplate;

  @Autowired private ObjectMapper mapper;

  public QrdaResponseDto[] getQRDA(QRDADto dto, String accessToken, String measureId) {
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

    try {
      log.info("fetching the qrda for measure {}", measureId);
       return qrdaRestTemplate.exchange(uri, HttpMethod.PUT, entity, QrdaResponseDto[].class).getBody();
    } catch (Exception ex) {
      String msg = "An issue occurred while fetching the qrda for measure " + measureId;
      log.error(msg, ex);
      throw new TranslationServiceException(msg, ex);
    }
  }
}
