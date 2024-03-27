package gov.cms.madie.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.madie.dto.QRDADto;
import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.QdmMeasure;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class QrdaService {

  private final CqmMeasureMapper mapper;
  private final TranslationServiceClient translationServiceClient;
  private final QrdaClient client;

  public List<String> generateQrda(QdmMeasure measure, String accessToken) {
    // get Libraries
    List<TranslatedLibrary> translatedLibraries =
        translationServiceClient.getTranslatedLibraries(measure.getCql(), accessToken);
    List<String> elms =
        translatedLibraries.stream()
            .map(translatedLibrary -> translatedLibrary.getElmJson())
            .collect(Collectors.toList());

    //TODO waiting for SME feedback
    List<SourceDataCriteria> dataCriteria =
        translationServiceClient.getRelevantDataElements(measure, accessToken);

    ObjectMapper objectMapper = new ObjectMapper();
    QRDADto dto = null;
    try {

      Long startTime =
          Long.parseLong(
              measure
                  .getMeasurementPeriodStart()
                  .toInstant()
                  .atZone(ZoneId.systemDefault())
                  .toLocalDateTime()
                  .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

      Long endTime =
          Long.parseLong(
              measure
                  .getMeasurementPeriodEnd()
                  .toInstant()
                  .atZone(ZoneId.systemDefault())
                  .toLocalDateTime()
                  .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

      Map<String, Long> options = new HashMap<>();
      options.put("start_time", startTime);
      options.put("end_time", endTime);
      dto =
          QRDADto.builder()
              .measure(
                  objectMapper.writeValueAsString(
                      mapper.measureToCqmMeasure(measure, elms, dataCriteria)))
              .testCases(measure.getTestCases())
              .sourceDataCriteria(null)
              .options(options)
              .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    // send to qrda
    return client.getQRDA(dto, accessToken);
  }
}
