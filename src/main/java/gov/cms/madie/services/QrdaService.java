package gov.cms.madie.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.madie.Exceptions.QrdaServiceException;
import gov.cms.madie.dto.qrda.QrdaDTO;
import gov.cms.madie.dto.qrda.QrdaExportResponseDto;
import gov.cms.madie.dto.qrda.QrdaRequestDTO;
import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.cqm.CqmMeasure;
import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.QdmMeasure;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
  private final ObjectMapper objectMapper;

  public QrdaExportResponseDto generateQrda(QrdaRequestDTO request, String accessToken) {
    QdmMeasure measure = (QdmMeasure) request.getMeasure();
    // get Libraries
    List<TranslatedLibrary> translatedLibraries =
        translationServiceClient.getTranslatedLibraries(measure.getCql(), accessToken);
    List<String> elms =
        translatedLibraries.stream()
            .map(translatedLibrary -> translatedLibrary.getElmJson())
            .collect(Collectors.toList());

    List<SourceDataCriteria> dataCriteria =
        translationServiceClient.getRelevantDataElements(measure, accessToken);

    QrdaDTO dto = null;
    try {
      CqmMeasure cqmMeasure = mapper.measureToCqmMeasure(measure, elms, null);
      cqmMeasure.setPopulation_sets(new ArrayList<>());
      dto =
          QrdaDTO.builder()
              .measure(objectMapper.writeValueAsString(cqmMeasure))
              .testCases(measure.getTestCases())
              .sourceDataCriteria(dataCriteria)
              .options(buildOptions(measure))
              .groupDTOs(request.getGroupDTOs())
              .build();
    } catch (JsonProcessingException e) {
      throw new QrdaServiceException("Problem mapping the measure for QRDA generation", e);
    }

    // send to qrda
    QrdaExportResponseDto response = client.getQRDA(dto, accessToken, measure.getId());
    if (CollectionUtils.isEmpty(response.getIndividualReports())) {
      throw new QrdaServiceException("No individual reports found for QRDA generation");
    }
    return response;
  }

  private Map<String, Object> buildOptions(QdmMeasure measure) {
    Long startTime =
        Long.parseLong(
            measure
                .getMeasurementPeriodStart()
                .toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

    Long endTime =
        Long.parseLong(
            measure
                .getMeasurementPeriodEnd()
                .toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

    Map<String, Object> options = new HashMap<>();
    options.put("start_time", startTime);
    options.put("end_time", endTime);
    // This is required for QRDA, but is not gathered in MADIE, so it has been defaulted
    options.put("submission_program", "HQR_IQR");

    return options;
  }
}
