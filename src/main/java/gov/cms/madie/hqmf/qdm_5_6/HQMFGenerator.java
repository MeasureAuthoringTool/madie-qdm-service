package gov.cms.madie.hqmf.qdm_5_6;

import gov.cms.madie.hqmf.Generator;
import gov.cms.madie.hqmf.HQMFFinalCleanUp;
import gov.cms.madie.hqmf.XmlProcessor;
import gov.cms.madie.hqmf.dto.MeasureExport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The Class CQLbasedHQMFGenerator.
 *
 * @author jmeyer
 */
@Slf4j
@Service
@AllArgsConstructor
public class HQMFGenerator implements Generator {

  private HQMFMeasureDetailsGenerator hqmfMeasureDetailsGenerator;
  private HQMFDataCriteriaGenerator hqmfDataCriteriaGenerator;

  private final Logger logger = LoggerFactory.getLogger(HQMFDataCriteriaGenerator.class);

  /**
   * Generate hqmf for CQL Based measures (QDM version 5.6)
   *
   * @param measureExport- an instance of MeasureExport
   * @return the string
   */
  @Override
  public String generate(MeasureExport measureExport) throws Exception {
    try {
      // MAT 6911: Export CQL based HQMF w/ Meta Data Section
      String hqmfXML = hqmfMeasureDetailsGenerator.generate(measureExport);
      // Inline comments are added after the end of last componentOf tag.
      // This is removed in this method
      hqmfXML = replaceInlineCommentFromEnd(hqmfXML);

      // TODO: would be handled by MAT-6785
      // String dataCriteriaXML = hqmfDataCriteriaGenerator.generate(measureExport);
      // hqmfXML = appendToHQMF(dataCriteriaXML, hqmfXML);

      XmlProcessor hqmfProcessor = new XmlProcessor(hqmfXML);
      measureExport.setHqmfXmlProcessor(hqmfProcessor);

      // generateNarrative(me);
      return finalCleanUp(measureExport);
    } catch (Exception e) {
      logger.error(
          "Unable to generate HQMF for QDM v5.6. Exception Stack Strace is as followed : ");
      throw e;
    }
  }

  /**
   * Inline comments are added after the end of last componentOf tag. This is removed in this method
   *
   * @param eMeasureDetailsXML - String eMeasureDetailsXML.
   * @return String eMeasureDetailsXML.
   */
  private String replaceInlineCommentFromEnd(String eMeasureDetailsXML) {
    int indexOfComponentOf = eMeasureDetailsXML.lastIndexOf("</componentOf>");
    eMeasureDetailsXML = eMeasureDetailsXML.substring(0, indexOfComponentOf);
    eMeasureDetailsXML = eMeasureDetailsXML.concat("</componentOf></QualityMeasureDocument>");
    return eMeasureDetailsXML;
  }

  /**
   * Append to hqmf.
   *
   * @param dataCriteriaXML the data criteria xml
   * @param hqmfXML the hqmf xml
   * @return the string
   */
  private String appendToHQMF(String dataCriteriaXML, String hqmfXML) {
    int indexOfEnd = hqmfXML.indexOf("</QualityMeasureDocument>");
    if (indexOfEnd > -1) {
      hqmfXML = hqmfXML.substring(0, indexOfEnd) + dataCriteriaXML + hqmfXML.substring(indexOfEnd);
    }
    return hqmfXML;
  }

  /**
   * Final clean up.
   *
   * @param measureExport- an instance of MeasureExport
   * @return the string
   */
  private String finalCleanUp(MeasureExport measureExport) {
    HQMFFinalCleanUp.clean(measureExport);
    return removeXmlTagNamespace(
        measureExport
            .getHqmfXmlProcessor()
            .transform(measureExport.getHqmfXmlProcessor().getOriginalDoc(), true));
  }

  /**
   * Removes the xml tag namespace.
   *
   * @param xmlString the xml string
   * @return the string
   */
  private String removeXmlTagNamespace(String xmlString) {
    xmlString = xmlString.replaceAll(" xmlns=\"\"", "").replaceAll("&#34;", "&quot;");
    return xmlString;
  }
}
