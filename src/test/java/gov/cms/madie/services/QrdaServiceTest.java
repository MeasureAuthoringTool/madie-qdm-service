package gov.cms.madie.services;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.madie.Exceptions.QrdaServiceException;
import gov.cms.madie.dto.QRDADto;
import gov.cms.madie.dto.QrdaExportResponseDto;
import gov.cms.madie.dto.QrdaReportDto;
import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.*;
import gov.cms.madie.qrda.CqmMeasure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrdaServiceTest {

  @Mock CqmMeasureMapper mapper;
  @Mock TranslationServiceClient translationServiceClient;
  @Mock QrdaClient client;
  @Mock ObjectMapper objectMapper = new ObjectMapper();
  @InjectMocks QrdaService qrdaService;
  private QdmMeasure qdmMeasure;

  @BeforeEach
  void setUp() {
    qdmMeasure =
        QdmMeasure.builder()
            .id("testId")
            .measureName("Measure1")
            .cqlLibraryName("MeasureLib1")
            .ecqmTitle("MLib1")
            .model(ModelType.QDM_5_6.getValue())
            .measurementPeriodStart(Date.from(Instant.now()))
            .measurementPeriodEnd(Date.from(Instant.now().plus(365, ChronoUnit.DAYS)))
            .testCases(List.of(TestCase.builder().id("1").name("testcase").build()))
            .cql("test cql")
            .build();
  }

  @Test
  void convertToCqmMeasure() throws Exception {
    CqmMeasure cqmMeasure = CqmMeasure.builder().id("1").description("test").build();
    when(translationServiceClient.getTranslatedLibraries(any(String.class), any(String.class)))
        .thenReturn(List.of(TranslatedLibrary.builder().build()));
    when(translationServiceClient.getRelevantDataElements(any(QdmMeasure.class), any(String.class)))
        .thenReturn(List.of(SourceDataCriteria.builder().build()));

    when(mapper.measureToCqmMeasure(any(QdmMeasure.class), any(List.class))).thenReturn(cqmMeasure);

    when(objectMapper.writeValueAsString(any(CqmMeasure.class))).thenReturn(cqmMeasure.toString());

    ArgumentCaptor<QRDADto> captor = ArgumentCaptor.forClass(QRDADto.class);
    List<QrdaReportDto> qrdaExport =
        List.of(QrdaReportDto.builder().qrda("qrda").filename("1_test").report("report").build());
    QrdaExportResponseDto clientResponse =
        QrdaExportResponseDto.builder()
            .summaryReport("summaryReport")
            .individualReports(qrdaExport)
            .build();
    when(client.getQRDA(captor.capture(), eq("testToken"), eq("testId")))
        .thenReturn(clientResponse);

    QrdaExportResponseDto result = qrdaService.generateQrda(qdmMeasure, "testToken");

    QRDADto dto = captor.getValue();
    assertTrue(dto.getMeasure().contains("test"));
    assertFalse(dto.getTestCases().isEmpty());
    assertEquals(1, dto.getTestCases().size());
    assertTrue(dto.getOptions() instanceof Map<?, ?>);
    Map<String, Object> options = (Map<String, Object>) dto.getOptions();
    assertEquals(3, options.size());
    assertTrue(options.containsKey("start_time"));
    assertTrue(options.containsKey("end_time"));
    assertEquals("HQR_IQR", options.get("submission_program"));
    assertEquals(1, dto.getSourceDataCriteria().size());
    assertEquals(1, result.getIndividualReports().size());
    assertEquals(clientResponse.getIndividualReports(), result.getIndividualReports());
    assertEquals(clientResponse.getSummaryReport(), result.getSummaryReport());
  }

  @Test
  void convertToCqmMeasureThrowsException() throws Exception {
    CqmMeasure cqmMeasure = CqmMeasure.builder().id("1").description("test").build();
    when(translationServiceClient.getTranslatedLibraries(any(String.class), any(String.class)))
        .thenReturn(List.of(TranslatedLibrary.builder().build()));
    when(translationServiceClient.getRelevantDataElements(any(QdmMeasure.class), any(String.class)))
        .thenReturn(List.of(SourceDataCriteria.builder().build()));

    when(mapper.measureToCqmMeasure(any(QdmMeasure.class), any(List.class))).thenReturn(cqmMeasure);

    when(objectMapper.writeValueAsString(any(CqmMeasure.class)))
        .thenThrow(new JsonMappingException("error"));

    Exception ex =
        assertThrows(
            QrdaServiceException.class,
            () -> qrdaService.generateQrda(qdmMeasure, "testToken"),
            "Problem mapping the measure for QRDA generation");
    assertThat(ex.getMessage(), containsString("Problem mapping the measure for QRDA generation"));
  }
}
