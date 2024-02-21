//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.4
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
//

package generated.gov.cms.madie.simplexml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * Java class for codeSystemType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="codeSystemType">
 *   <simpleContent>
 *     <extension base="<http://www.w3.org/2001/XMLSchema>string">
 *       <attribute name="codeSystem" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="codeSystemName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="codeSystemVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     </extension>
 *   </simpleContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "codeSystemType",
    propOrder = {"value"})
public class CodeSystemType {

  @XmlValue protected String value;

  @XmlAttribute(name = "codeSystem")
  protected String codeSystem;

  @XmlAttribute(name = "codeSystemName")
  protected String codeSystemName;

  @XmlAttribute(name = "codeSystemVersion")
  protected String codeSystemVersion;

  @XmlAttribute(name = "id")
  protected String id;

  /**
   * Gets the value of the value property.
   *
   * @return possible object is {@link String }
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of the value property.
   *
   * @param value allowed object is {@link String }
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets the value of the codeSystem property.
   *
   * @return possible object is {@link String }
   */
  public String getCodeSystem() {
    return codeSystem;
  }

  /**
   * Sets the value of the codeSystem property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCodeSystem(String value) {
    this.codeSystem = value;
  }

  /**
   * Gets the value of the codeSystemName property.
   *
   * @return possible object is {@link String }
   */
  public String getCodeSystemName() {
    return codeSystemName;
  }

  /**
   * Sets the value of the codeSystemName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCodeSystemName(String value) {
    this.codeSystemName = value;
  }

  /**
   * Gets the value of the codeSystemVersion property.
   *
   * @return possible object is {@link String }
   */
  public String getCodeSystemVersion() {
    return codeSystemVersion;
  }

  /**
   * Sets the value of the codeSystemVersion property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCodeSystemVersion(String value) {
    this.codeSystemVersion = value;
  }

  /**
   * Gets the value of the id property.
   *
   * @return possible object is {@link String }
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   *
   * @param value allowed object is {@link String }
   */
  public void setId(String value) {
    this.id = value;
  }
}
