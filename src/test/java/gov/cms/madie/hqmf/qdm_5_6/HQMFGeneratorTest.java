package gov.cms.madie.hqmf.qdm_5_6;

import gov.cms.madie.hqmf.dto.MeasureExport;
import gov.cms.madie.packaging.utils.ResourceFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HQMFGeneratorTest implements ResourceFileUtil {

    @Mock
    HQMFMeasureDetailsGenerator hqmfMeasureDetailsGenerator;
    @Mock
    HQMFDataCriteriaGenerator hqmfDataCriteriaGenerator;

    @InjectMocks
    HQMFGenerator hqmfGenerator;

    @Test
    void generate() throws Exception {
        String simpleXml = getStringFromTestResource("/simplexml/BMAT1644A-v0-0-001-QDM-5-6.xml");
        MeasureExport measureExport = MeasureExport.builder()
                .simpleXml(simpleXml)
                .releaseVersion("1.3.1")
                .build();
        String hqmf = hqmfGenerator.generate(measureExport);
        assertThat(hqmf, is(notNullValue()));
    }
}