package gov.cms.madie.madieqdmservice.resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/qdm/measures")
@RequiredArgsConstructor
public class PackageController {

  @GetMapping("/package")
  public String getMeasurePackage() {
    // TODO: implementation coming up soon
    return "raw package contents";
  }
}
