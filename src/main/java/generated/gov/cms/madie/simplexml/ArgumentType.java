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
 * Java class for argumentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="argumentType">
 *   <simpleContent>
 *     <extension base="<http://www.w3.org/2001/XMLSchema>string">
 *       <attribute name="argumentName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="qdmDataType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="otherType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     </extension>
 *   </simpleContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "argumentType",
    propOrder = {"value"})
public class ArgumentType {

  @XmlValue protected String value;

  @XmlAttribute(name = "argumentName")
  protected String argumentName;

  @XmlAttribute(name = "id")
  protected String id;

  @XmlAttribute(name = "qdmDataType")
  protected String qdmDataType;

  @XmlAttribute(name = "otherType")
  protected String otherType;

  @XmlAttribute(name = "type")
  protected String type;

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
   * Gets the value of the argumentName property.
   *
   * @return possible object is {@link String }
   */
  public String getArgumentName() {
    return argumentName;
  }

  /**
   * Sets the value of the argumentName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setArgumentName(String value) {
    this.argumentName = value;
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

  /**
   * Gets the value of the qdmDataType property.
   *
   * @return possible object is {@link String }
   */
  public String getQdmDataType() {
    return qdmDataType;
  }

  /**
   * Sets the value of the qdmDataType property.
   *
   * @param value allowed object is {@link String }
   */
  public void setQdmDataType(String value) {
    this.qdmDataType = value;
  }

  /**
   * Gets the value of the otherType property.
   *
   * @return possible object is {@link String }
   */
  public String getOtherType() {
    return otherType;
  }

  /**
   * Sets the value of the otherType property.
   *
   * @param value allowed object is {@link String }
   */
  public void setOtherType(String value) {
    this.otherType = value;
  }

  /**
   * Gets the value of the type property.
   *
   * @return possible object is {@link String }
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the value of the type property.
   *
   * @param value allowed object is {@link String }
   */
  public void setType(String value) {
    this.type = value;
  }
}
