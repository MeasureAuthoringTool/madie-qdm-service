package gov.cms.madie.resources;

import gov.cms.madie.Exceptions.UnsupportedModelException;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.services.HqmfService;
import gov.cms.madie.services.PackagingService;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.services.SimpleXmlService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackageControllerTest {

  @Mock private PackagingService packagingService;
  @Mock private SimpleXmlService simpleXmlService;
  @Mock private HqmfService hqmfService;
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
            () -> packageController.getMeasureSimpleXml(measure),
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
            () -> packageController.getMeasureSimpleXml(measure),
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
    when(hqmfService.generateHqmf(any(QdmMeasure.class), anyString()))
        .thenReturn("<QualityMeasureDocument></QualityMeasureDocument>");
    String hqmf = packageController.generateHqmf(measure, TOKEN).getBody();
    assertThat(hqmf, is(equalTo("<QualityMeasureDocument></QualityMeasureDocument>")));
  }
}
