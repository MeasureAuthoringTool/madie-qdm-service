package gov.cms.madie.util;

import java.util.*;

import gov.cms.madie.qrda.StatementDependency;
import gov.cms.madie.qrda.StatementReference;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
//TODO waiting for SME feedback
@Slf4j
public class ElmDependencyUtil {

  public static List<StatementDependency> findDependencies(
      List<String> cqlLibraryElms, String mainCqlLibraryName) throws Exception {
    Map<String, List<StatementDependency>> neededElmDepsMap = new HashMap<>();
    Map<String, List<StatementDependency>> allElmsDepMap = new HashMap<>();
    for (String elm : cqlLibraryElms) {

      JSONObject elmJson = new JSONObject(elm);
      if (!elmJson.has("library") || !elmJson.getJSONObject("library").has("identifier")) {
        log.warn("library or identifier missing"); // TODO something more meaningful here
      }
      String elmId = elmJson.getJSONObject("library").getJSONObject("identifier").getString("id");
      neededElmDepsMap.put(
          elmId,
          List.of(
              StatementDependency.builder()
                  .statement_name(elmId)
                  .statement_references(new ArrayList<>())
                  .build()));
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
                      reference -> {
                        deepAddExternalLibraryDeps(reference, neededElmDepsMap, allElmsDepMap);
                      });
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
    if (obj instanceof ArrayList) {
      ((ArrayList<?>) obj)
          .forEach(
              el ->
                  generateStatementDepsForElmHelper(
                      el, libraryId, parentName, elmDeps, includedLibrariesMap));
    } else if (obj instanceof JSONObject) {
      JSONObject jsonObj = (JSONObject) obj;
      List<String> ref = List.of("ExpressionRef", "FunctionRef");
      if (jsonObj.has("type")
          && ref.contains(jsonObj.getString("type"))
          && !parentName.equals("Patient")) {
        elmDeps.stream()
            .map(
                statementDependency -> {
                  if (statementDependency.getStatement_name().equals(parentName)) {
                    if (!includedLibrariesMap.containsKey(jsonObj.getString("libraryName"))) {
                      statementDependency
                          .getStatement_references()
                          .add(
                              StatementReference.builder()
                                  .library_name(libraryId)
                                  .statement_name(jsonObj.getString("name"))
                                  .build());
                    } else {
                      statementDependency
                          .getStatement_references()
                          .add(
                              StatementReference.builder()
                                  .library_name(
                                      includedLibrariesMap.get(jsonObj.getString("libraryName")))
                                  .statement_name(jsonObj.getString("name"))
                                  .build());
                    }
                  }
                  return statementDependency;
                });
      } else if (jsonObj.has("name") && jsonObj.has("expression")) {
        String newParentName = jsonObj.getString("name");
        elmDeps.stream()
            .filter(
                statementDependency ->
                    statementDependency.getStatement_name().equals(newParentName))
            .findAny()
            .orElseGet(
                () ->
                    StatementDependency.builder()
                        .statement_name(newParentName)
                        .statement_references(new ArrayList<>())
                        .build());
      }
      //                JSONObject jsonObj = (JSONObject) obj;
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
                    statementDependency.getStatement_name().equals(statementName))) {
      return; // Return if key already exists
    }
    if (!allElmsDepMap.containsKey(statementLibrary)) {
      throw new RuntimeException("Elm library " + statementLibrary + " referenced but not found.");
    }
    if (!allElmsDepMap.get(statementLibrary).stream()
        .anyMatch(
            statementDependency -> statementDependency.getStatement_name().equals(statementName))) {
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
            statementDependency -> statementDependency.getStatement_name().equals(statementName))
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
}
