package gov.cms.madie.madieqdmservice.services;

import gov.cms.madie.madieqdmservice.Exceptions.TranslationServiceException;
import gov.cms.madie.madieqdmservice.config.CqlElmTranslatorClientConfig;
import gov.cms.madie.madieqdmservice.dto.TranslatedLibrary;
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
public class TranslationServiceClientTest {

  @Mock private CqlElmTranslatorClientConfig translatorClientConfig;
  @Mock private RestTemplate elmTranslatorRestTemplate;
  @InjectMocks private TranslationServiceClient translationServiceClient;

  @Test
  public void getTranslatedLibraries() {
    TranslatedLibrary library1 =
        TranslatedLibrary.builder().name("one").cql("cql").elmJson("json").build();
    TranslatedLibrary library2 =
        TranslatedLibrary.builder().name("two").cql("cql").elmJson("json").build();
    when(translatorClientConfig.getBaseUrl()).thenReturn("baseurl");
    when(translatorClientConfig.getCqlElmUri()).thenReturn("/elm/uri");
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
  public void getTranslatedLibrariesWhenTranslationServiceFailed() {
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
}
