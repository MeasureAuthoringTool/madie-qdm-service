package gov.cms.madie.services;

import gov.cms.madie.Exceptions.PackagingException;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.hqmf.HQMFGeneratorFactory;
import gov.cms.madie.hqmf.dto.MeasureExport;
import gov.cms.madie.hqmf.qdm_5_6.HQMFGenerator;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.measure.QdmMeasure;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HqmfServiceTest {
  private static String TOKEN = "joh doe";

  @Mock SimpleXmlService simpleXmlService;
  @Mock HQMFGeneratorFactory factory;
  @Mock TranslationServiceClient translationServiceClient;

  @InjectMocks HqmfService hqmfService;
  private QdmMeasure measure;

  @BeforeEach
  void setUp() {
    measure =
        QdmMeasure.builder()
            .id("1")
            .ecqmTitle("test")
            .model(String.valueOf(ModelType.QDM_5_6))
            .build();
  }

  @Test
  void generateHqmf() throws Exception {
    when(simpleXmlService.measureToSimpleXml(any(QdmMeasure.class), any(CqlLookups.class)))
        .thenReturn("<measure></measure>");
    when(translationServiceClient.getCqlLookups(any(QdmMeasure.class), anyString()))
        .thenReturn(CqlLookups.builder().build());

    HQMFGenerator generator = mock(HQMFGenerator.class);
    when(factory.getHQMFGenerator()).thenReturn(generator);
    when(generator.generate(any(MeasureExport.class)))
        .thenReturn("<QualityMeasureDocument></QualityMeasureDocument>");

    String output = hqmfService.generateHqmf(measure, TOKEN);
    assertThat(output, is(equalTo("<QualityMeasureDocument></QualityMeasureDocument>")));
  }

  @Test
  void generateHqmfWhenSimpleXmlGenerationFailed() throws Exception {
    when(translationServiceClient.getCqlLookups(any(QdmMeasure.class), anyString()))
        .thenReturn(CqlLookups.builder().build());
    String message = "An issue occurred while generating the simple xml for measure";
    doThrow(new JAXBException(message))
        .when(simpleXmlService)
        .measureToSimpleXml(any(QdmMeasure.class), any(CqlLookups.class));
    Exception ex =
        assertThrows(
            PackagingException.class,
            () -> hqmfService.generateHqmf(measure, TOKEN),
            "Exception occurred");
    assertThat(ex.getMessage(), containsString(message));
  }

  @Test
  void generateHqmfWhenHqmfGenerationFailed() throws Exception {
    when(simpleXmlService.measureToSimpleXml(any(QdmMeasure.class), any(CqlLookups.class)))
        .thenReturn("<measure></measure>");
    String message = "An issue occurred while generating the HQMF for measure";
    HQMFGenerator generator = mock(HQMFGenerator.class);
    when(factory.getHQMFGenerator()).thenReturn(generator);
    doThrow(new Exception(message)).when(generator).generate(any(MeasureExport.class));
    Exception ex =
        assertThrows(
            PackagingException.class,
            () -> hqmfService.generateHqmf(measure, TOKEN),
            "Exception occurred");
    assertThat(ex.getMessage(), containsString(message));
  }
}
