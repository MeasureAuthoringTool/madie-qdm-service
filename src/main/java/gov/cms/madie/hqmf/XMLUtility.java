package gov.cms.madie.hqmf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

@Slf4j
public class XMLUtility {
  private static final String HTTP_WWW_W3_ORG_2001_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  private static XMLUtility instance;
  private static final String DISALLOW_DOCTYPE_DECL =
      "http://apache.org/xml/features/disallow-doctype-decl";
  private static final String LOAD_EXTERNAL_DTD =
      "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  private static final String EXTERNAL_GENERAL_ENTITIES =
      "http://xml.org/sax/features/external-general-entities";
  private static final String EXTERNAL_PARAMETER_ENTITIES =
      "http://xml.org/sax/features/external-parameter-entities";
  private static final String X_PATH_EXPR_OP_LIMIT = "jdk.xml.xpathExprOpLimit";

  public static XMLUtility getInstance() {
    if (instance == null) {
      instance = new XMLUtility();
    }
    return instance;
  }

  private XMLUtility() {}

  /**
   * Apply xsl.
   *
   * @param input the input
   * @param xslt the xslt
   * @return the string
   */
  public String applyXSL(String input, String xslt) {
    // This system property sets the TransformFactory to use the Saxon TransformerFactoryImpl method
    // This line is needed to prevent issues with XSLT transform.
    System.setProperty(
        "javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
    String result;
    ByteArrayOutputStream outStream;
    outStream = new ByteArrayOutputStream();
    TransformerFactory tFactory = buildTransformerFactory();
    Transformer transformer;
    try {
      transformer = tFactory.newTransformer(new StreamSource(xslt));
      transformer.transform(new StreamSource(new StringReader(input)), new StreamResult(outStream));
    } catch (TransformerConfigurationException e) {
      log.error("An error occurred in transformer", e);
      throw new RuntimeException("Error Configuring XML Transformer:", e.getCause());
    } catch (TransformerException e) {
      log.error("An error occurred in transformer", e);
      throw new RuntimeException("Error Transforming XML:", e.getCause());
    } catch (Exception e) {
      log.error("An error occurred in transformer", e);
    }
    result = outStream.toString();
    return result;
  }

  /**
   * Gets the xML resource.
   *
   * @param name the name
   * @return the xML resource
   */
  public String getXMLResource(String name) {
    try {
      return new ClassPathResource(name).getURL().toExternalForm();
    } catch (IOException e) {
      log.error("An error occurred loading the resource [{}]: ", name, e);
    }
    return null;
  }

  public TransformerFactory buildTransformerFactory() {
    System.setProperty(X_PATH_EXPR_OP_LIMIT, "400");
    TransformerFactory transformerFactory = SAXTransformerFactory.newInstance();

    try {
      transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    } catch (IllegalArgumentException | TransformerConfigurationException e) {
      /* see JDK-8015487, some parsers do not handle these attributes */
    }

    return transformerFactory;
  }

  public DocumentBuilderFactory buildDocumentBuilderFactory() throws ParserConfigurationException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setFeature(DISALLOW_DOCTYPE_DECL, true);
    documentBuilderFactory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
    documentBuilderFactory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
    documentBuilderFactory.setFeature(LOAD_EXTERNAL_DTD, false);
    documentBuilderFactory.setXIncludeAware(false);
    documentBuilderFactory.setExpandEntityReferences(false);
    return documentBuilderFactory;
  }

  public SAXParserFactory buildSaxParserFactory()
      throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    saxParserFactory.setFeature(DISALLOW_DOCTYPE_DECL, true);
    saxParserFactory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
    saxParserFactory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
    saxParserFactory.setFeature(LOAD_EXTERNAL_DTD, false);
    saxParserFactory.setXIncludeAware(false);
    return saxParserFactory;
  }

  public SchemaFactory buildSchemaFactory()
      throws SAXNotRecognizedException, SAXNotSupportedException {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(HTTP_WWW_W3_ORG_2001_XML_SCHEMA);
    schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    return schemaFactory;
  }

  public XMLReader getXMLReader(SAXParser parser) throws SAXException {
    XMLReader reader = parser.getXMLReader();
    reader.setFeature(DISALLOW_DOCTYPE_DECL, true);
    reader.setFeature(LOAD_EXTERNAL_DTD, false);
    reader.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
    reader.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
    return reader;
  }
}
