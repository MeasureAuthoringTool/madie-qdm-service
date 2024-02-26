package gov.cms.madie.services;

import gov.cms.madie.hqmf.HQMFGeneratorFactory;
import gov.cms.madie.hqmf.dto.MeasureExport;
import gov.cms.madie.hqmf.qdm_5_6.HQMFGenerator;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.measure.QdmMeasure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HqmfServiceTest {

  @Mock SimpleXmlService simpleXmlService;
  @Mock HQMFGeneratorFactory factory;

  @InjectMocks HqmfService hqmfService;

  @Test
  void generateHqmf() throws Exception {
    QdmMeasure measure =
        QdmMeasure.builder()
            .id("1")
            .ecqmTitle("test")
            .model(String.valueOf(ModelType.QDM_5_6))
            .build();

    when(simpleXmlService.measureToSimpleXml(any(QdmMeasure.class)))
        .thenReturn("<measure></measure>");

    HQMFGenerator generator = mock(HQMFGenerator.class);
    when(factory.getHQMFGenerator()).thenReturn(generator);
    when(generator.generate(any(MeasureExport.class)))
        .thenReturn("<QualityMeasureDocument></QualityMeasureDocument>");

    String output = hqmfService.generateHqmf(measure);
    assertThat(output, is(equalTo("<QualityMeasureDocument></QualityMeasureDocument>")));
  }
}
