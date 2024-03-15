package gov.cms.madie.services;

import gov.cms.madie.Exceptions.PackagingException;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.hqmf.Generator;
import gov.cms.madie.hqmf.HQMFGeneratorFactory;
import gov.cms.madie.hqmf.dto.MeasureExport;
import gov.cms.madie.models.measure.QdmMeasure;
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

  public String generateHqmf(QdmMeasure qdmMeasure, CqlLookups cqlLookups) {
    try {
      String simpleXml = simpleXmlService.measureToSimpleXml(qdmMeasure, cqlLookups);
      Generator hqmfGenerator = hqmfGeneratorFactory.getHQMFGenerator();
      MeasureExport measureExport =
          MeasureExport.builder().measure(qdmMeasure).simpleXml(simpleXml).build();
      return hqmfGenerator.generate(measureExport);
    } catch (Exception ex) {
      String message =
          "An issue occurred while generating the HQMF for measure: " + qdmMeasure.getId();
      log.error(message, ex);
      throw new PackagingException(message, ex);
    }
  }
}
