package gov.cms.madie.services;

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

    public String generateHqmf(QdmMeasure qdmMeasure) throws Exception {
        Generator hqmfGenerator = hqmfGeneratorFactory.getHQMFGenerator();
        String simpleXml = simpleXmlService.measureToSimpleXml(qdmMeasure);
        MeasureExport measureExport = MeasureExport.builder()
                .measure(qdmMeasure)
                .simpleXml(simpleXml)
                .build();

        return hqmfGenerator.generate(measureExport);
    }
}
