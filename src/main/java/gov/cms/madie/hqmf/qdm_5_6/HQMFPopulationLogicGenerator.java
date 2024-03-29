package gov.cms.madie.hqmf.qdm_5_6;

import gov.cms.madie.hqmf.XmlProcessor;
import gov.cms.madie.hqmf.dto.MeasureExport;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

// TODO: Auto-generated Javadoc

/** The Class HQMFPopulationLogicGenerator. */
@Slf4j
public class HQMFPopulationLogicGenerator extends HQMFClauseLogicGenerator {

  private Map<Integer, MeasureGroup> measureGroupingMap = new TreeMap<>();

  /** The scoring type. */
  private String scoringType;

  /** The initial population. */
  private Node initialPopulation;

  /* (non-Javadoc)
   * @see
   * mat.server.simplexml.hqmf.HQMFClauseLogicGenerator#generate(mat.model.clause.MeasureExport)
   */
  @Override
  public String generate(MeasureExport measureExport) throws Exception {
    /*1. Fetch all Clause Logic HQMF in MAP.
      2. Fetch all groupings.
      3. For Each Grouping :
         1.Generate component tag.
      2.Generate populationCriteriaSection tag.
      3.Generate id tag with root and extension as attributes
      4. Generate code tag with code and codesystem as attributes.
      5. Generate title tag with value as attribute.
      6. Generate text tag with value as attribute
      defining the populations used in current section.
      7. For each population used :
         1.Generate component tag as child of populationCriteriaSection with typeCode COMP.
         2.Generate for ex : initialPopulationCriteria tag for IP
         with classCode="OBS" moodCode="EVN" as attributes.
         3.Generate id tag with root and extension as attributes
         4. Generate code tag with ccodeSystem="2.16.840.1.113883.5.4"
         codeSystemName="HL7 Observation Value"
             	    code="IPOP" as attributes.
                  5.Generate displayName as child tag of code with
                  value = name of population as in Simple Xml.
                  6. Generate precondition tag typeCode="PRCN".
                  7. Based on top AND/OR/ANDNOT/ORNOT generate "AllTrue", "AllFalse",
                  "AtLeastOneTrue" tag. Generate id empty tag inside it.
                  8. Generate precondition tag typeCode="PRCN" for all children inside
                  top Logical Op and add criteriaRef to it with id and extension.
    */
    this.measureExport = measureExport;
    getMeasureScoringType(measureExport);
    getAllMeasureGroupings(measureExport);
    generatePopulationCriteria(measureExport);
    return null;
  }

