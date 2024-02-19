package gov.cms.madie.services;

import generated.gov.cms.madie.simplexml.MeasureType;
import gov.cms.madie.models.measure.QdmMeasure;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
@AllArgsConstructor
@Slf4j
public class SimpleXmlService {

  private final MeasureMapper measureMapper;
  private final Marshaller simpleXmlMarshaller;

  public String measureToSimpleXml(QdmMeasure measure) throws JAXBException {
    StringWriter sw = new StringWriter();
    if (measure != null) {
      MeasureType measureType = measureMapper.measureToMeasureType(measure);
      log.info("calling marshal with measureType: {}", measureType);
      log.info("calling marshal with stringWriter: {}", sw);
      simpleXmlMarshaller.marshal(measureType, sw);
    }
    return sw.toString();
  }
}
