package gov.cms.madie.services;

import gov.cms.madie.Exceptions.TranslationServiceException;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.Measure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackagingServiceTest {
  @Mock private TranslationServiceClient translationServiceClient;
  @InjectMocks private PackagingService packagingService;

  private static final String TOKEN = "test token";
  private Measure measure;

  @BeforeEach
  void setUp() throws Exception {
    measure =
        Measure.builder()
            .id("1")
            .ecqmTitle("test")
            .cql("fake cql")
            .model(String.valueOf(ModelType.QDM_5_6))
            .build();
  }

  @Test
  void testCreateMeasurePackage() {
    TranslatedLibrary library1 =
        TranslatedLibrary.builder()
            .name("Lib one")
            .version("0.0.000")
            .elmJson("elm xml")
            .elmXml("elm xml")
            .cql("cql")
            .build();
    TranslatedLibrary library2 =
        TranslatedLibrary.builder()
            .name("Lib two")
            .version("0.0.001")
            .elmJson("elm xml")
            .elmXml("elm xml")
            .cql("cql")
            .build();
    when(translationServiceClient.getTranslatedLibraries(measure.getCql(), TOKEN))
        .thenReturn(List.of(library1, library2));
    byte[] packageContents = packagingService.createMeasurePackage(measure, TOKEN);
    assertNotNull(packageContents);
  }

  @Test
  void testCreateMeasurePackageWhenNoLibFound() {
    when(translationServiceClient.getTranslatedLibraries(measure.getCql(), TOKEN))
        .thenReturn(List.of());
    byte[] packageContents = packagingService.createMeasurePackage(measure, TOKEN);
    assertThat(packageContents.length, is(equalTo(0)));
  }

  @Test
  void testCreateMeasurePackageWhenTranslationFailed() {
    String msg = "An issue occurred while fetching the translated artifacts for measure cql";
    Mockito.doThrow(new TranslationServiceException(msg, new Exception()))
        .when(translationServiceClient)
        .getTranslatedLibraries(anyString(), anyString());
    Exception exception =
        assertThrows(
            TranslationServiceException.class,
            () -> packagingService.createMeasurePackage(measure, TOKEN),
            msg);
    assertThat(exception.getMessage(), containsString(msg));
  }
}
