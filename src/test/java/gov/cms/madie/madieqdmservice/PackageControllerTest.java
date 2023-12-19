package gov.cms.madie.madieqdmservice;

import gov.cms.madie.madieqdmservice.resources.PackageController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
public class PackageControllerTest {

  @InjectMocks private PackageController packageController;

  @Test
  public void testGetPackage() {
    assertThat(packageController.getMeasurePackage(), is(equalTo("raw package contents")));
  }
}
