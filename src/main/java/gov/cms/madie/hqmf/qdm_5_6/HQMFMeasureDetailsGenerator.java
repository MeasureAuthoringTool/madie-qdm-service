package gov.cms.madie.hqmf.qdm_5_6;


import gov.cms.madie.hqmf.Generator;
import gov.cms.madie.hqmf.XMLUtility;
import gov.cms.madie.hqmf.XmlProcessor;
import gov.cms.madie.hqmf.dto.MeasureExport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
@Service
public class HQMFMeasureDetailsGenerator implements Generator {
	
	private static final String CONVERSION_FILE_FOR_CQL_BASED_HQMF_HEADER = "xsl/qdm_v5_6_measure_details.xsl";

	@Override
	public String generate(MeasureExport me) {
		String simpleXML = me.getSimpleXml();
		String releaseVersion = me.getReleaseVersion();

		simpleXML = addReleaseVersionToSimpleXML(simpleXML);
		String measureDetailsHQMF_XML = XMLUtility.getInstance()
				.applyXSL(simpleXML, XMLUtility.getInstance().getXMLResource(CONVERSION_FILE_FOR_CQL_BASED_HQMF_HEADER));
		measureDetailsHQMF_XML = incrementEndDatebyOne(measureDetailsHQMF_XML);
		return measureDetailsHQMF_XML.replaceAll("xmlns=\"\"", "");
	}
	
	 private String addReleaseVersionToSimpleXML(String simpleXML) {
//		 if(releaseVersion == null || releaseVersion.trim().length() == 0){
//			 return simpleXML;
//		 }

		 // TODO: CREATE CONSTANT Hardcoded to 5.6 for now...probably refactor to accept Measure Export, measure, and pull QDM model from measure?
		 String releaseVersion = "5.6"; // MATPropertiesService.get().getQdmVersion();
		 int measureDetailsTagIndex = simpleXML.indexOf("<measureDetails>");
		 if(measureDetailsTagIndex > -1){
			 simpleXML = simpleXML.substring(0, measureDetailsTagIndex) + "<measureReleaseVersion releaseVersion=\""+releaseVersion + "\"/>" + simpleXML.substring(measureDetailsTagIndex);
		 }
	    
		 return simpleXML;
	 }
	
	/**
	 * Increment end date by one.
	 *
	 * @param measureDetailsHQMF_XML the measure details hqm f_ xml
	 * @return the string
	 */
	private String incrementEndDatebyOne(String measureDetailsHQMF_XML){
		XmlProcessor measureDetailsXml = new XmlProcessor(measureDetailsHQMF_XML);
		String xpathMeasurementPeriodStr = "/QualityMeasureDocument/controlVariable/measurePeriod/value/high";
		try {
			Node endDateHigh = measureDetailsXml.findNode(measureDetailsXml.getOriginalDoc(), 
					xpathMeasurementPeriodStr);
			if(endDateHigh!=null){
				String highEndDate = endDateHigh.getAttributes().getNamedItem("value").getNodeValue();
				highEndDate = formatDate(highEndDate);
				endDateHigh.getAttributes().getNamedItem("value").setNodeValue(highEndDate);
			}
			//to edit default startDate in HQMF measureDetails
			editDefaultStartDate(measureDetailsXml);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

			return measureDetailsXml.transform(measureDetailsXml.getOriginalDoc(), true);
	}
	
	/**
	 * Edits the default start date.
	 *
	 * @param measureDetailsXml the measure details xml
	 */
	private void editDefaultStartDate(XmlProcessor measureDetailsXml) {
		String xpathMeasurementPeriodStr = "/QualityMeasureDocument/controlVariable/measurePeriod/value/phase/low";
		try {
			Node measurementPeriodLowNode = measureDetailsXml.findNode(measureDetailsXml.getOriginalDoc(), 
					xpathMeasurementPeriodStr);
			if(measurementPeriodLowNode!=null){
				String lowDateValue = measurementPeriodLowNode.getAttributes().
						getNamedItem("value").getNodeValue();
				measurementPeriodLowNode.getAttributes().getNamedItem("value").
				setNodeValue(getLowValue(lowDateValue));
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the low value.
	 *
	 * @param lowValue the low value
	 * @return the low value
	 */
	private String getLowValue(String lowValue){
		String mm = lowValue.substring(4,6);
		String dd = lowValue.substring(6,8);
		String year = getCurrentYear();
		// Low date
		return year + mm + dd;
	}
	
	/**
	 * Gets the current year.
	 *
	 * @return the current year
	 */
	private String getCurrentYear(){
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String currentYear = String.valueOf(year);
		return currentYear;
	}
	
	
	/**
	 * Format date.
	 *
	 * @param date the date
	 * @return the string
	 */
	private String formatDate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.add(Calendar.DATE, 1);  // to add one to End Date
		date = sdf.format(c.getTime()); 
		return date;
	}

}
