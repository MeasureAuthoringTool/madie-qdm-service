package gov.cms.madie.hqmf;

import gov.cms.madie.hqmf.dto.MeasureExport;

public interface Generator extends MatConstants {

  String HIGH = "high";

  String STOP_DATETIME = "stop datetime";

  String START_DATETIME = "start datetime";

  String FLAVOR_ID = "flavorId";

  String LOW = "low";

  String EFFECTIVE_TIME = "effectiveTime";

  String ATTRIBUTE_UUID = "attributeUUID";

  String RELATED_TO = "related to";

  String CHECK_IF_PRESENT = "Check if Present";

  String TYPE = "type";

  String GROUPING_CHECK = "isInGrouping";

  String MOOD = "mood";

  String CLASS = "class";

  String XSI_TYPE = "xsi:type";

  String VALUE = "value";

  String TITLE = "title";

  String DISPLAY_NAME = "displayName";

  String CODE_SYSTEM = "codeSystem";

  String CODE_SYSTEM_NAME = "codeSystemName";

  String CODE_SYSTEM_DISPLAY_NAME = "codeDisplayName";

  String ID = "id";

  String ROOT = "root";

  String ITEM = "item";

  String TEMPLATE_ID = "templateId";

  String MOOD_CODE = "moodCode";

  String CLASS_CODE = "classCode";

  String TYPE_CODE = "typeCode";

  String RAV = "riskAdjVar";

  String OBSERVATION_CRITERIA = "observationCriteria";

  String OUTBOUND_RELATIONSHIP = "outboundRelationship";

  String UUID = "uuid";

  String TAXONOMY = "taxonomy";

  String OID = "oid";

  String NAME = "name";

  String CODE = "code";

  String VERSION_5_0_ID = "2017-05-01";

  String VERSION_4_1_2_ID = "2014-11-24";

  String VERSION_4_3_ID = "2015-09-30";

  String POPULATION_CRITERIA_EXTENSION = "2015-12-01";

  String POPULATION_CRITERIA_EXTENSION_CQL = "2017-08-01";

  String VALUE_SET = "Value Set";

  String ANATOMICAL_LOCATION_SITE = "Anatomical Location Site";

  String ANATOMICAL_APPROACH_SITE = "Anatomical Approach Site";

  String ORDINALITY = "Ordinality";

  String LATERALITY = "Laterality";

  String ROUTE = "route";

  String FACILITY_LOCATION = "facility location";

  String FACILITY_LOCATION_ARRIVAL_DATETIME = "facility location arrival datetime";

  String FACILITY_LOCATION_DEPARTURE_DATETIME = "facility location departure datetime";

  String REFILLS = "refills";

  String CUMULATIVE_MEDICATION_DURATION = "cumulative medication duration";

  String FREQUENCY = "frequency";

  String ADMISSION_DATETIME = "admission datetime";

  String DISCHARGE_STATUS = "discharge status";

  String DISCHARGE_DATETIME = "discharge datetime";

  String REMOVAL_DATETIME = "removal datetime";

  String INCISION_DATETIME = "incision datetime";

  String SIGNED_DATETIME = "signed datetime";

  String ACTIVE_DATETIME = "active datetime";

  String TIME = "time";

  String DATE = "date";

  String ATTRIBUTE_MODE = "attributeMode";

  String ATTRIBUTE_NAME = "attributeName";

  String NEGATION_RATIONALE = "negation rationale";

  String ATTRIBUTE_DATE = "attrDate";

  String nameSpace = "http://www.w3.org/2001/XMLSchema-instance";

  String LESS_THAN = "Less Than";

  String GREATER_THAN = "Greater Than";

  String EQUAL_TO = "Equal To";

  String DOSE = "dose";

  String LENGTH_OF_STAY = "length of stay";

  String TRANSLATION = "translation";

  String NULL_FLAVOR = "nullFlavor";

  String RADIATION_DURATION = "radiation duration";

  String RADIATION_DOSAGE = "radiation dosage";

  String STATUS_CODE = "statusCode";

  String ONSET_DATETIME = "onset datetime";

  String ABATEMENT_DATETIME = "abatement datetime";

  String RECORDED_DATETIME = "recorded datetime";

  String REPEAT_NUMBER = "repeatNumber";

  String ONSET_AGE = "Onset Age";

  String REFERENCE = "reference";

  String RELATIONSHIP = "relationship";

  String DIAGNOSIS = "diagnosis";

  String PRINCIPAL_DIAGNOSIS = "principal diagnosis";

  String ACTION_NEGATION_IND = "actionNegationInd";

  String generate(MeasureExport me) throws Exception;
}
