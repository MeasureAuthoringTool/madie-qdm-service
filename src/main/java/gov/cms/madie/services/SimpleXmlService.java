package gov.cms.madie.services;

import generated.gov.cms.madie.simplexml.MeasureType;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.models.measure.QdmMeasure;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.io.StringWriter;

@Service
@AllArgsConstructor
@Slf4j
public class SimpleXmlService {

  private final MeasureMapper measureMapper;
  private final Marshaller simpleXmlMarshaller;

  public String measureToSimpleXml(QdmMeasure measure, CqlLookups cqlLookups) throws JAXBException {
    StringWriter sw = new StringWriter();
    if (measure != null) {
      MeasureType measureType = measureMapper.measureToMeasureType(measure, cqlLookups);
      JAXBElement<MeasureType> jaxbElement =
          new JAXBElement<>(new QName(null, "measure"), MeasureType.class, measureType);
      simpleXmlMarshaller.marshal(jaxbElement, sw);
    }
    return sw.toString();
  }
}
