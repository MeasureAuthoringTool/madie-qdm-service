package gov.cms.madie.resources;

import gov.cms.madie.models.cqm.CqmMeasure;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.packaging.utils.ResourceFileUtil;
import gov.cms.madie.services.CqmConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CqmConversionController.class})
public class CqmConversionControllerMvcTest implements ResourceFileUtil {
  @MockBean private CqmConversionService cqmConversionService;
  @Autowired private MockMvc mockMvc;

  private static final String TEST_USER_ID = "john_doe";
  private static final String TOKEN = "test-okta";

  @Test
  void testConvertMadieMeasureToCqmMeasure() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qdm-test-measure.json");
    // convert to a QdmMeasure
    ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    QdmMeasure qdmMeasure = mapper.readValue(measureJson, QdmMeasure.class);
    when(cqmConversionService.convertMadieMeasureToCqmMeasure(
            any(QdmMeasure.class), any(String.class)))
        .thenReturn(new CqmMeasure());

    ObjectWriter ow =
        JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build()
            .writer()
            .withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(qdmMeasure);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/qdm/measures/cqm")
                .with(user(TEST_USER_ID))
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andReturn();

    verify(cqmConversionService, times(1))
        .convertMadieMeasureToCqmMeasure(any(QdmMeasure.class), anyString());
  }

  @Test
  void testConvertMadieMeasureToCqmMeasureNoScoring() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qdm-test-measure-noscoring.json");
    // convert to a QdmMeasure
    ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    QdmMeasure qdmMeasure = mapper.readValue(measureJson, QdmMeasure.class);
    when(cqmConversionService.convertMadieMeasureToCqmMeasure(
            any(QdmMeasure.class), any(String.class)))
        .thenReturn(new CqmMeasure());

    ObjectWriter ow =
        JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build()
            .writer()
            .withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(qdmMeasure);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/qdm/measures/cqm")
                .with(user(TEST_USER_ID))
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andReturn();

    verify(cqmConversionService, times(1))
        .convertMadieMeasureToCqmMeasure(any(QdmMeasure.class), anyString());
  }

  @Test
  void testConvertMadieMeasureToCqmMeasureUnsupportedModel() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qicore-test-measure.json");
    MvcResult mockResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/qdm/measures/cqm")
                    .with(user(TEST_USER_ID))
                    .with(csrf())
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .content(measureJson)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest())
            .andReturn();
    assertThat(
        mockResult.getResolvedException().getMessage(),
        is(equalTo("Unsupported model type: QI-Core v4.1.1")));
  }
}
