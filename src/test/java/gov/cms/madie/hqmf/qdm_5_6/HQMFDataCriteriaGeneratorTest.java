package gov.cms.madie.hqmf.qdm_5_6;

import gov.cms.madie.hqmf.dto.MeasureExport;
import gov.cms.madie.packaging.utils.ResourceFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
public class HQMFDataCriteriaGeneratorTest implements ResourceFileUtil {
  @InjectMocks private HQMFDataCriteriaGenerator hqmfDataCriteriaGenerator;

  @BeforeEach
  void setup() {}

  @Test
  void testGenerateHqmfWithDataCriteria() throws Exception {
    String simpleXml = getStringFromTestResource("/simplexml/BMAT1644A-v0-0-001-QDM-5-6.xml");
    MeasureExport measureExport =
        MeasureExport.builder().simpleXml(simpleXml).releaseVersion("1.3.1").build();
    String hqmf = hqmfDataCriteriaGenerator.generate(measureExport);
    assertThat(hqmf, is(notNullValue()));
    assertThat(hqmf.contains("<title value=\"Data Criteria Section\"/>"), is(true));
    assertThat(hqmf.contains("<title value=\"Encounter, Performed\"/>"), is(true));
    assertThat(hqmf.contains("<dataCriteriaSection>"), is(true));
    assertThat(hqmf.contains("<populationCriteriaSection>"), is(true));
  }

  @Test
  void testrRemoveOccurrenceFromName() {
    String testString = "Occurrence A_Encounter";
    String formattedString = HQMFDataCriteriaGenerator.removeOccurrenceFromName(testString);
    assertThat(formattedString, is(equalTo("Encounter")));
  }

  @Test
  void testrRemoveOccurrenceFromNameWhenRegexNotPresent() {
    String testString = "Encounter_performed";
    String formattedString = HQMFDataCriteriaGenerator.removeOccurrenceFromName(testString);
    assertThat(formattedString, is(equalTo(testString)));
  }
}
