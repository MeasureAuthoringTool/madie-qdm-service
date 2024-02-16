package gov.cms.madie.resources;

import gov.cms.madie.Exceptions.UnsupportedModelException;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.services.MeasureMapper;
import gov.cms.madie.services.PackagingService;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.services.SimpleXmlService;
import jakarta.xml.bind.JAXBException;
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
  private final MeasureMapper measureMapper;
  private final SimpleXmlService simpleXmlService;

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
      @RequestBody @Validated(Measure.ValidationSequence.class) QdmMeasure measure)
      throws JAXBException {
    //    StringWriter sw = new StringWriter();
    //    JAXBContext context = JAXBContext.newInstance(MeasureType.class);
    //    Marshaller mar= context.createMarshaller();
    //    mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    //
    //
    //    MeasureType measureType = measureMapper.measureToMeasureType(measure);
    //    mar.marshal(measureType, sw);
    //    log.info("measureType: {}", measureType);
    //    return Map.of("measure", measureType, "simpleXml", sw.getBuffer().toString());
    return simpleXmlService.measureToSimpleXml(measure);
  }
}
