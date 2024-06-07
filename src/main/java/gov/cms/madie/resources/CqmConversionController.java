package gov.cms.madie.resources;

import gov.cms.madie.Exceptions.UnsupportedModelException;
import gov.cms.madie.models.cqm.CqmMeasure;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/qdm/measures")
@RequiredArgsConstructor
public class CqmConversionController {

  private final CqmConversionService cqmConversionService;

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handle(HttpMessageNotReadableException e) {
    log.warn("Returning HTTP 400 Bad Request", e);
  }

  @PutMapping(
      value = "/cqm",
      produces = {MediaType.APPLICATION_JSON_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CqmMeasure> convertMadieMeasureToCqmMeasure(
      @RequestBody @Validated(Measure.ValidationSequence.class) Measure measure,
      @RequestHeader("Authorization") String accessToken) {

    if (measure.getModel() != null && measure.getModel().contains("QDM")) {
      return ResponseEntity.ok(cqmConversionService.convertMadieMeasureToCqmMeasure(
          (QdmMeasure) measure, accessToken));
    }
    throw new UnsupportedModelException("Unsupported model type: " + measure.getModel());
  }
}
