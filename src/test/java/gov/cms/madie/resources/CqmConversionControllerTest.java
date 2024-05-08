package gov.cms.madie.resources;

import gov.cms.madie.Exceptions.UnsupportedModelException;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.cqm.CqmMeasure;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.services.CqmConversionService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CqmConversionControllerTest {
  @Mock private CqmConversionService cqmConversionService;
  @InjectMocks private CqmConversionController cqmConversionController;

  private static final String TOKEN = "test token";
  private QdmMeasure measure;

  @BeforeEach
  void setup() {
    measure =
        QdmMeasure.builder()
            .id("1")
            .ecqmTitle("test")
            .model(String.valueOf(ModelType.QDM_5_6))
            .build();
  }

  @Test
  void testConvertMadieMeasureToCqmMeasure() {
    CqmMeasure cqmMeasure = CqmMeasure.builder().id("testId").build();
    when(cqmConversionService.convertMadieMeasureToCqmMeasure(measure, TOKEN))
        .thenReturn(cqmMeasure);
    CqmMeasure result = cqmConversionController.convertMadieMeasureToCqmMeasure(measure, TOKEN);
    assertThat(result, is(equalTo(cqmMeasure)));
  }

  @Test
  void testGetMeasurePackageIfModelIsNull() {
    measure.setModel(null);
    String errorMessage = "Unsupported model type: " + measure.getModel();
    Exception ex =
        Assertions.assertThrows(
            UnsupportedModelException.class,
            () -> cqmConversionController.convertMadieMeasureToCqmMeasure(measure, TOKEN),
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
            () -> cqmConversionController.convertMadieMeasureToCqmMeasure(measure, TOKEN),
            errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }
}
