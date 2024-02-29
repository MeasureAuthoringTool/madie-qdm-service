package gov.cms.madie.services;

import gov.cms.madie.Exceptions.TranslationServiceException;
import gov.cms.madie.config.CqlElmTranslatorClientConfig;
import gov.cms.madie.dto.CqlLookupRequest;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.util.MeasureUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public record TranslationServiceClient(
    CqlElmTranslatorClientConfig translatorClientConfig, RestTemplate elmTranslatorRestTemplate) {

  public List<TranslatedLibrary> getTranslatedLibraries(String cql, String accessToken) {
    URI uri =
        URI.create(translatorClientConfig.getBaseUrl() + translatorClientConfig.getCqlElmUrn());
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, accessToken);
    HttpEntity<String> entity = new HttpEntity<>(cql, headers);
    ParameterizedTypeReference<List<TranslatedLibrary>> responseType =
        new ParameterizedTypeReference<>() {};
    try {
      log.info("fetching the elm for measure cql & included libraries");
      return elmTranslatorRestTemplate
          .exchange(uri, HttpMethod.PUT, entity, responseType)
          .getBody();
    } catch (Exception ex) {
      String msg = "An issue occurred while fetching the translated artifacts for measure cql";
      log.error(msg, ex);
      throw new TranslationServiceException(msg, ex);
    }
  }

  public String getHumanReadable(Measure measure, String accessToken) {
    URI uri =
        URI.create(
            translatorClientConfig.getBaseUrl() + translatorClientConfig.getHumanReadableUrl());

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, accessToken);
    HttpEntity<Measure> measureEntity = new HttpEntity<>(measure, headers);
    try {
      log.info("fetching human readable");
      return elmTranslatorRestTemplate
          .exchange(uri, HttpMethod.PUT, measureEntity, String.class)
          .getBody();
    } catch (Exception ex) {
      String msg =
          "An issue occurred while fetching human readable for measure id: " + measure.getId();
      log.error(msg, ex);
      throw new TranslationServiceException(msg, ex);
    }
  }

  public CqlLookups getCqlLookups(QdmMeasure measure, String accessToken) {
    URI uri =
        URI.create(translatorClientConfig.getBaseUrl() + translatorClientConfig.getCqlLookups());
    Set<String> measureDefinitions = MeasureUtils.getMeasureDefinitions(measure);
    CqlLookupRequest lokRequest =
        CqlLookupRequest.builder()
            .cql(measure.getCql())
            .measureExpressions(measureDefinitions)
            .build();
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, accessToken);
    HttpEntity<CqlLookupRequest> lookupRequestEntity = new HttpEntity<>(lokRequest, headers);
    try {
      log.info("fetching CQL Lookups");
      return elmTranslatorRestTemplate
          .exchange(uri, HttpMethod.PUT, lookupRequestEntity, CqlLookups.class)
          .getBody();
    } catch (Exception ex) {
      String msg = "An issue occurred while fetching CQL Lookups for measure: " + measure.getId();
      log.error(msg, ex);
      throw new TranslationServiceException(msg, ex);
    }
  }
}
