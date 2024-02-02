package gov.cms.madie.hqmf;

import gov.cms.madie.hqmf.qdm_5_6.HQMFGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class HQMFGeneratorFactoryTest {

    @Mock
    HQMFGenerator qdm_5_6_generator;

    @InjectMocks
    HQMFGeneratorFactory factory;
    @Test
    void getHQMFGenerator() {
        Generator hqmfGenerator = factory.getHQMFGenerator();
        assertThat(hqmfGenerator, is(equalTo(qdm_5_6_generator)));
    }
}