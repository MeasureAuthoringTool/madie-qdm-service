package gov.cms.madie.services;

import generated.gov.cms.madie.simplexml.MeasureType;
import gov.cms.madie.models.measure.QdmMeasure;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class SimpleXmlService {

  private final MeasureMapper measureMapper;
  private final Marshaller marshaller;

  public SimpleXmlService(@Autowired MeasureMapper measureMapper) throws JAXBException {
    this.measureMapper = measureMapper;
    JAXBContext context = JAXBContext.newInstance(MeasureType.class);
    marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
  }

  public String measureToSimpleXml(QdmMeasure measure) throws JAXBException {
    MeasureType measureType = measureMapper.measureToMeasureType(measure);
    StringWriter sw = new StringWriter();
    marshaller.marshal(measureType, sw);
    return sw.toString();
  }
}
