package gov.cms.madie.util;

public interface MadieConstants {

  String DATE_FORMAT = "yyyyMMdd";

  // Scoring Abbreviations
  interface Scoring {
    String COHORT_ABBREVIATION = "COHORT";
    String RATIO_ABBREVIATION = "RATIO";
    String CONTINUOUS_VARIABLE_ABBREVIATION = "CONTVAR";
    String PROPORTION_ABBREVIATION = "PROPOR";
  }

  // INSERT INTO `MEASURE_TYPES` VALUES ('1','Composite','COMPOSITE'),
  // ('10','Appropriate Use Process','APPROPRIATE'),
  // ('2','Cost/Resource Use','RESOURCE'),
  // ('3','Efficiency','EFFICIENCY'),
  // ('4','Outcome','OUTCOME'),
  // ('5','Patient Engagement/Experience','EXPERIENCE'),
  // ('6','Process','PROCESS'),
  // ('7','Structure','STRUCTURE'),
  // ('8','Patient Reported Outcome Performance','PRO-PM'),
  // ('9','Intermediate Clinical Outcome','INTERM-OM');
  interface MeasureType {
    String APPROPRIATE_USE_PROCESS = "APPROPRIATE";
    String COST_OR_RESOURCE_USE = "RESOURCE";
    String EFFICIENCY = "EFFICIENCY";
    String INTERMEDIATE_CLINICAL_OUTCOME = "INTERM-OM";
    String OUTCOME = "OUTCOME";
    String PATIENT_ENGAGEMENT_OR_EXPERIENCE = "EXPERIENCE";
    String PATIENT_REPORTED_OUTCOME = "PRO-PM";
    String PERFORMANCE = "PERFORMANCE";
    String PROCESS = "PROCESS";
    String STRUCTURE = "STRUCTURE";
  }
}
