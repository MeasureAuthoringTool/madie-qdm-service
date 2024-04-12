package gov.cms.madie.util;

import java.util.*;

import gov.cms.madie.Exceptions.QrdaServiceException;
import gov.cms.madie.qrda.StatementDependency;
import gov.cms.madie.qrda.StatementReference;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
public class ElmDependencyUtil {

  public static List<StatementDependency> findDependencies(
      List<String> cqlLibraryElms, String mainCqlLibraryName) {
    Map<String, List<StatementDependency>> neededElmDepsMap = new HashMap<>();
    Map<String, List<StatementDependency>> allElmsDepMap = new HashMap<>();

    if (cqlLibraryElms == null || cqlLibraryElms.isEmpty()) {
      throw new QrdaServiceException("elm json missing");
    }

    for (String elm : cqlLibraryElms) {

      JSONObject elmJson = new JSONObject(elm);
      if (!elmJson.has("library") || !elmJson.getJSONObject("library").has("identifier")) {
        throw new QrdaServiceException("library or identifier missing");
      }
      String elmId = elmJson.getJSONObject("library").getJSONObject("identifier").getString("id");
      neededElmDepsMap.put(elmId, List.of(buildStatementDependency(elmId, null, null)));
      allElmsDepMap.put(elmId, makeStatementDepsForElm(elmJson));
    }
    neededElmDepsMap.put(mainCqlLibraryName, allElmsDepMap.get(mainCqlLibraryName));
    neededElmDepsMap
        .get(mainCqlLibraryName)
        .forEach(
            statements -> {
              statements
                  .getStatement_references()
                  .forEach(
                      reference ->
                          deepAddExternalLibraryDeps(reference, neededElmDepsMap, allElmsDepMap));
            });
    return neededElmDepsMap.get(mainCqlLibraryName);
  }

  private static List<StatementDependency> makeStatementDepsForElm(JSONObject elmJson) {
    List<StatementDependency> elmDeps = new ArrayList<>();
    Map<String, String> includedLibrariesMap = getIncludedLibrariesMap(elmJson);
    String libraryId = elmJson.getJSONObject("library").getJSONObject("identifier").getString("id");
    generateStatementDepsForElmHelper(elmJson, libraryId, null, elmDeps, includedLibrariesMap);
    return elmDeps;
  }

  private static Map<String, String> getIncludedLibrariesMap(JSONObject elmJson) {
    Map<String, String> includedLibrariesMap = new HashMap<>();
    includedLibrariesMap.put(
        elmJson.getJSONObject("library").getJSONObject("identifier").getString("id"),
        elmJson.getJSONObject("library").getJSONObject("identifier").getString("id"));
    if (elmJson.getJSONObject("library").has("includes")) {
      for (Object def :
          elmJson.getJSONObject("library").getJSONObject("includes").getJSONArray("def")) {
        JSONObject defJson = (JSONObject) def;
        includedLibrariesMap.put(defJson.getString("localIdentifier"), defJson.getString("path"));
      }
    }
    return includedLibrariesMap;
  }

  private static void generateStatementDepsForElmHelper(
      Object obj,
      String libraryId,
      String parentName,
      List<StatementDependency> elmDeps,
      Map<String, String> includedLibrariesMap) {
    if (obj instanceof JSONArray) {
      ((JSONArray) obj)
          .forEach(
              el ->
                  generateStatementDepsForElmHelper(
                      el, libraryId, parentName, elmDeps, includedLibrariesMap));
    } else if (obj instanceof JSONObject jsonObj) {
      List<String> ref = List.of("ExpressionRef", "FunctionRef");
      if (jsonObj.has("type")
          && ref.contains(jsonObj.getString("type"))
          && !"Patient".equals(parentName)
          && null != parentName) {
        if (elmDeps.isEmpty()) {
          elmDeps.add(buildStatementDependency(parentName, libraryId, jsonObj));
        } else {
          elmDeps.stream()
              .map(
                  statementDependency -> {
                    if (parentName.equals(statementDependency.getStatement_name())) {
                      statementDependency
                          .getStatement_references()
                          .add(
                              buildStatementReference(
                                  includedLibrariesMap.getOrDefault(
                                      jsonObj.getString("libraryName"), libraryId),
                                  jsonObj));
                    }
                    return statementDependency;
                  });
        }
      } else if (jsonObj.has("name") && jsonObj.has("expression")) {
        String newParentName = jsonObj.getString("name");
        StatementDependency dep =
            elmDeps.stream()
                .filter(
                    statementDependency ->
                        newParentName.equals(statementDependency.getStatement_name()))
                .findAny()
                .orElseGet(() -> buildStatementDependency(newParentName, null, null));
        elmDeps.add(dep);
      }
      for (String key : jsonObj.keySet()) {
        if (!key.equals("annotation")) {
          generateStatementDepsForElmHelper(
              jsonObj.get(key), libraryId, parentName, elmDeps, includedLibrariesMap);
        }
      }
    }
  }

  private static void deepAddExternalLibraryDeps(
      StatementReference reference,
      Map<String, List<StatementDependency>> neededElmDepsMap,
      Map<String, List<StatementDependency>> allElmsDepMap) {
    String statementLibrary = reference.getLibrary_name();
    String statementName = reference.getStatement_name();
    if (neededElmDepsMap.containsKey(statementLibrary)
        && neededElmDepsMap.get(statementLibrary).stream()
            .anyMatch(
                statementDependency ->
                    statementName.equals(statementDependency.getStatement_name()))) {
      return; // Return if key already exists
    }
    if (!allElmsDepMap.containsKey(statementLibrary)) {
      throw new RuntimeException("Elm library " + statementLibrary + " referenced but not found.");
    }
    if (allElmsDepMap.get(statementLibrary).stream()
        .noneMatch(
            statementDependency -> statementName.equals(statementDependency.getStatement_name()))) {
      throw new RuntimeException(
          "Elm statement '"
              + statementName
              + "' referenced but not found in library '"
              + statementLibrary
              + "'.");
    }
    List<StatementReference> depsToAdd = new ArrayList<>();
    allElmsDepMap.get(statementLibrary).stream()
        .filter(
            statementDependency -> statementName.equals(statementDependency.getStatement_name()))
        .forEach(
            statementDependency -> depsToAdd.addAll(statementDependency.getStatement_references()));
    if (!neededElmDepsMap.containsKey(statementLibrary)) {
      neededElmDepsMap.put(statementLibrary, new ArrayList<>());
    }
    neededElmDepsMap
        .get(statementLibrary)
        .add(
            StatementDependency.builder()
                .statement_name(statementName)
                .statement_references(depsToAdd)
                .build());
    depsToAdd.forEach(dep -> deepAddExternalLibraryDeps(dep, neededElmDepsMap, allElmsDepMap));
  }

  private static StatementDependency buildStatementDependency(
      String parentName, String libraryId, JSONObject jsonObj) {
    List<StatementReference> statementReferences = new ArrayList<>();
    if (jsonObj != null) {
      statementReferences = List.of(buildStatementReference(libraryId, jsonObj));
    }
    return StatementDependency.builder()
        .statement_name(parentName)
        .statement_references(statementReferences)
        .build();
  }

  private static StatementReference buildStatementReference(String libraryId, JSONObject jsonObj) {
    return StatementReference.builder()
        .library_name(libraryId)
        .statement_name(jsonObj.getString("name"))
        .build();
  }
}