  /**
   * Method to generate population Criteria.
   *
   * @param measureExport - MeasureExport
   * @throws XPathExpressionException - Exception
   */
  private void generatePopulationCriteria(MeasureExport measureExport)
      throws XPathExpressionException {
    for (Integer key : measureGroupingMap.keySet()) {
      Node populationCriteriaComponentElement =
          createPopulationCriteriaSection(key.toString(), measureExport.getHqmfXmlProcessor());
      NodeList groupingChildList = measureGroupingMap.get(key).getPackageClauses();
      log.info("measureGroupingMap: {}", measureGroupingMap);
      log.info("groupingChildList: {}", groupingChildList);

      for (int i = 0; i < groupingChildList.getLength(); i++) {
        String popType =
            groupingChildList.item(i).getAttributes().getNamedItem(TYPE).getNodeValue();
        switch (popType) {
          case "initialPopulation":
            initialPopulation = groupingChildList.item(i);
            generatePopulationTypeCriteria(
                groupingChildList.item(i),
                populationCriteriaComponentElement,
                measureExport,
                "initialPopulationCriteria",
                "IPOP");
            break;
          case "denominator":
            if (checkForRequiredClauseByScoring(
                measureExport, popType, groupingChildList.item(i))) {
              generatePopulationTypeCriteria(
                  groupingChildList.item(i),
                  populationCriteriaComponentElement,
                  measureExport,
                  "denominatorCriteria",
                  "DENOM");
            }
            break;
          case "denominatorExclusions":
            // top Logical Op is OR
            if (checkForRequiredClauseByScoring(
                measureExport, popType, groupingChildList.item(i))) {
              generatePopulationTypeCriteria(
                  groupingChildList.item(i),
                  populationCriteriaComponentElement,
                  measureExport,
                  "denominatorExclusionCriteria",
                  "DENEX");
            }
            break;
          case "denominatorExceptions":
            // top Logical Op is OR
            if (checkForRequiredClauseByScoring(
                measureExport, popType, groupingChildList.item(i))) {
              generatePopulationTypeCriteria(
                  groupingChildList.item(i),
                  populationCriteriaComponentElement,
                  measureExport,
                  "denominatorExceptionCriteria",
                  "DENEXCEP");
            }
            break;
          case "numerator":
            if (checkForRequiredClauseByScoring(
                measureExport, popType, groupingChildList.item(i))) {
              generatePopulationTypeCriteria(
                  groupingChildList.item(i),
                  populationCriteriaComponentElement,
                  measureExport,
                  "numeratorCriteria",
                  "NUMER");
            }
            break;
          case "numeratorExclusions":
            // top Logical Op is OR
            if (checkForRequiredClauseByScoring(
                measureExport, popType, groupingChildList.item(i))) {
              generatePopulationTypeCriteria(
                  groupingChildList.item(i),
                  populationCriteriaComponentElement,
                  measureExport,
                  "numeratorExclusionCriteria",
                  "NUMEX");
            }
            break;
          case "measurePopulation":
            if (checkForRequiredClauseByScoring(
                measureExport, popType, groupingChildList.item(i))) {
              generatePopulationTypeCriteria(
                  groupingChildList.item(i),
                  populationCriteriaComponentElement,
                  measureExport,
                  "measurePopulationCriteria",
                  "MSRPOPL");
            }
            break;
          case "measurePopulationExclusions":
            // If measurePopulationExclusions has no logic added
            // then it should not be included in populationCriteria as per Stan.
            if (checkForRequiredClauseByScoring(
                measureExport, popType, groupingChildList.item(i))) {
              generatePopulationTypeCriteria(
                  groupingChildList.item(i),
                  populationCriteriaComponentElement,
                  measureExport,
                  "measurePopulationExclusionCriteria",
                  "MSRPOPLEX");
            }
            break;
          case "measureObservation":
            break;
          case "stratum":
            if (checkForRequiredClauseByScoring(
                measureExport, popType, groupingChildList.item(i))) {
              generatePopulationTypeCriteria(
                  groupingChildList.item(i),
                  populationCriteriaComponentElement,
                  measureExport,
                  "stratifierCriteria",
                  "STRAT");
            }
            break;
          default:
            // do nothing.
            break;
        }
      }
      // for creating SupplementalDataElements Criteria Section
      createSupplementalDataElmStratifier(
          measureExport, populationCriteriaComponentElement.getFirstChild());
      createRiskAdjustmentStratifier(
          measureExport, populationCriteriaComponentElement.getFirstChild());
      createScoreUnit(
          measureExport,
          populationCriteriaComponentElement.getFirstChild(),
          measureGroupingMap.get(key).getScoreUnit());
    }
  }

  private void createScoreUnit(
      MeasureExport measureExport, Node populationCriteriaSection, String scoreUnit) {
    if (StringUtils.isBlank(scoreUnit)) {
      // No Score Unit provided in measure group
      return;
    }
    Document doc = measureExport.getHqmfXmlProcessor().getOriginalDoc();

    Element componentElement = doc.createElement("component");
    componentElement.setAttribute(TYPE_CODE, "COMP");
    Attr qdmNameSpaceAttr = doc.createAttribute("xmlns:cql-ext");
    qdmNameSpaceAttr.setNodeValue("urn:hhs-cql:hqmf-n1-extensions:v1");
    componentElement.setAttributeNodeNS(qdmNameSpaceAttr);

    Element scoreUnitElement = doc.createElement("cql-ext:scoreUnit");
    scoreUnitElement.setAttribute("nullFlavor", "DER");
    scoreUnitElement.setAttribute("xsi:type", "PQ");

    Element unit = doc.createElement("unit");
    unit.setAttribute("value", scoreUnit);

    scoreUnitElement.appendChild(unit);
    componentElement.appendChild(scoreUnitElement);
    populationCriteriaSection.appendChild(componentElement);
  }

