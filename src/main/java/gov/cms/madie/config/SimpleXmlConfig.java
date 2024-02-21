package gov.cms.madie.config;

import generated.gov.cms.madie.simplexml.MeasureType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleXmlConfig {

  @Bean
  public Marshaller simpleXmlMarshaller() throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(MeasureType.class);
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    return marshaller;
  }
}
