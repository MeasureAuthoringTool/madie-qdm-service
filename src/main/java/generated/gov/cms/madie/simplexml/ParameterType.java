//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
//

package generated.gov.cms.madie.simplexml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java class for parameterType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="parameterType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="logic" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *       <attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="readOnly" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "parameterType",
    propOrder = {"comment", "logic"})
public class ParameterType {

  @XmlElement(required = true)
  protected String comment;

  @XmlElement(required = true)
  protected String logic;

  @XmlAttribute(name = "id")
  protected String id;

  @XmlAttribute(name = "name")
  protected String name;

  @XmlAttribute(name = "readOnly")
  protected String readOnly;

  /**
   * Gets the value of the comment property.
   *
   * @return possible object is {@link String }
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the value of the comment property.
   *
   * @param value allowed object is {@link String }
   */
  public void setComment(String value) {
    this.comment = value;
  }

  /**
   * Gets the value of the logic property.
   *
   * @return possible object is {@link String }
   */
  public String getLogic() {
    return logic;
  }

  /**
   * Sets the value of the logic property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLogic(String value) {
    this.logic = value;
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
   * Gets the value of the name property.
   *
   * @return possible object is {@link String }
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value allowed object is {@link String }
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Gets the value of the readOnly property.
   *
   * @return possible object is {@link String }
   */
  public String getReadOnly() {
    return readOnly;
  }

  /**
   * Sets the value of the readOnly property.
   *
   * @param value allowed object is {@link String }
   */
  public void setReadOnly(String value) {
    this.readOnly = value;
  }
}
