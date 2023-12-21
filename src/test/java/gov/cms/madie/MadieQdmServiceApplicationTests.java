package gov.cms.madie;

import gov.cms.madie.resources.PackageController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
class MadieQdmServiceApplicationTests {
  @Autowired private PackageController controller;

  @Test
  void contextLoads() {
    assertNotNull(controller);
  }
}
