package gov.cms.madie.hqmf;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gov.cms.madie.hqmf.dto.MeasureExport;
import javax.xml.xpath.XPathExpressionException;

@Slf4j
public class HQMFFinalCleanUp {

  /** The Constant reverseEntryCheckFile. */
  private static final String reverseEntryCheckFile = "xsl/final_hqmf_entry_deletion_check.xsl";

  /** The Constant deleteUnUsedEntryFile. */
  private static final String deleteUnUsedEntryFile = "xsl/hqmf_delete_Unused_entry.xsl";

  /**
   * Clean the HQMF for xml entities
   *
   * @param measureExport- instance of MeasureExport
   */
  public static void clean(MeasureExport measureExport) {

    XmlProcessor hqmfProcessor = measureExport.getHqmfXmlProcessor();
    XmlProcessor simpleProcessor = measureExport.getSimpleXmlProcessor();

    if (hqmfProcessor == null) {
      log.debug("HQMF document is null. Aborting clean up.");
      return;
    }

    if (simpleProcessor == null) {
      log.debug("SimpleXML document is null. Aborting clean up.");
      return;
    }

    cleanExtensions(measureExport);
    cleanLocalVariableNames(measureExport);
  }

  /**
   * Clean extensions.
   *
   * @param measureExport- an instance of MeasureExport
   */
  private static void cleanExtensions(MeasureExport measureExport) {

    String xPathForExtensions = "//*/@extension";
    try {
      NodeList extensionsList =
          measureExport
              .getHqmfXmlProcessor()
              .findNodeList(
                  measureExport.getHqmfXmlProcessor().getOriginalDoc(), xPathForExtensions);
      for (int i = 0; i < extensionsList.getLength(); i++) {
        Node extNode = extensionsList.item(i);
        String extValue = extNode.getNodeValue();

        extValue = getReplaceString(extValue);
        extNode.setNodeValue(extValue);
      }

    } catch (XPathExpressionException e) {
      log.error("Exception in HQMFFinalCleanUp.cleanExtensions:" + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Clean local variable names.
   *
   * @param measureExport- an instance of MeasureExport
   */
  private static void cleanLocalVariableNames(MeasureExport measureExport) {

    String xPathForLocalVars = "//localVariableName/@value";
    String xPathForMeasureObValueExpression = "//measureObservationDefinition//value/expression";
    String xPathForMeasureObPreconditionVal = "//measureObservationDefinition//precondition//value";

    try {
      NodeList localVarValuesList =
          measureExport
              .getHqmfXmlProcessor()
              .findNodeList(
                  measureExport.getHqmfXmlProcessor().getOriginalDoc(), xPathForLocalVars);
      NodeList measureObValueExpList =
          measureExport
              .getHqmfXmlProcessor()
              .findNodeList(
                  measureExport.getHqmfXmlProcessor().getOriginalDoc(),
                  xPathForMeasureObValueExpression);
      NodeList measureObPreConditionValList =
          measureExport
              .getHqmfXmlProcessor()
              .findNodeList(
                  measureExport.getHqmfXmlProcessor().getOriginalDoc(),
                  xPathForMeasureObPreconditionVal);
      for (int i = 0; i < localVarValuesList.getLength(); i++) {
        Node extNode = localVarValuesList.item(i);
        String extValue = extNode.getNodeValue();
        extValue = getReplaceString(extValue);
        extNode.setNodeValue(extValue);
      }

      for (int i = 0; i < measureObValueExpList.getLength(); i++) {
        Node expressionNode = measureObValueExpList.item(i);
        String value = expressionNode.getAttributes().getNamedItem("value").getNodeValue();
        value = getReplaceString(value);
        expressionNode.getAttributes().getNamedItem("value").setNodeValue(value);
      }

      for (int i = 0; i < measureObPreConditionValList.getLength(); i++) {
        Node valueNode = measureObPreConditionValList.item(i);
        String value = valueNode.getAttributes().getNamedItem("value").getNodeValue();
        String[] preConditionExpArray = value.split("==");
        String preCondExpValue = null;
        for (String valueToEval : preConditionExpArray) {
          if (preCondExpValue == null) {
            preCondExpValue = getReplaceString(valueToEval);
          } else {
            preCondExpValue = preCondExpValue + "==" + getReplaceString(valueToEval);
          }
        }
        value = getReplaceString(value);
        valueNode.getAttributes().getNamedItem("value").setNodeValue(preCondExpValue);
      }

    } catch (XPathExpressionException e) {
      log.error("Exception in HQMFFinalCleanUp.cleanExtensions:" + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Reverse entry check.
   *
   * @param hqmfXML- hqmf string
   */
  private static void reverseEntryCheck(String hqmfXML) {
    String reverseEntryCheckResults =
        XMLUtility.getInstance()
            .applyXSL(hqmfXML, XMLUtility.getInstance().getXMLResource(reverseEntryCheckFile));
    log.debug("Reverse Entry Check results: " + reverseEntryCheckResults);
  }

  /**
   * Gets the replace string.
   *
   * @param extValue the ext value
   * @return the replace string
   */
  private static String getReplaceString(String extValue) {
    if (extValue.indexOf(">=") > -1) {
      extValue = StringUtils.replace(extValue, ">=", "grtr_thn_eql_");
    }
    if (extValue.indexOf(">") > -1) {
      extValue = StringUtils.replace(extValue, ">", "grtr_thn_");
    }
    if (extValue.indexOf("<=") > -1) {
      extValue = StringUtils.replace(extValue, "<=", "less_thn_eql_");
    }
    if (extValue.indexOf("<") > -1) {
      extValue = StringUtils.replace(extValue, "<", "less_thn_");
    }
    if (extValue.indexOf("=") > -1) {
      extValue = StringUtils.replace(extValue, "=", "eql_");
    }
    if (extValue.contains(",")) {
      extValue = StringUtils.remove(extValue, ',');
    }
    if (extValue.contains("'")) {
      extValue = StringUtils.remove(extValue, "'");
    }
    return extValue;
  }

  /**
   * Delete unused entry. This method is to check and delete Unused QDM Entries in HQMF
   *
   * @param me the me
   */
  private static void deleteUnusedEntry(MeasureExport me) {
    String delDupEntryResults =
        XMLUtility.getInstance()
            .applyXSL(
                me.getHqmfXmlProcessor().transform(me.getHqmfXmlProcessor().getOriginalDoc()),
                XMLUtility.getInstance().getXMLResource(deleteUnUsedEntryFile));

    me.setHqmfXmlProcessor(new XmlProcessor(delDupEntryResults));

    reverseEntryCheck(delDupEntryResults);
  }
}
