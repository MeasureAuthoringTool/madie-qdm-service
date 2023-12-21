package gov.cms.madie.madieqdmservice.resources;

import gov.cms.madie.madieqdmservice.Exceptions.UnsupportedModelException;
import gov.cms.madie.madieqdmservice.services.PackagingService;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.measure.Measure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class PackageControllerTest {

  @Mock private PackagingService packagingService;
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
    Mockito.when(packagingService.createMeasurePackage(measure, TOKEN))
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
}
