package gov.cms.madie.hqmf;

import gov.cms.madie.hqmf.qdm_5_6.HQMFGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Factory class to select the proper HQMF generator based on QDM model version. */
@Slf4j
@Component
@AllArgsConstructor
public class HQMFGeneratorFactory {

  private HQMFGenerator qdm_5_6_generator;

  public Generator getHQMFGenerator() {
    // Keeping generator around in case we need to support additional versions or implementations in
    // the future
    return qdm_5_6_generator;
  }
}
