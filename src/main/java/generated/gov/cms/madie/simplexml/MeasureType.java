//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
//

package generated.gov.cms.madie.simplexml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java class for measureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="measureType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="cqlLookUp" type="{}cqlLookUpType"/>
 *         <element name="supplementalDataElements" type="{}supplementalDataElementsType"/>
 *         <element name="riskAdjustmentVariables" type="{}riskAdjustmentVariablesType"/>
 *         <element name="measureGrouping" type="{}measureGroupingType"/>
 *         <element name="elementLookUp" type="{}elementLookUpType"/>
 *         <element name="allUsedCQLLibs" type="{}allUsedCQLLibsType"/>
 *         <element name="measureDetails" type="{}measureDetailsType"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "measureType",
    propOrder = {
      "cqlLookUp",
      "supplementalDataElements",
      "riskAdjustmentVariables",
      "measureGrouping",
      "elementLookUp",
      "allUsedCQLLibs",
      "measureDetails"
    })
public class MeasureType {

  @XmlElement(required = true)
  protected CqlLookUpType cqlLookUp;

  @XmlElement(required = true)
  protected SupplementalDataElementsType supplementalDataElements;

  @XmlElement(required = true)
  protected RiskAdjustmentVariablesType riskAdjustmentVariables;

  @XmlElement(required = true)
  protected MeasureGroupingType measureGrouping;

  @XmlElement(required = true)
  protected ElementLookUpType elementLookUp;

  @XmlElement(required = true)
  protected AllUsedCQLLibsType allUsedCQLLibs;

  @XmlElement(required = true)
  protected MeasureDetailsType measureDetails;

  /**
   * Gets the value of the cqlLookUp property.
   *
   * @return possible object is {@link CqlLookUpType }
   */
  public CqlLookUpType getCqlLookUp() {
    return cqlLookUp;
  }

  /**
   * Sets the value of the cqlLookUp property.
   *
   * @param value allowed object is {@link CqlLookUpType }
   */
  public void setCqlLookUp(CqlLookUpType value) {
    this.cqlLookUp = value;
  }

  /**
   * Gets the value of the supplementalDataElements property.
   *
   * @return possible object is {@link SupplementalDataElementsType }
   */
  public SupplementalDataElementsType getSupplementalDataElements() {
    return supplementalDataElements;
  }

  /**
   * Sets the value of the supplementalDataElements property.
   *
   * @param value allowed object is {@link SupplementalDataElementsType }
   */
  public void setSupplementalDataElements(SupplementalDataElementsType value) {
    this.supplementalDataElements = value;
  }

  /**
   * Gets the value of the riskAdjustmentVariables property.
   *
   * @return possible object is {@link RiskAdjustmentVariablesType }
   */
  public RiskAdjustmentVariablesType getRiskAdjustmentVariables() {
    return riskAdjustmentVariables;
  }

  /**
   * Sets the value of the riskAdjustmentVariables property.
   *
   * @param value allowed object is {@link RiskAdjustmentVariablesType }
   */
  public void setRiskAdjustmentVariables(RiskAdjustmentVariablesType value) {
    this.riskAdjustmentVariables = value;
  }

  /**
   * Gets the value of the measureGrouping property.
   *
   * @return possible object is {@link MeasureGroupingType }
   */
  public MeasureGroupingType getMeasureGrouping() {
    return measureGrouping;
  }

  /**
   * Sets the value of the measureGrouping property.
   *
   * @param value allowed object is {@link MeasureGroupingType }
   */
  public void setMeasureGrouping(MeasureGroupingType value) {
    this.measureGrouping = value;
  }

  /**
   * Gets the value of the elementLookUp property.
   *
   * @return possible object is {@link ElementLookUpType }
   */
  public ElementLookUpType getElementLookUp() {
    return elementLookUp;
  }

  /**
   * Sets the value of the elementLookUp property.
   *
   * @param value allowed object is {@link ElementLookUpType }
   */
  public void setElementLookUp(ElementLookUpType value) {
    this.elementLookUp = value;
  }

  /**
   * Gets the value of the allUsedCQLLibs property.
   *
   * @return possible object is {@link AllUsedCQLLibsType }
   */
  public AllUsedCQLLibsType getAllUsedCQLLibs() {
    return allUsedCQLLibs;
  }

  /**
   * Sets the value of the allUsedCQLLibs property.
   *
   * @param value allowed object is {@link AllUsedCQLLibsType }
   */
  public void setAllUsedCQLLibs(AllUsedCQLLibsType value) {
    this.allUsedCQLLibs = value;
  }

  /**
   * Gets the value of the measureDetails property.
   *
   * @return possible object is {@link MeasureDetailsType }
   */
  public MeasureDetailsType getMeasureDetails() {
    return measureDetails;
  }

  /**
   * Sets the value of the measureDetails property.
   *
   * @param value allowed object is {@link MeasureDetailsType }
   */
  public void setMeasureDetails(MeasureDetailsType value) {
    this.measureDetails = value;
  }
}
