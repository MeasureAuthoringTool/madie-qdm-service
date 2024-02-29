package gov.cms.madie.services;

import gov.cms.madie.Exceptions.PackagingException;
import gov.cms.madie.hqmf.Generator;
import gov.cms.madie.hqmf.HQMFGeneratorFactory;
import gov.cms.madie.hqmf.dto.MeasureExport;
import gov.cms.madie.models.measure.QdmMeasure;
import jakarta.xml.bind.JAXBException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class HqmfService {

  private final SimpleXmlService simpleXmlService;
  private final HQMFGeneratorFactory hqmfGeneratorFactory;
  private final TranslationServiceClient translationServiceClient;

  public String generateHqmf(QdmMeasure qdmMeasure, String accessToken) {
    String simpleXml;
    // CqlLookups cqlLookups = translationServiceClient.getCqlLookups(qdmMeasure, accessToken);
    try {
      simpleXml = simpleXmlService.measureToSimpleXml(qdmMeasure);
      Generator hqmfGenerator = hqmfGeneratorFactory.getHQMFGenerator();
      MeasureExport measureExport =
          MeasureExport.builder().measure(qdmMeasure).simpleXml(simpleXml).build();
      return hqmfGenerator.generate(measureExport);
    } catch (JAXBException ex) {
      String message =
          "An issue occurred while generating the simple xml for measure: " + qdmMeasure.getId();
      log.error(message, ex);
      throw new PackagingException(message, ex);
    } catch (Exception ex) {
      String message =
          "An issue occurred while generating the HQMF for measure: " + qdmMeasure.getId();
      log.error(message, ex);
      throw new PackagingException(message, ex);
    }
  }
}