  /**
   * Method to generate default criteriaTag for all population types included in measure grouping.
   *
   * @param item - Node
   * @param populationCriteriaComponentElement - Element
   * @param measureExport - MeasureExport
   * @param criteriaTagName - String.
   * @param criteriaTagCodeName - String code value.
   * @throws XPathExpressionException - Exception
   */
  private void generatePopulationTypeCriteria(
      Node item,
      Node populationCriteriaComponentElement,
      MeasureExport measureExport,
      String criteriaTagName,
      String criteriaTagCodeName)
      throws XPathExpressionException {
    String idExtension;
    /*String displayValue = "";*/
    Document doc = populationCriteriaComponentElement.getOwnerDocument();
    Element populationCriteriaElement =
        (Element) populationCriteriaComponentElement.getFirstChild();
    Element componentElement = doc.createElement("component");
    componentElement.setAttribute(TYPE_CODE, "COMP");
    Element initialPopCriteriaElement = doc.createElement(criteriaTagName);

    if (criteriaTagName.equalsIgnoreCase("stratifierCriteria")) {
      idExtension = "Stratifiers";
      //	displayValue = "Stratification";
    } else {
      initialPopCriteriaElement.setAttribute(CLASS_CODE, "OBS");
      initialPopCriteriaElement.setAttribute(MOOD_CODE, "EVN");
      idExtension =
          StringUtils.deleteWhitespace(item.getAttributes().getNamedItem(TYPE).getNodeValue());
      // displayValue = item.getAttributes().getNamedItem(TYPE).getNodeValue();
    }
    Element idElement = doc.createElement(ID);
    idElement.setAttribute(ROOT, item.getAttributes().getNamedItem(UUID).getNodeValue());
    idElement.setAttribute("extension", idExtension);
    initialPopCriteriaElement.appendChild(idElement);
    Element codeElem = doc.createElement(CODE);
    codeElem.setAttribute(CODE, criteriaTagCodeName);
    codeElem.setAttribute(CODE_SYSTEM, "2.16.840.1.113883.5.4");
    codeElem.setAttribute(CODE_SYSTEM_NAME, "Act Code");
    // displayName inside <code> not needed for populations as per stan
    /*	Element displayNameElement = doc.createElement(DISPLAY_NAME);
    displayNameElement.setAttribute(VALUE, displayValue);
    codeElem.appendChild(displayNameElement);*/
    initialPopCriteriaElement.appendChild(codeElem);
    /*Element preConditionElem = doc.createElement("precondition");
    preConditionElem.setAttribute(TYPE_CODE, "PRCN");*/
    if (item.getChildNodes() != null) {
      for (int i = 0; i < item.getChildNodes().getLength(); i++) {
        generatePopulationLogic(
            initialPopCriteriaElement, item.getChildNodes().item(i), measureExport);
      }
    }
    checkScoringTypeToAssociateIP(initialPopCriteriaElement, item);
    /*initialPopCriteriaElement.appendChild(preConditionElem);*/
    componentElement.appendChild(initialPopCriteriaElement);
    populationCriteriaElement.appendChild(componentElement);
  }

  /**
   * Associate IP with Deno in Proportion Measures, With MeasurePopulation In Continous Variable and
   * based on association in Ratio Measures with Deno and NUm.
   *
   * @param populationCritieriaElem the pre condition elem
   * @param item the item
   */
  private void checkScoringTypeToAssociateIP(Element populationCritieriaElem, Node item) {
    String nodeType = item.getAttributes().getNamedItem(TYPE).getNodeValue();
    Document mainDocument = populationCritieriaElem.getOwnerDocument();
    Element preConditionElem = mainDocument.createElement("subject");
    preConditionElem.setAttribute(TYPE_CODE, "SUBJ");

    if (scoringType.equalsIgnoreCase("Ratio")
        && (nodeType.equalsIgnoreCase("denominator") || nodeType.equalsIgnoreCase("numerator"))) {

      String associatedIPUUID = initialPopulation.getAttributes().getNamedItem(UUID).getNodeValue();

      if (item.getAttributes().getNamedItem("associatedPopulationUUID") != null) {
        associatedIPUUID =
            item.getAttributes().getNamedItem("associatedPopulationUUID").getNodeValue();
      }
      Element criteriaRef = mainDocument.createElement("criteriaReference");
      criteriaRef.setAttribute(CLASS_CODE, "OBS");
      criteriaRef.setAttribute(MOOD_CODE, "EVN");
      Element idElement = mainDocument.createElement(ID);
      idElement.setAttribute(ROOT, associatedIPUUID);
      idElement.setAttribute("extension", StringUtils.deleteWhitespace("initialPopulation"));
      criteriaRef.appendChild(idElement);
      preConditionElem.appendChild(criteriaRef);
      populationCritieriaElem.appendChild(preConditionElem);
    }
  }

