package gov.cms.madie.services;

import gov.cms.madie.Exceptions.TranslationServiceException;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.QdmMeasure;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackagingServiceTest {
  @Mock private TranslationServiceClient translationServiceClient;
  @Mock private HqmfService hqmfService;
  @Mock private HumanReadableService humanReadableService;
  @InjectMocks private PackagingService packagingService;

  private static final String TOKEN = "test token";
  private QdmMeasure measure;

  @BeforeEach
  void setUp() throws Exception {
    measure =
        QdmMeasure.builder()
            .id("1")
            .ecqmTitle("test")
            .version(
                gov.cms.madie.models.common.Version.builder()
                    .major(1)
                    .minor(2)
                    .revisionNumber(3)
                    .build())
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
    CqlLookups cqlLookups = CqlLookups.builder().build();
    when(translationServiceClient.getTranslatedLibraries(measure.getCql(), TOKEN))
        .thenReturn(List.of(library1, library2));
    when(translationServiceClient.getCqlLookups(any(QdmMeasure.class), anyString()))
        .thenReturn(CqlLookups.builder().build());
    when(hqmfService.generateHqmf(measure, cqlLookups))
        .thenReturn("<hqmf>this is a test hqmf</hqmf>");
    when(humanReadableService.generate(any(Measure.class), any(CqlLookups.class)))
        .thenReturn("success");
    byte[] packageContents = packagingService.createMeasurePackage(measure, TOKEN);
    String packageString = new String(packageContents);
    String library1FileName = library1.getName() + "-" + library1.getVersion();
    assertThat(packageString, containsString(library1FileName + ".cql"));
    assertThat(packageString, containsString(library1FileName + ".xml"));
    assertThat(packageString, containsString(library1FileName + ".json"));
    assertThat(packageString, containsString("test-v1.2.003-QDM.html"));
    assertThat(packageString, containsString("test-v1.2.003-QDM.xml"));
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

  @Test
  void testCreateMeasurePackageWhenHqmfGenerationFailed() {
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
    CqlLookups cqlLookups = CqlLookups.builder().build();
    when(translationServiceClient.getCqlLookups(any(QdmMeasure.class), anyString()))
            .thenReturn(cqlLookups);
    when(humanReadableService.generate(any(Measure.class), any(CqlLookups.class)))
            .thenReturn("success");
    Mockito.doThrow(new RuntimeException("Test Exception"))
                    .when(hqmfService).generateHqmf(any(QdmMeasure.class), any(CqlLookups.class));
    byte[] packageContents = packagingService.createMeasurePackage(measure, TOKEN);
    String packageString = new String(packageContents);
    String library1FileName = library1.getName() + "-" + library1.getVersion();
    assertThat(packageString, containsString(library1FileName + ".cql"));
    assertThat(packageString, containsString(library1FileName + ".xml"));
    assertThat(packageString, containsString(library1FileName + ".json"));
    assertThat(packageString, containsString("test-v1.2.003-QDM.html"));
    assertThat(packageString, containsString("test-v1.2.003-QDM-ERROR.xml"));
  }

  @Test
  void testCreateQRDA() {
    byte[] qrda = packagingService.createQRDA(measure, TOKEN);
    assertThat(new String(qrda), is(equalTo("test qrda")));
  }
}
