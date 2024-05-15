package gov.cms.madie.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gov.cms.madie.Exceptions.CqmConversionException;
import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.cqm.CqmMeasure;
import gov.cms.madie.models.cqm.datacriteria.Diagnosis;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.packaging.utils.ResourceFileUtil;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CqmMeasureMapperTest implements ResourceFileUtil {

  CqmMeasureMapperImpl mapper;

  private QdmMeasure measure;
  private String elm;
  private String elm2;

  private SourceDataCriteria dataCriteria;

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() throws JsonProcessingException {
    mapper = new CqmMeasureMapperImpl();
    elm = getStringFromTestResource("/elm/libraryElm.json");
    elm2 = getStringFromTestResource("/elm/libraryElm2.json");
    String measureString = getStringFromTestResource("/measures/measure.json");
    dataCriteria =
        SourceDataCriteria.builder()
            .oid("test")
            .description("test description")
            .type("Diagnosis")
            .build();

    objectMapper.registerModule(new JavaTimeModule());
    measure = objectMapper.readValue(measureString, QdmMeasure.class);
  }

  @Test
  void measureToCqmMeasure() {

    CqmMeasure result = mapper.measureToCqmMeasure(measure, List.of(elm, elm2), null);
    System.out.println(result);
    assertEquals("65f1ba66df0e11775088e5e0", result.getId());
    assertEquals("123", result.getCms_id());
    assertEquals("7bec3519-c428-4b6f-a483-f01fe9799c85", result.getHqmf_set_id());
    assertEquals("c32e60ac-3118-4bf6-965e-ea2906289f27", result.getHqmf_version_number());
    assertEquals("CMS771", result.getTitle());
    assertEquals(false, result.isComponent());
    assertEquals(false, result.isComposite());
    assertEquals("PROPORTION", result.getMeasure_scoring());
    assertEquals("PATIENT", result.getCalculation_method());
    assertEquals(2, result.getCql_libraries().size());
    assertEquals(
        "UrinarySymptomScoreChangeAfterBenignProstaticHyperplasia", result.getMain_cql_library());
  }

  @Test
  void measureToCqmMeasureWithDataCriteria() {

    CqmMeasure result =
        mapper.measureToCqmMeasure(measure, List.of(elm, elm2), List.of(dataCriteria));
    System.out.println(result);
    assertEquals("65f1ba66df0e11775088e5e0", result.getId());
    assertEquals("123", result.getCms_id());
    assertEquals("7bec3519-c428-4b6f-a483-f01fe9799c85", result.getHqmf_set_id());
    assertEquals("c32e60ac-3118-4bf6-965e-ea2906289f27", result.getHqmf_version_number());
    assertEquals("CMS771", result.getTitle());
    assertEquals(false, result.isComponent());
    assertEquals(false, result.isComposite());
    assertEquals("PROPORTION", result.getMeasure_scoring());
    assertEquals("PATIENT", result.getCalculation_method());
    assertEquals(2, result.getCql_libraries().size());
    assertEquals(
        "UrinarySymptomScoreChangeAfterBenignProstaticHyperplasia", result.getMain_cql_library());
    assertEquals(1, result.getSource_data_criteria().size());
    assertThat(result.getSource_data_criteria().get(0)).isInstanceOf(Diagnosis.class);
    Diagnosis diagnosis = (Diagnosis) result.getSource_data_criteria().get(0);
    assertEquals("test", diagnosis.getCodeListId());
    assertEquals("test description", diagnosis.getDescription());
    assertEquals("Diagnosis", diagnosis.getQdmTitle());
    assertEquals("2.16.840.1.113883.10.20.28.4.110", diagnosis.getHqmfOid());
    assertEquals("2.16.840.1.113883.10.20.24.3.135", diagnosis.getQrdaOid());
    assertEquals("condition", diagnosis.getQdmCategory());
    assertEquals("5.6", diagnosis.getQdmVersion());
    assertEquals("QDM::Diagnosis", diagnosis.get_type());
  }

  @Test
  void measureToCqmMeasureWithDataCriteriaNoMeasureScoring() {
    measure.setScoring(null);
    CqmMeasure result =
        mapper.measureToCqmMeasure(measure, List.of(elm, elm2), List.of(dataCriteria));
    System.out.println(result);
    assertEquals("65f1ba66df0e11775088e5e0", result.getId());
    assertEquals("123", result.getCms_id());
    assertEquals("7bec3519-c428-4b6f-a483-f01fe9799c85", result.getHqmf_set_id());
    assertEquals("c32e60ac-3118-4bf6-965e-ea2906289f27", result.getHqmf_version_number());
    assertEquals("CMS771", result.getTitle());
    assertEquals(false, result.isComponent());
    assertEquals(false, result.isComposite());
    assertNull(result.getMeasure_scoring());
    assertEquals("PATIENT", result.getCalculation_method());
    assertEquals(2, result.getCql_libraries().size());
    assertEquals(
        "UrinarySymptomScoreChangeAfterBenignProstaticHyperplasia", result.getMain_cql_library());
    assertEquals(1, result.getSource_data_criteria().size());
    assertThat(result.getSource_data_criteria().get(0)).isInstanceOf(Diagnosis.class);
    Diagnosis diagnosis = (Diagnosis) result.getSource_data_criteria().get(0);
    assertEquals("test", diagnosis.getCodeListId());
    assertEquals("test description", diagnosis.getDescription());
    assertEquals("Diagnosis", diagnosis.getQdmTitle());
    assertEquals("2.16.840.1.113883.10.20.28.4.110", diagnosis.getHqmfOid());
    assertEquals("2.16.840.1.113883.10.20.24.3.135", diagnosis.getQrdaOid());
    assertEquals("condition", diagnosis.getQdmCategory());
    assertEquals("5.6", diagnosis.getQdmVersion());
    assertEquals("QDM::Diagnosis", diagnosis.get_type());
  }

  @Test
  void measureToCqmMeasureThrowsException() throws Exception {
    SourceDataCriteria badCriteria = SourceDataCriteria.builder().type("bad").build();

    Exception ex =
        assertThrows(
            CqmConversionException.class,
            () -> mapper.measureToCqmMeasure(measure, List.of(elm, elm2), List.of(badCriteria)),
            "Unsupported data type: bad");
    MatcherAssert.assertThat(ex.getMessage(), containsString("Unsupported data type: bad"));
  }
}