  /**
   * Method to generate tags for logic used inside population.
   *
   * @param populationTypeCriteriaElement - Element.
   * @param measureExport - MeasureExport.
   * @throws XPathExpressionException - Exception.
   */
  private void generatePopulationLogic(
      Element populationTypeCriteriaElement, Node childNode, MeasureExport measureExport)
      throws XPathExpressionException {

    String nodeType;
    if (childNode.getAttributes().getNamedItem(TYPE) != null) {
      nodeType = childNode.getAttributes().getNamedItem(TYPE).getNodeValue();
    } else {
      nodeType = childNode.getNodeName();
    }
    Element preConditionElement =
        populationTypeCriteriaElement.getOwnerDocument().createElement("precondition");
    preConditionElement.setAttribute(TYPE_CODE, "PRCN");
    switch (nodeType) {
      case "cqldefinition":
        generateCritRefCQLDefine(
            preConditionElement, childNode, measureExport.getHqmfXmlProcessor());
        break;
      case "comment":
        // skipping comment node as of now.
        break;
      case "cqlfunction":
        break;

      default:
        break;
    }
    if (preConditionElement.hasChildNodes()) {
      populationTypeCriteriaElement.appendChild(preConditionElement);
    }
  }

  /**
   * Method to generate component and populationCriteria default tags.
   *
   * @param sequenceNumber - Measure Grouping sequence number.
   * @param outputProcessor - XmlProcessor.
   * @return - Node.
   */
  private Node createPopulationCriteriaSection(
      String sequenceNumber, XmlProcessor outputProcessor) {
    Element componentElement = outputProcessor.getOriginalDoc().createElement("component");
    Attr nameSpaceAttr = outputProcessor.getOriginalDoc().createAttribute("xmlns:xsi");
    nameSpaceAttr.setNodeValue(nameSpace);
    componentElement.setAttributeNodeNS(nameSpaceAttr);
    Node popCriteriaElem =
        outputProcessor.getOriginalDoc().createElement("populationCriteriaSection");
    Element templateId = outputProcessor.getOriginalDoc().createElement(TEMPLATE_ID);
    popCriteriaElem.appendChild(templateId);
    Element itemChild = outputProcessor.getOriginalDoc().createElement(ITEM);
    itemChild.setAttribute(ROOT, "2.16.840.1.113883.10.20.28.2.7");
    itemChild.setAttribute("extension", POPULATION_CRITERIA_EXTENSION_CQL);
    templateId.appendChild(itemChild);
    Element idElement = outputProcessor.getOriginalDoc().createElement(ID);
    idElement.setAttribute(ROOT, java.util.UUID.randomUUID().toString());
    idElement.setAttribute("extension", "PopulationCriteria" + sequenceNumber);
    popCriteriaElem.appendChild(idElement);
    Element codeElem = outputProcessor.getOriginalDoc().createElement(CODE);
    codeElem.setAttribute(CODE, "57026-7");
    codeElem.setAttribute(CODE_SYSTEM, "2.16.840.1.113883.6.1");
    popCriteriaElem.appendChild(codeElem);
    Element titleElem = outputProcessor.getOriginalDoc().createElement(TITLE);
    titleElem.setAttribute(VALUE, "Population Criteria Section");
    popCriteriaElem.appendChild(titleElem);
    // creating text for PopulationCriteria
    Element textElem = outputProcessor.getOriginalDoc().createElement("text");
    // textElem.setAttribute(VALUE, "Population Criteria text");
    popCriteriaElem.appendChild(textElem);
    componentElement.appendChild(popCriteriaElem);
    outputProcessor.getOriginalDoc().getDocumentElement().appendChild(componentElement);
    return componentElement;
  }

