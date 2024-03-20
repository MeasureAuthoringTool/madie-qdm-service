package gov.cms.madie.resources;

import gov.cms.madie.Exceptions.UnsupportedModelException;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.models.measure.FhirMeasure;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.services.HqmfService;
import gov.cms.madie.services.HumanReadableService;
import gov.cms.madie.services.PackagingService;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.services.SimpleXmlService;
import gov.cms.madie.services.TranslationServiceClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackageControllerTest {

  @Mock private PackagingService packagingService;
  @Mock private SimpleXmlService simpleXmlService;
  @Mock private HqmfService hqmfService;
  @Mock private HumanReadableService humanReadableService;
  @Mock private TranslationServiceClient translationServiceClient;
  @InjectMocks private PackageController packageController;

  private static final String TOKEN = "test token";
  private Measure measure;

  @BeforeEach
  void setup() {
    measure =
        Measure.builder()
            .id("1")
            .ecqmTitle("test")
            .model(String.valueOf(ModelType.QDM_5_6))
            .build();
  }

  @Test
  void testGetMeasurePackage() {
    String measurePackage = "measure package";
    when(packagingService.createMeasurePackage(measure, TOKEN))
        .thenReturn(measurePackage.getBytes());
    byte[] rawPackage = packageController.getMeasurePackage(measure, TOKEN);
    assertThat(new String(rawPackage), is(equalTo(measurePackage)));
  }

  @Test
  void testGetMeasurePackageIfModelIsNull() {
    measure.setModel(null);
    String errorMessage = "Unsupported model type: " + measure.getModel();
    Exception ex =
        Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> packageController.getMeasurePackage(measure, TOKEN),
            errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }

  @Test
  void testGetMeasurePackageForUnsupportedModel() {
    measure.setModel(String.valueOf(ModelType.QI_CORE));
    String errorMessage = "Unsupported model type: " + measure.getModel();
    Exception ex =
        Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> packageController.getMeasurePackage(measure, TOKEN),
            errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }

  @Test
  void testGetMeasureSimpleXmlIfModelIsNull() {
    measure.setModel(String.valueOf(ModelType.QI_CORE));
    String errorMessage = "Unsupported model type: " + measure.getModel();
    Exception ex =
        Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> packageController.getMeasureSimpleXml(measure, "user.jwt"),
            errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }

  @Test
  void testGetMeasureSimpleXmlForUnsupportedModel() {
    measure.setModel(String.valueOf(ModelType.QI_CORE));
    String errorMessage = "Unsupported model type: " + measure.getModel();
    Exception ex =
        Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> packageController.getMeasureSimpleXml(measure, "user.jwt"),
            errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }

  @Test
  void testGenerateHqmfForUnsupportedModel() {
    measure.setModel(String.valueOf(ModelType.QI_CORE));
    String errorMessage = "Unsupported model type: " + measure.getModel();
    Exception ex =
        Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> packageController.generateHqmf(measure, TOKEN),
            errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }

  @Test
  void testGenerateHqmfForUnsupportedModelMissingMeasure() {
    measure.setModel(null);
    String errorMessage = "Unsupported model type: NONE";
    Exception ex =
        Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> packageController.generateHqmf(measure, TOKEN),
            errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }

  @Test
  void testGenerateHqmfReturnsHqmf() throws Exception {
    measure =
        QdmMeasure.builder()
            .id("1")
            .ecqmTitle("test")
            .model(String.valueOf(ModelType.QDM_5_6))
            .build();
    when(translationServiceClient.getCqlLookups(any(QdmMeasure.class), anyString()))
        .thenReturn(CqlLookups.builder().build());
    when(hqmfService.generateHqmf(any(QdmMeasure.class), any(CqlLookups.class)))
        .thenReturn("<QualityMeasureDocument></QualityMeasureDocument>");
    String hqmf = packageController.generateHqmf(measure, TOKEN).getBody();
    assertThat(hqmf, is(equalTo("<QualityMeasureDocument></QualityMeasureDocument>")));
  }

  @Test
  void testGetQRDASuccess() {
    String qrda = "test QRDA";
    when(packagingService.createQRDA(measure, TOKEN)).thenReturn(qrda.getBytes());
    byte[] result = packageController.getQRDA(measure, TOKEN);
    assertThat(new String(result), is(equalTo(qrda)));
  }

  @Test
  void testGetQRDAIfModelIsNull() {
    measure.setModel(null);
    String errorMessage = "Unsupported model type: " + measure.getModel();
    Exception ex =
        Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> packageController.getQRDA(measure, TOKEN),
            errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }

  @Test
  void testGetQRDAForUnsupportedModel() {
    measure.setModel(String.valueOf(ModelType.QI_CORE));
    String errorMessage = "Unsupported model type: " + measure.getModel();
    Exception ex =
        Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> packageController.getQRDA(measure, TOKEN),
            errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }

  @Test
  void testGenerateHumanReadable() {
    CqlLookups cqlLookups = CqlLookups.builder().build();
    measure =
        QdmMeasure.builder()
            .id("1")
            .ecqmTitle("test")
            .model(String.valueOf(ModelType.QDM_5_6))
            .build();
    when(translationServiceClient.getCqlLookups(any(QdmMeasure.class), anyString()))
        .thenReturn(cqlLookups);
    when(humanReadableService.generate(any(), any(CqlLookups.class)))
        .thenReturn("test human Readable");
    var result = packageController.getMeasureHumanReadable(measure, "accessToken");
    assertThat(result, is(equalTo("test human Readable")));
  }

  @Test
  void testGenerateHumanReadableUnsupportedModel() {
    measure =
        FhirMeasure.builder()
            .id("1")
            .ecqmTitle("test")
            .model(String.valueOf(ModelType.QI_CORE))
            .build();

    Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> packageController.getMeasureHumanReadable(measure, "accessToken"));
    verifyNoInteractions(translationServiceClient);
    verifyNoInteractions(humanReadableService);
  }
}
