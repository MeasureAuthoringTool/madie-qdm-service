package gov.cms.madie.hqmf.qdm_5_6;

import gov.cms.madie.hqmf.dto.MeasureExport;
import gov.cms.madie.packaging.utils.ResourceFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class HQMFGeneratorTest implements ResourceFileUtil {

  @Mock HQMFMeasureDetailsGenerator hqmfMeasureDetailsGenerator;
  @Mock HQMFDataCriteriaGenerator hqmfDataCriteriaGenerator;

  @InjectMocks HQMFGenerator hqmfGenerator;

  final String baseHqmf =
      """
<QualityMeasureDocument>
<componentOf >
      <qualityMeasureSet classCode="ACT">
         <id root="1117b927-236b-433d-89aa-a03861956a02"/>
         <title value=""/>
      </qualityMeasureSet>
   </componentOf>
</QualityMeasureDocument>
""";

  final String dataCriteriaXml =
      """
<component xmlns:cql-ext="urn:hhs-cql:hqmf-n1-extensions:v1"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dataCriteriaSection>
     <templateId>
        <item extension="2021-02-01" root="2.16.840.1.113883.10.20.28.2.6"/>
     </templateId>
     <code code="57025-9" codeSystem="2.16.840.1.113883.6.1"/>
     <title value="Data Criteria Section"/>
     <text/>
  </dataCriteriaSection>
</component>
            """;

  @Test
  void testGenerate() throws Exception {
    String simpleXml = getStringFromTestResource("/simplexml/BMAT1644A-v0-0-001-QDM-5-6.xml");
    MeasureExport measureExport =
        MeasureExport.builder().simpleXml(simpleXml).releaseVersion("1.3.1").build();
    when(hqmfMeasureDetailsGenerator.generate(any(MeasureExport.class))).thenReturn(baseHqmf);
    when(hqmfDataCriteriaGenerator.generate(any(MeasureExport.class))).thenReturn(dataCriteriaXml);
    String hqmf = hqmfGenerator.generate(measureExport);
    assertThat(hqmf, is(notNullValue()));
    assertThat(
        hqmf.trim().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"),
        is(true));
    assertThat(hqmf.contains("<dataCriteriaSection>"), is(true));
  }

  @Test
  void testGenerateWhenDataCriteriaGenerationFailed() throws Exception {
    String simpleXml = getStringFromTestResource("/simplexml/BMAT1644A-v0-0-001-QDM-5-6.xml");
    MeasureExport measureExport =
        MeasureExport.builder().simpleXml(simpleXml).releaseVersion("1.3.1").build();
    when(hqmfMeasureDetailsGenerator.generate(any(MeasureExport.class))).thenReturn(baseHqmf);
    String errorMessage = "test";
    Mockito.doThrow(new Exception(errorMessage))
        .when(hqmfDataCriteriaGenerator)
        .generate(any(MeasureExport.class));

    Exception ex =
        Assertions.assertThrows(
            Exception.class, () -> hqmfGenerator.generate(measureExport), errorMessage);
    assertThat(ex.getMessage(), is(equalTo(errorMessage)));
  }
}
