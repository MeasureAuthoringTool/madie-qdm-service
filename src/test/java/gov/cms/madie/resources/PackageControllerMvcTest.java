package gov.cms.madie.resources;

import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.services.HqmfService;
import gov.cms.madie.services.PackagingService;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.packaging.utils.ResourceFileUtil;
import gov.cms.madie.services.SimpleXmlService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({PackageController.class})
class PackageControllerMvcTest implements ResourceFileUtil {

  @MockBean private PackagingService packagingService;
  @MockBean private SimpleXmlService simpleXmlService;
  @MockBean private HqmfService hqmfService;
  @Autowired private MockMvc mockMvc;

  private static final String TEST_USER_ID = "john_doe";
  private static final String TOKEN = "test-okta";

  @Test
  void testGetMeasurePackage() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qdm-test-measure.json");
    Mockito.when(packagingService.createMeasurePackage(new Measure(), TOKEN))
        .thenReturn("measure package".getBytes());
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/qdm/measures/package")
                .with(user(TEST_USER_ID))
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(measureJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andReturn();

    verify(packagingService, times(1)).createMeasurePackage(any(Measure.class), anyString());
  }

  @Test
  void testGetMeasurePackageForUnsupportedModel() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qicore-test-measure.json");
    MvcResult mockResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/qdm/measures/package")
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

  @Test
  void testGetMeasureSimpleXml() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qdm-test-measure.json");
    Mockito.when(simpleXmlService.measureToSimpleXml(any(QdmMeasure.class)))
        .thenReturn("<measure></measure>");
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/qdm/measures/simple-xml")
                .with(user(TEST_USER_ID))
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(measureJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andReturn();
    verify(simpleXmlService, times(1)).measureToSimpleXml(any(QdmMeasure.class));
  }

  @Test
  void testGetMeasureSimpleXmlForUnsupportedModel() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qicore-test-measure.json");
    MvcResult mockResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/qdm/measures/simple-xml")
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

  @Test
  void testGetMeasureHqmf() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qdm-test-measure.json");
    Mockito.when(hqmfService.generateHqmf(any(QdmMeasure.class)))
        .thenReturn("<QualityMeasureDocument></QualityMeasureDocument>");
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/qdm/measures/hqmf")
                    .with(user(TEST_USER_ID))
                    .with(csrf())
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .content(measureJson)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();
    assertThat(
        mvcResult.getResponse().getContentType(), is(equalTo("application/xml;charset=UTF-8")));
    verify(hqmfService, times(1)).generateHqmf(any(QdmMeasure.class));
  }

  @Test
  void testGetMeasureHqmfForUnsupportedModel() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qicore-test-measure.json");
    MvcResult mockResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/qdm/measures/hqmf")
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

  @Test
  void testGetQRDASuccess() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qdm-test-measure.json");
    Mockito.when(packagingService.createQRDA(new Measure(), TOKEN))
        .thenReturn("measure package".getBytes());
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/qdm/measures/package/qrda")
                .with(user(TEST_USER_ID))
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .content(measureJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andReturn();

    verify(packagingService, times(1)).createQRDA(any(Measure.class), anyString());
  }

  @Test
  void testGetQRDAUnsupportedModel() throws Exception {
    String measureJson = getStringFromTestResource("/measures/qicore-test-measure.json");
    MvcResult mockResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/qdm/measures/package/qrda")
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
