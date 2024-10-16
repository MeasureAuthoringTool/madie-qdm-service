package gov.cms.madie.resources;

import gov.cms.madie.Exceptions.UnsupportedModelException;
import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.dto.qrda.QrdaRequestDTO;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.services.*;
import gov.cms.madie.models.measure.Measure;
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
  private final TranslationServiceClient translationServiceClient;
  private final HumanReadableService humanReadableService;

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
      @RequestBody @Validated(Measure.ValidationSequence.class) Measure measure,
      @RequestHeader("Authorization") String accessToken) {
    if (measure.getModel() != null && measure.getModel().contains("QDM")) {
      QdmMeasure qdmMeasure = (QdmMeasure) measure;
      CqlLookups cqlLookups = translationServiceClient.getCqlLookups(qdmMeasure, accessToken);
      return simpleXmlService.measureToSimpleXml(qdmMeasure, cqlLookups);
    }
    throw new UnsupportedModelException("Unsupported model type: " + measure.getModel());
  }

  @PutMapping(
      value = "/human-readable",
      produces = {
        MediaType.TEXT_HTML_VALUE,
      },
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public String getMeasureHumanReadable(
      @RequestBody @Validated(Measure.ValidationSequence.class) Measure measure,
      @RequestHeader("Authorization") String accessToken) {
    if (measure.getModel() != null && measure.getModel().contains("QDM")) {
      QdmMeasure qdmMeasure = (QdmMeasure) measure;
      CqlLookups cqlLookups = translationServiceClient.getCqlLookups(qdmMeasure, accessToken);
      return humanReadableService.generate(qdmMeasure, cqlLookups);
    }
    throw new UnsupportedModelException("Unsupported model type: " + measure.getModel());
  }

  @PutMapping(
      value = "/hqmf",
      produces = {MediaType.APPLICATION_XML_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> generateHqmf(
      @RequestBody @Validated(Measure.ValidationSequence.class) Measure measure,
      @RequestHeader("Authorization") String accessToken) {
    // generate HQMF if the model type is QDM
    if (measure != null && measure.getModel() != null && measure.getModel().contains("QDM")) {
      QdmMeasure qdmMeasure = (QdmMeasure) measure;
      CqlLookups cqlLookups = translationServiceClient.getCqlLookups(qdmMeasure, accessToken);
      return ResponseEntity.ok().body(hqmfService.generateHqmf((QdmMeasure) measure, cqlLookups));
    }
    throw new UnsupportedModelException(
        "Unsupported model type: "
            + (measure == null || measure.getModel() == null ? "NONE" : measure.getModel()));
  }

  @PutMapping(
      value = "/package/qrda",
      produces = {
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
      },
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public byte[] getQRDA(
      @RequestBody QrdaRequestDTO qrdaRequestDTO,
      @RequestHeader("Authorization") String accessToken) {
    Measure measure = qrdaRequestDTO.getMeasure();
    log.info("export QRDA for measure [{}] ", measure.getId());
    // generate QRDA if the model type is QDM
    if (measure.getModel() != null && measure.getModel().contains("QDM")) {
      return packagingService.createQRDA(qrdaRequestDTO, accessToken);
    }
    throw new UnsupportedModelException("Unsupported model type: " + measure.getModel());
  }
}
