package gov.cms.madie.services;

import gov.cms.madie.Exceptions.CqmConversionException;
import gov.cms.madie.dto.QrdaExportResponseDto;
import gov.cms.madie.dto.QrdaReportDto;
import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.common.ModelType;
import gov.cms.madie.models.cqm.CqmMeasure;
import gov.cms.madie.models.cqm.datacriteria.basetypes.DataElement;
import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.models.measure.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CqmConversionServiceTest {

  @Mock CqmMeasureMapper mapper;
  @Mock TranslationServiceClient translationServiceClient;
  @InjectMocks CqmConversionService cqmConversionService;
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
  void convertMadieMeasureToCqmMeasure() throws Exception {
    CqmMeasure cqmMeasure =
        CqmMeasure.builder()
            .id("1")
            .description("test")
            .source_data_criteria(List.of(DataElement.builder().id("test_datacriteria").build()))
            .build();
    when(translationServiceClient.getTranslatedLibraries(any(String.class), any(String.class)))
        .thenReturn(List.of(TranslatedLibrary.builder().build()));
    when(translationServiceClient.getRelevantDataElements(any(QdmMeasure.class), any(String.class)))
        .thenReturn(List.of(SourceDataCriteria.builder().build()));

    when(mapper.measureToCqmMeasure(any(QdmMeasure.class), any(List.class), any(List.class)))
        .thenReturn(cqmMeasure);

    List<QrdaReportDto> qrdaExport =
        List.of(QrdaReportDto.builder().qrda("qrda").filename("1_test").report("report").build());
    QrdaExportResponseDto clientResponse =
        QrdaExportResponseDto.builder()
            .summaryReport("summaryReport")
            .individualReports(qrdaExport)
            .build();

    CqmMeasure result =
        cqmConversionService.convertMadieMeasureToCqmMeasure(qdmMeasure, "testToken");

    assertEquals(cqmMeasure.getDescription(), result.getDescription());
    assertEquals(cqmMeasure.getId(), result.getId());
    assertEquals(1, result.getSource_data_criteria().size());
    assertEquals(
        cqmMeasure.getSource_data_criteria().get(0).getId(),
        result.getSource_data_criteria().get(0).getId());
  }

  @Test
  void convertToCqmMeasureThrowsException() throws Exception {
    when(translationServiceClient.getTranslatedLibraries(any(String.class), any(String.class)))
        .thenReturn(List.of(TranslatedLibrary.builder().build()));
    when(translationServiceClient.getRelevantDataElements(any(QdmMeasure.class), any(String.class)))
        .thenReturn(List.of(SourceDataCriteria.builder().build()));

    when(mapper.measureToCqmMeasure(any(QdmMeasure.class), any(List.class), any(List.class)))
        .thenThrow(new CqmConversionException("Unsupported data type"));

    Exception ex =
        assertThrows(
            CqmConversionException.class,
            () -> cqmConversionService.convertMadieMeasureToCqmMeasure(qdmMeasure, "testToken"),
            "Unsupported data type");
    assertThat(ex.getMessage(), containsString("Unsupported data type"));
  }
}
