//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.4
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
//

package generated.gov.cms.madie.simplexml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java class for argumentsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="argumentsType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="argument" type="{}argumentType"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "argumentsType",
    propOrder = {"argument"})
public class ArgumentsType {

  @XmlElement(required = true)
  protected ArgumentType argument;

  /**
   * Gets the value of the argument property.
   *
   * @return possible object is {@link ArgumentType }
   */
  public ArgumentType getArgument() {
    return argument;
  }

  /**
   * Sets the value of the argument property.
   *
   * @param value allowed object is {@link ArgumentType }
   */
  public void setArgument(ArgumentType value) {
    this.argument = value;
  }
}