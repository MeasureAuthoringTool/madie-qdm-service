//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.4
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
//

package generated.gov.cms.madie.simplexml;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java class for elementLookUpType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="elementLookUpType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="qdm" type="{}qdmType" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "elementLookUpType",
    propOrder = {"qdm"})
public class ElementLookUpType {

  protected List<QdmType> qdm;

  /**
   * Gets the value of the qdm property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the qdm property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   * getQdm().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link QdmType }
   *
   * @return The value of the qdm property.
   */
  public List<QdmType> getQdm() {
    if (qdm == null) {
      qdm = new ArrayList<>();
    }
    return this.qdm;
  }
}