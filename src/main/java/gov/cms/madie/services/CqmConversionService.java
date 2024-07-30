package gov.cms.madie.services;

import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.cqm.CqmMeasure;
import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.QdmMeasure;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CqmConversionService {

  private final CqmMeasureMapper mapper;
  private final TranslationServiceClient translationServiceClient;

  public CqmMeasure convertMadieMeasureToCqmMeasure(QdmMeasure measure, String accessToken) {
    // get Libraries
    List<TranslatedLibrary> translatedLibraries =
        translationServiceClient.getTranslatedLibraries(measure.getCql(), accessToken);
    List<String> elms =
        translatedLibraries.stream()
            .map(TranslatedLibrary::getElmJson)
            .collect(Collectors.toList());

    List<SourceDataCriteria> dataCriteria =
        translationServiceClient.getRelevantDataElements(measure, accessToken);

    return mapper.measureToCqmMeasure(measure, elms, dataCriteria);
  }
}
