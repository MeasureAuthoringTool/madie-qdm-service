package gov.cms.madie.hqmf;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * DO NOT DELETE OLDER GENERATORS.
 *
 * <p>On Export, MAT will serve up the latest pre-existing instance in the MEASURE_EXPORT table. If
 * no HQMF exists, then MAT will generate the HQMF during the Export operation and save it to the
 * table. Older versions of the Generators are kept to ensure the HQMF can be generated on-demand.
 */
@Slf4j
public class QDMTemplateProcessorFactory {

  public static XmlProcessor getTemplateProcessor(double qdmVersion) {
    String fileName = "templates/hqmf/qdm_v5_6_datatype_templates.xml";

    URL templateFileUrl = QDMTemplateProcessorFactory.class.getClassLoader().getResource(fileName);
    log.info("Template file path: " + templateFileUrl.getPath());
    File templateFile = null;
    try {
      log.info("Template file uri: " + templateFileUrl.toURI());
      templateFile = new File(templateFileUrl.toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    return new XmlProcessor(templateFile);
  }
}