  /**
   * Get Measure Scoring type.
   *
   * @param measureExport - MeasureExport
   * @return the measure scoring type
   * @throws XPathExpressionException - {@link Exception}
   */
  private void getMeasureScoringType(MeasureExport measureExport) throws XPathExpressionException {
    String xPathScoringType = "/measure/measureDetails/scoring/text()";
    javax.xml.xpath.XPath xPath = XPathFactory.newInstance().newXPath();

    scoringType =
        (String)
            xPath.evaluate(
                xPathScoringType,
                measureExport.getSimpleXmlProcessor().getOriginalDoc(),
                XPathConstants.STRING);
  }

  /**
   * Method to populate all measure groupings in measureGroupingMap.
   *
   * @param measureExport - MeasureExport
   * @return the all measure groupings
   * @throws XPathExpressionException - {@link Exception}
   */
  private void getAllMeasureGroupings(MeasureExport measureExport) throws XPathExpressionException {
    String xPath = "/measure/measureGrouping/group";
    NodeList measureGroupings =
        measureExport
            .getSimpleXmlProcessor()
            .findNodeList(measureExport.getSimpleXmlProcessor().getOriginalDoc(), xPath);
    for (int i = 0; i < measureGroupings.getLength(); i++) {
      int measureGroupingSequence =
          Integer.parseInt(
              measureGroupings.item(i).getAttributes().getNamedItem("sequence").getNodeValue());
      Node measureGroupNode = measureGroupings.item(i);
      // Added to remove text nodes with only newlines that make later processing blow up
      XmlProcessor.clean(measureGroupNode);

      MeasureGroup measureGroup =
          MeasureGroup.builder()
              .sequence(measureGroupingSequence)
              .packageClauses(measureGroupNode.getChildNodes())
              .build();

      Node ucum = measureGroupings.item(i).getAttributes().getNamedItem("ucum");
      if (ucum != null && isNotBlank(ucum.getNodeValue().strip())) {
        measureGroup.setScoreUnit(ucum.getNodeValue().strip());
      }

      measureGroupingMap.put(measureGroupingSequence, measureGroup);
    }
  }

  /**
   * Gets the required clauses.
   *
   * @param type the type
   * @return the required clauses
   */
  private static List<String> getRequiredClauses(String type) {
    List<String> list = new ArrayList<>();
    if ("Cohort".equalsIgnoreCase(type)) {
      list.add("initialPopulation");
    } else if ("Continuous Variable".equalsIgnoreCase(type)) {
      list.add("initialPopulation");
      list.add("measurePopulation");
      list.add("measureObservation");
    } else if ("Proportion".equalsIgnoreCase(type) || "Ratio".equalsIgnoreCase(type)) {
      list.add("initialPopulation");
      list.add("denominator");
      list.add("numerator");
    }
    return list;
  }

  /**
   * Check for required clause by scoring.
   *
   * @param me the me
   * @param popType the pop type
   * @param node the node
   * @return true, if successful
   * @throws XPathExpressionException the x path expression exception
   */
  private static boolean checkForRequiredClauseByScoring(
      MeasureExport me, String popType, Node node) throws XPathExpressionException {
    boolean isRequiredClause = false;
    Node scoringType =
        me.getSimpleXmlProcessor()
            .findNode(
                me.getSimpleXmlProcessor().getOriginalDoc(), "/measure/measureDetails/scoring");
    List<String> clauseList = new ArrayList<>();
    clauseList = getRequiredClauses(scoringType.getTextContent());
    if (clauseList.contains(popType)) {
      isRequiredClause = true;
    } else {
      isRequiredClause = checkForPackageClauseLogic(node, popType);
    }
    return isRequiredClause;
  }

