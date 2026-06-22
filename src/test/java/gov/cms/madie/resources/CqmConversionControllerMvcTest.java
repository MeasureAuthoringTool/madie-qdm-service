package gov.cms.madie.resources;

import gov.cms.madie.models.cqm.CqmMeasure;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.packaging.utils.ResourceFileUtil;
import gov.cms.madie.services.CqmConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import gov.cms.madie.config.SecurityConfig;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

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
@Import(SecurityConfig.class)
public class CqmConversionControllerMvcTest implements ResourceFileUtil {
  @MockitoBean private JwtDecoder jwtDecoder;
  @MockitoBean private CqmConversionService cqmConversionService;
  @Autowired private MockMvc mockMvc;

  private static final String TEST_USER_ID = "john_doe";
  private static final String TOKEN = "test-okta";

  @Test
  void testConvertMadieMeasureToCqmMeasure() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qdm-test-measure.json");
    // convert to a QdmMeasure
    ObjectMapper mapper = JsonMapper.builder().build();
    QdmMeasure qdmMeasure = mapper.readValue(measureJson, QdmMeasure.class);
    when(cqmConversionService.convertMadieMeasureToCqmMeasure(
            any(QdmMeasure.class), any(String.class)))
        .thenReturn(new CqmMeasure());

    ObjectWriter ow = JsonMapper.builder().build().writer().withDefaultPrettyPrinter();
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
    ObjectMapper mapper = JsonMapper.builder().build();
    QdmMeasure qdmMeasure = mapper.readValue(measureJson, QdmMeasure.class);
    when(cqmConversionService.convertMadieMeasureToCqmMeasure(
            any(QdmMeasure.class), any(String.class)))
        .thenReturn(new CqmMeasure());

    ObjectWriter ow = JsonMapper.builder().build().writer().withDefaultPrettyPrinter();
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
