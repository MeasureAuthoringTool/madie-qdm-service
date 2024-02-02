package gov.cms.madie.resources;

import gov.cms.madie.Exceptions.UnsupportedModelException;
import gov.cms.madie.hqmf.Generator;
import gov.cms.madie.hqmf.HQMFGeneratorFactory;
import gov.cms.madie.hqmf.XmlProcessor;
import gov.cms.madie.hqmf.dto.MeasureExport;
import gov.cms.madie.services.PackagingService;
import gov.cms.madie.models.measure.Measure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
  private final HQMFGeneratorFactory hqmfGeneratorFactory;

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
      value = "/hqmf",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
      },
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public String generateHqmf(@RequestBody MeasureExport measureExport) throws Exception {
    // generate HQMF if the model type is QDM
    Measure measure = measureExport.getMeasure();
    if (measure != null && measure.getModel() != null && measure.getModel().contains("QDM")) {
      Generator hqmfGenerator = hqmfGeneratorFactory.getHQMFGenerator();
      return hqmfGenerator.generate(measureExport);
    }
    throw new UnsupportedModelException("Unsupported model type: " + (measure == null ? "NONE" : measure.getModel()));
  }
}
