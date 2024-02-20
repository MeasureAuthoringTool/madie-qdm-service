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
