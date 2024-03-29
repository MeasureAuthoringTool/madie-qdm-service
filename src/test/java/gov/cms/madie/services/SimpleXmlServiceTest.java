package gov.cms.madie.services;

import generated.gov.cms.madie.simplexml.MeasureDetailsType;
import generated.gov.cms.madie.simplexml.MeasureType;
import gov.cms.madie.Exceptions.PackagingException;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.models.measure.QdmMeasure;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.StringWriter;
import java.io.Writer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleXmlServiceTest {

  @Mock MeasureMapper measureMapper;
  @Mock Marshaller simpleXmlMarshaller;

  @InjectMocks SimpleXmlService simpleXmlService;

  @Test
  void testMeasureToSimpleXmlNullMeasure() {
    String output = simpleXmlService.measureToSimpleXml(null, null);
    assertThat(output, is(notNullValue()));
    assertThat(output.isBlank(), is(true));
  }

  @Test
  void testMeasureToSimpleXmlValidMeasure() throws JAXBException {
    QdmMeasure measure = QdmMeasure.builder().measureName("TestMeasure1").build();
    MeasureType measureType = new MeasureType();
    MeasureDetailsType measureDetailsType = new MeasureDetailsType();
    measureDetailsType.setTitle("TestMeasure1");
    measureType.setMeasureDetails(measureDetailsType);

    CqlLookups cqlLookups = CqlLookups.builder().build();

    when(measureMapper.measureToMeasureType(any(QdmMeasure.class), any(CqlLookups.class)))
        .thenReturn(measureType);
    Mockito.doAnswer(
            invocationOnMock -> {
              StringWriter writer = invocationOnMock.getArgument(1);
              writer.append("<measure></measure>");
              return null;
            })
        .when(simpleXmlMarshaller)
        .marshal(any(Object.class), any(Writer.class));

    String output = simpleXmlService.measureToSimpleXml(measure, cqlLookups);
    assertThat(output, is(notNullValue()));
    assertThat(output, is(equalTo("<measure></measure>")));
  }

  @Test
  void testGenerateSimpleXmlWhenMarshalingFailed() throws JAXBException {
    String message = "An issue occurred while generating the simple xml for measure";
    CqlLookups cqlLookups = CqlLookups.builder().build();
    QdmMeasure measure = QdmMeasure.builder().measureName("TestMeasure1").build();
    MeasureType measureType = new MeasureType();
    MeasureDetailsType measureDetailsType = new MeasureDetailsType();
    measureDetailsType.setTitle("TestMeasure1");
    measureType.setMeasureDetails(measureDetailsType);

    when(measureMapper.measureToMeasureType(any(QdmMeasure.class), any(CqlLookups.class)))
        .thenReturn(measureType);
    doThrow(new JAXBException(message))
        .when(simpleXmlMarshaller)
        .marshal(any(Object.class), any(Writer.class));
    Exception ex =
        assertThrows(
            PackagingException.class,
            () -> simpleXmlService.measureToSimpleXml(measure, cqlLookups),
            "Exception occurred");
    assertThat(ex.getMessage(), containsString(message));
  }
}
