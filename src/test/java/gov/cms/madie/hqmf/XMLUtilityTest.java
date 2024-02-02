package gov.cms.madie.hqmf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class XMLUtilityTest {

    XMLUtility xmlUtility;

    @BeforeEach
    public void setup() {
        xmlUtility = XMLUtility.getInstance();
    }

    @Test
    void testGetXmlResource() {
        String xmlResource = xmlUtility.getXMLResource("xsl/qdm_v5_6_measure_details.xsl");
        assertThat(xmlResource, is(notNullValue()));
    }

    @Test
    void testBuildTransformerFactory() {
        TransformerFactory transformerFactory = xmlUtility.buildTransformerFactory();
        assertThat(transformerFactory, is(notNullValue()));
    }

    @Test
    void testBuildDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = xmlUtility.buildDocumentBuilderFactory();
        assertThat(documentBuilderFactory, is(notNullValue()));
    }

    @Test
    void testBuildSaxParserFactory() throws SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
        SAXParserFactory saxParserFactory = xmlUtility.buildSaxParserFactory();
        assertThat(saxParserFactory, is(notNullValue()));
    }

    @Test
    void testBuildSchemaFactory() throws SAXNotSupportedException, SAXNotRecognizedException {
        SchemaFactory schemaFactory = xmlUtility.buildSchemaFactory();
        assertThat(schemaFactory, is(notNullValue()));
    }

    @Test
    void testGetXmlReader() throws SAXException {
        SAXParser parser = mock(SAXParser.class);
        XMLReader reader = mock(XMLReader.class);
        when(parser.getXMLReader()).thenReturn(reader);
        XMLReader xmlReader = xmlUtility.getXMLReader(parser);
        assertThat(xmlReader, is(equalTo(reader)));
    }

}