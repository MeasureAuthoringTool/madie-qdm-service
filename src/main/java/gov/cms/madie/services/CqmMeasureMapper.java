package gov.cms.madie.services;

import gov.cms.madie.models.measure.*;
import gov.cms.madie.qrda.*;
import gov.cms.madie.util.ElmDependencyUtil;
import org.json.JSONObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CqmMeasureMapper {

  @Mapping(target = "hqmf_set_id", source = "measure.measureSetId")
  @Mapping(target = "hqmf_version_number", source = "measure.versionId")
  @Mapping(target = "cms_id", source = "measure.cmsId")
  @Mapping(target = "title", source = "measure.measureName")
  @Mapping(target = "description", source = "measure.measureMetaData.description")
  @Mapping(target = "measure_scoring", expression = "java(measure.getScoring().toUpperCase())")
  @Mapping(target = "main_cql_library", source = "measure.cqlLibraryName")
  @Mapping(target = "calculate_sdes", source = "measure.testCaseConfiguration.sdeIncluded")
  @Mapping(target = "calculation_method", expression = "java(getCalculationMethod(measure))")
  @Mapping(target = "measure_period", expression = "java(getMeasurePeriod(measure))")
  @Mapping(target = "cql_libraries", expression = "java(getCqlLibraries(measure, elms))")
  CqmMeasure measureToCqmMeasure(QdmMeasure measure, List<String> elms);

  default String getCalculationMethod(QdmMeasure measure) {
    return measure.isPatientBasis() ? "PATIENT" : "EPISODE_OF_CARE";
  }

  default MeasurePeriod getMeasurePeriod(QdmMeasure measure) {
    JSONObject low = new JSONObject();
    low.put(
        "value",
        measure
            .getMeasurementPeriodStart()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHH")));

    JSONObject high = new JSONObject();
    high.put(
        "value",
        measure
            .getMeasurementPeriodEnd()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHH")));

    return MeasurePeriod.builder().low(low.toString()).high(high.toString()).build();
  }

  default List<CQLLibrary> getCqlLibraries(QdmMeasure measure, List<String> elms) {

    List<StatementDependency> statements = ElmDependencyUtil.findDependencies(elms, measure.getCqlLibraryName());

    List<StatementDependency> finalStatements = statements;
    return elms.stream()
        .map(elm -> buildCQLLibrary(elm, measure.getCqlLibraryName(), finalStatements))
        .collect(Collectors.toList());
  }

  default CQLLibrary buildCQLLibrary(
      String elm, String measureLibraryName, List<StatementDependency> statementDependencies) {

    JSONObject elmJson = new JSONObject(elm);
    if (elmJson.has("library") && elmJson.getJSONObject("library").has("valueSets")) {
      elmJson
          .getJSONObject("library")
          .getJSONObject("valueSets")
          .getJSONArray("def")
          .forEach(
              item -> {
                JSONObject valueSet = (JSONObject) item;
                valueSet.put("id", valueSet.getString("id").replace("urn:oid:", ""));
              });
    }
    if (elmJson.has("library") && elmJson.getJSONObject("library").has("codeSystems")) {
      elmJson
          .getJSONObject("library")
          .getJSONObject("codeSystems")
          .getJSONArray("def")
          .forEach(
              item -> {
                JSONObject codeSystem = (JSONObject) item;
                codeSystem.put("id", codeSystem.getString("id").replace("urn:oid:", ""));
              });
    }
    CQLLibrary cqlLibrary =
        CQLLibrary.builder()
            .library_name(
                elmJson.getJSONObject("library").getJSONObject("identifier").getString("id"))
            .library_version(
                elmJson.getJSONObject("library").getJSONObject("identifier").getString("version"))
            // true for all non-composite measures
            .is_top_level(true)
            .statement_dependencies(
                statementDependencies.stream()
                    .filter(
                        statementDependency ->
                            statementDependency
                                .getStatement_name()
                                .equals(
                                    elmJson
                                        .getJSONObject("library")
                                        .getJSONObject("identifier")
                                        .getString("id")))
                    .collect(Collectors.toList()))
            .build();

    cqlLibrary.set_main_library(measureLibraryName.equals(cqlLibrary.getLibrary_name()));

    return cqlLibrary;
  }
}
