package gov.cms.madie.hqmf.qdm_5_6;

import gov.cms.madie.hqmf.dto.MeasureExport;
import gov.cms.madie.packaging.utils.ResourceFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.not;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@ExtendWith(MockitoExtension.class)
class HQMFMeasureDetailsGeneratorTest implements ResourceFileUtil {
  HQMFMeasureDetailsGenerator hqmfMeasureDetailsGenerator;

  @BeforeEach
  void setUp() {
    hqmfMeasureDetailsGenerator = new HQMFMeasureDetailsGenerator();
  }

  @Test
  void generate() {
    String simpleXml = getStringFromTestResource("/simplexml/BMAT1644A-v0-0-001-QDM-5-6.xml");
    MeasureExport measureExport =
        MeasureExport.builder().simpleXml(simpleXml).releaseVersion("1.3.1").build();
    String hqmf = hqmfMeasureDetailsGenerator.generate(measureExport);
    log.info(hqmf);
    assertThat(hqmf, is(notNullValue()));
    assertThat(hqmf, is(not(equalTo(simpleXml))));
    assertThat(
        hqmf.startsWith(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<QualityMeasureDocument"),
        is(true));
    assertThat(hqmf.contains("<low value=\"20240101\"/>"), is(true));
  }
}