  /**
   * Check for package clause logic.
   *
   * @param node the node
   * @param popType the pop type
   * @return true, if successful
   */
  private static boolean checkForPackageClauseLogic(Node node, String popType) {
    switch (popType) {
      case "measurePopulation":
      case "numerator":
      case "denominator":
      case "denominatorExclusions":
      case "numeratorExclusions":
      case "measureObservation":
      case "stratum":
      case "denominatorExceptions":
      case "measurePopulationExclusions":
        if (node.hasChildNodes()) {
          return true;
        }
        break;

      default: // do Nothing
        break;
    }
    return false;
  }

  /**
   * Create the risk adjustment components. This will create a create a component tag underneath
   * population critiera section for risk adjustment variables.
   *
   * @param measureExport the measure export
   * @param parentNode the parent node
   * @throws XPathExpressionException
   */
  private void createRiskAdjustmentStratifier(MeasureExport measureExport, Node parentNode)
      throws XPathExpressionException {

    String xPathForRiskAdjustmentVariables = "/measure/riskAdjustmentVariables/cqldefinition";
    XmlProcessor simpleXmlProcessor = measureExport.getSimpleXmlProcessor();
    NodeList riskAdjustmentVariables =
        simpleXmlProcessor.findNodeList(
            simpleXmlProcessor.getOriginalDoc(), xPathForRiskAdjustmentVariables);
    String xPathForLibraryName = "/measure/cqlLookUp/library";
    Node libraryNode =
        simpleXmlProcessor.findNode(simpleXmlProcessor.getOriginalDoc(), xPathForLibraryName);
    String libraryName = libraryNode.getTextContent();

    String xPathForCQLUUID = "/measure/measureDetails/cqlUUID";
    Node cqluuidNode =
        simpleXmlProcessor.findNode(simpleXmlProcessor.getOriginalDoc(), xPathForCQLUUID);
    String cqlUUID = cqluuidNode.getTextContent();

    for (int i = 0; i < riskAdjustmentVariables.getLength(); i++) {
      Node current = riskAdjustmentVariables.item(i);
      String riskAdjustmentDefName =
          current.getAttributes().getNamedItem("displayName").getNodeValue();

      Element component =
          createRiskAdjustmentComponentNode(
              measureExport, cqlUUID, libraryName, riskAdjustmentDefName, "MSRADJ");
      parentNode.appendChild(component);
    }
  }

  /**
   * Creates the component for a risk adjustment variable in the hqmf document
   *
   * @param measureExport the measure export
   * @param cqlUUID the cql file uuid
   * @param libraryName the cql library name
   * @param riskAdjustmentDefName the risk adjustment definition name
   * @return the component element
   */
  private Element createRiskAdjustmentComponentNode(
      MeasureExport measureExport,
      String cqlUUID,
      String libraryName,
      String riskAdjustmentDefName,
      String type) {
    XmlProcessor processor = measureExport.getHqmfXmlProcessor();

    Element component = processor.getOriginalDoc().createElement("component");
    component.setAttribute(TYPE_CODE, "COMP");

    Attr qdmNameSpaceAttr = processor.getOriginalDoc().createAttribute("xmlns:cql-ext");
    qdmNameSpaceAttr.setNodeValue("urn:hhs-cql:hqmf-n1-extensions:v1");
    component.setAttributeNodeNS(qdmNameSpaceAttr);

    Element stratifierCriteria =
        processor.getOriginalDoc().createElement("cql-ext:supplementalDataElement");

    String extensionStr = "";
    String codeStr = "";
    if (type.equalsIgnoreCase("MSRADJ")) {
      extensionStr = "Risk Adjustment Variables";
      codeStr = "MSRADJ";
    } else {
      extensionStr = "Supplemental Data Elements";
      codeStr = "SDE";
    }

    Element id = processor.getOriginalDoc().createElement("id");
    id.setAttribute("extension", extensionStr);
    id.setAttribute("root", java.util.UUID.randomUUID().toString());
    stratifierCriteria.appendChild(id);

    Element code = processor.getOriginalDoc().createElement("code");
    code.setAttribute("code", codeStr);
    code.setAttribute("codeSystem", "2.16.840.1.113883.5.4");
    code.setAttribute("codeSystemName", "Act Code");
    stratifierCriteria.appendChild(code);

    Element precondition = processor.getOriginalDoc().createElement("precondition");
    precondition.setAttribute("typeCode", "PRCN");
    stratifierCriteria.appendChild(precondition);

    Element criteriaReference = processor.getOriginalDoc().createElement("criteriaReference");
    criteriaReference.setAttribute("moodCode", "EVN");
    criteriaReference.setAttribute("classCode", "OBS");
    precondition.appendChild(criteriaReference);

    Element criteriaReferenceId = processor.getOriginalDoc().createElement("id");
    criteriaReferenceId.setAttribute("root", cqlUUID);
    String extensionString = String.format("%s.\"%s\"", libraryName, riskAdjustmentDefName);
    criteriaReferenceId.setAttribute("extension", extensionString);
    criteriaReference.appendChild(criteriaReferenceId);
    component.appendChild(stratifierCriteria);

    return component;
  }

