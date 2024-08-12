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
    String APPROPRIATE_USE_PROCESS = "Appropriate Use Process";
    String RESOURCE_USE = "Resource Use";
    String EFFICIENCY = "Efficiency";
    String INTERMEDIATE_OUTCOME = "Intermediate Outcome";
    String OUTCOME = "Outcome";
    String EXPERIENCE = "Experience";
    String PATIENT_REPORTED_OUTCOME = "Patient Reported Outcome";
    String PROCESS = "Process";
    String STRUCTURE = "Structure";
  }
}
