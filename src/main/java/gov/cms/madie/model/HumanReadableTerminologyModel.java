package gov.cms.madie.model;

public interface HumanReadableTerminologyModel {
  String getTerminologyDisplay();

  String getDataCriteriaDisplay();

  void setDataCriteriaDisplay(String display);

  void setTerminologyDisplay(String display);

  String getName();

  void setName(String name);

  String getDatatype();

  void setDatatype(String datatype);
}
