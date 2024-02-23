package gov.cms.madie.resources;

import gov.cms.madie.Exceptions.UnsupportedModelException;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.services.HqmfService;
import gov.cms.madie.services.PackagingService;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.services.SimpleXmlService;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/qdm/measures")
@RequiredArgsConstructor
public class PackageController {

  private final PackagingService packagingService;
  private final SimpleXmlService simpleXmlService;
  private final HqmfService hqmfService;

  @PutMapping(
      value = "/package",
      produces = {
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
      },
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public byte[] getMeasurePackage(
      @RequestBody @Validated(Measure.ValidationSequence.class) Measure measure,
      @RequestHeader("Authorization") String accessToken) {
    // generate package if the model type is QDM
    if (measure.getModel() != null && measure.getModel().contains("QDM")) {
      return packagingService.createMeasurePackage(measure, accessToken);
    }
    throw new UnsupportedModelException("Unsupported model type: " + measure.getModel());
  }

  @PutMapping(
      value = "/simple-xml",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
      },
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public String getMeasureSimpleXml(
      @RequestBody @Validated(Measure.ValidationSequence.class) Measure measure)
      throws JAXBException {
    if (measure.getModel() != null && measure.getModel().contains("QDM")) {
      return simpleXmlService.measureToSimpleXml((QdmMeasure) measure);
    }
    throw new UnsupportedModelException("Unsupported model type: " + measure.getModel());
  }

  @PutMapping(
      value = "/hqmf",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
      })
  public ResponseEntity<String> generateHqmf(
      @RequestBody @Validated(Measure.ValidationSequence.class) Measure measure) throws Exception {
    // generate HQMF if the model type is QDM
    if (measure != null && measure.getModel() != null && measure.getModel().contains("QDM")) {
      return ResponseEntity.ok().body(hqmfService.generateHqmf((QdmMeasure) measure));
    }
    throw new UnsupportedModelException(
        "Unsupported model type: "
            + (measure == null || measure.getModel() == null ? "NONE" : measure.getModel()));
  }
}
