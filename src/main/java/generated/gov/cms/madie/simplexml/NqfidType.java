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
 * Java class for nqfidType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="nqfidType">
 *   <simpleContent>
 *     <extension base="<http://www.w3.org/2001/XMLSchema>string">
 *       <attribute name="extension" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="root" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     </extension>
 *   </simpleContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "nqfidType",
    propOrder = {"value"})
public class NqfidType {

  @XmlValue protected String value;

  @XmlAttribute(name = "extension")
  protected String extension;

  @XmlAttribute(name = "root")
  protected String root;

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
   * Gets the value of the extension property.
   *
   * @return possible object is {@link String }
   */
  public String getExtension() {
    return extension;
  }

  /**
   * Sets the value of the extension property.
   *
   * @param value allowed object is {@link String }
   */
  public void setExtension(String value) {
    this.extension = value;
  }

  /**
   * Gets the value of the root property.
   *
   * @return possible object is {@link String }
   */
  public String getRoot() {
    return root;
  }

  /**
   * Sets the value of the root property.
   *
   * @param value allowed object is {@link String }
   */
  public void setRoot(String value) {
    this.root = value;
  }
}