  /**
   * Creates Logic for Each Supplemental Data Element Nodes.
   *
   * @param measureExport - the measure export
   * @param parentNode - PopulationCriteria First Child Node.
   * @throws XPathExpressionException the x path expression exception
   */
  private void createSupplementalDataElmStratifier(MeasureExport measureExport, Node parentNode)
      throws XPathExpressionException {
    String xpathForOtherSupplementalQDMs = "/measure/supplementalDataElements/cqldefinition/@uuid";
    NodeList supplementalDataElements =
        measureExport
            .getSimpleXmlProcessor()
            .findNodeList(
                measureExport.getSimpleXmlProcessor().getOriginalDoc(),
                xpathForOtherSupplementalQDMs);
    String xPathForLibraryName = "/measure/cqlLookUp/library";
    Node libraryNode =
        measureExport
            .getSimpleXmlProcessor()
            .findNode(measureExport.getSimpleXmlProcessor().getOriginalDoc(), xPathForLibraryName);
    String libraryName = libraryNode.getTextContent();

    String xPathForCQLUUID = "/measure/measureDetails/cqlUUID";
    Node cqluuidNode =
        measureExport
            .getSimpleXmlProcessor()
            .findNode(measureExport.getSimpleXmlProcessor().getOriginalDoc(), xPathForCQLUUID);
    String cqlUUID = cqluuidNode.getTextContent();

    if (supplementalDataElements == null || supplementalDataElements.getLength() < 1) {
      return;
    }
    List<String> supplementalElementRefIds = new ArrayList<>();
    for (int i = 0; i < supplementalDataElements.getLength(); i++) {
      supplementalElementRefIds.add(supplementalDataElements.item(i).getNodeValue());
    }

    StringBuilder uuidXPathString = new StringBuilder();
    for (String uuidString : supplementalElementRefIds) {
      uuidXPathString.append("@id = '").append(uuidString).append("' or ");
    }
    uuidXPathString =
        new StringBuilder(uuidXPathString.substring(0, uuidXPathString.lastIndexOf(" or ")));
    String xpathforOtherSupplementalDataElements =
        "/measure/cqlLookUp/definitions/definition[" + uuidXPathString + "]";
    NodeList supplementalQDMNodeList =
        measureExport
            .getSimpleXmlProcessor()
            .findNodeList(
                measureExport.getSimpleXmlProcessor().getOriginalDoc(),
                xpathforOtherSupplementalDataElements);
    if (supplementalQDMNodeList.getLength() < 1) {
      return;
    }

    for (int i = 0; i < supplementalQDMNodeList.getLength(); i++) {
      Node qdmNode = supplementalQDMNodeList.item(i);
      String qdmName = qdmNode.getAttributes().getNamedItem("name").getNodeValue();

      // createRiskAdjustmentComponentNode is good enough for this too.
      Element componentElement =
          createRiskAdjustmentComponentNode(measureExport, cqlUUID, libraryName, qdmName, "SDE");
      parentNode.appendChild(componentElement);
    }
  }

  @Data
  @Builder
  static class MeasureGroup {
    private NodeList packageClauses;
    private String scoreUnit;
    private int sequence;
  }
}
