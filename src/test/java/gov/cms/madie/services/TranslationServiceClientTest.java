package gov.cms.madie.services;

import gov.cms.madie.Exceptions.TranslationServiceException;
import gov.cms.madie.config.CqlElmTranslatorClientConfig;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.Measure;

import gov.cms.madie.models.measure.QdmMeasure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranslationServiceClientTest {

  @Mock private CqlElmTranslatorClientConfig translatorClientConfig;
  @Mock private RestTemplate elmTranslatorRestTemplate;
  @InjectMocks private TranslationServiceClient translationServiceClient;

  @Test
  void getTranslatedLibraries() {
    TranslatedLibrary library1 =
        TranslatedLibrary.builder().name("one").cql("cql").elmJson("json").build();
    TranslatedLibrary library2 =
        TranslatedLibrary.builder().name("two").cql("cql").elmJson("json").build();
    when(translatorClientConfig.getBaseUrl()).thenReturn("baseurl");
    when(translatorClientConfig.getCqlElmUrn()).thenReturn("/elm/uri");
    when(elmTranslatorRestTemplate.exchange(
            any(URI.class),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)))
        .thenReturn(ResponseEntity.ok(List.of(library1, library2)));
    List<TranslatedLibrary> translatedLibraries =
        translationServiceClient.getTranslatedLibraries("cql", "token");
    assertThat(translatedLibraries.size(), is(equalTo(2)));
    assertThat(translatedLibraries.get(0).getName(), is(equalTo(library1.getName())));
    assertThat(translatedLibraries.get(1).getName(), is(equalTo(library2.getName())));
  }

  @Test
  void getTranslatedLibrariesWhenTranslationServiceFailed() {
    String message = "An issue occurred while fetching the translated artifacts for measure cql";
    when(elmTranslatorRestTemplate.exchange(
            any(URI.class),
            eq(HttpMethod.PUT),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)))
        .thenThrow(new TranslationServiceException(message, new Exception()));
    Exception ex =
        assertThrows(
            TranslationServiceException.class,
            () -> translationServiceClient.getTranslatedLibraries("cql", "token"),
            message);
    assertThat(ex.getMessage(), containsString(message));
  }

  @Test
  void testGetHumanReadableSuccess() {
    when(translatorClientConfig.getBaseUrl()).thenReturn("baseurl");
    when(translatorClientConfig.getHumanReadableUrl()).thenReturn("/human-readable");
    when(elmTranslatorRestTemplate.exchange(
            any(URI.class), eq(HttpMethod.PUT), any(HttpEntity.class), any(Class.class)))
        .thenReturn(ResponseEntity.ok("success"));

    String humanReadable =
        translationServiceClient.getHumanReadable(Measure.builder().build(), "token");
    assertThat(humanReadable, is(equalTo("success")));
  }

  @Test
  void testGetHumanReadableFailure() {
    String message =
        "An issue occurred while fetching human readable for measure id: testMeasureId";
    when(elmTranslatorRestTemplate.exchange(
            any(URI.class), eq(HttpMethod.PUT), any(HttpEntity.class), any(Class.class)))
        .thenThrow(new TranslationServiceException(message, new Exception()));
    Exception ex =
        assertThrows(
            TranslationServiceException.class,
            () ->
                translationServiceClient.getHumanReadable(
                    Measure.builder().id("testMeasureId").build(), "token"),
            message);
    assertThat(ex.getMessage(), containsString(message));
  }

  @Test
  void testGetCqlLookups() {
    CqlLookups cqlLookups =
        CqlLookups.builder()
            .library("Test")
            .version("0.0.001")
            .usingModel("QDM")
            .usingModelVersion("5.6")
            .build();
    QdmMeasure measure = QdmMeasure.builder().cqlLibraryName("Test").build();
    ResponseEntity<CqlLookups> LookupResponseEntity = ResponseEntity.ok().body(cqlLookups);
    when(elmTranslatorRestTemplate.exchange(
            any(URI.class), eq(HttpMethod.PUT), any(HttpEntity.class), any(Class.class)))
        .thenReturn(LookupResponseEntity);
    CqlLookups cqlLookupsResponse = translationServiceClient.getCqlLookups(measure, "token");
    assertThat(cqlLookupsResponse.getLibrary(), is(equalTo(cqlLookups.getLibrary())));
    assertThat(cqlLookupsResponse.getVersion(), is(equalTo(cqlLookups.getVersion())));
  }

  @Test
  void testGetCqlLookupsFailure() {
    String message = "An issue occurred while fetching CQL Lookups for measure: testMeasureId";
    when(elmTranslatorRestTemplate.exchange(
            any(URI.class), eq(HttpMethod.PUT), any(HttpEntity.class), any(Class.class)))
        .thenThrow(new TranslationServiceException(message, new Exception()));
    Exception ex =
        assertThrows(
            TranslationServiceException.class,
            () ->
                translationServiceClient.getCqlLookups(
                    QdmMeasure.builder().id("testMeasureId").build(), "token"),
            message);
    assertThat(ex.getMessage(), containsString(message));
  }
}
