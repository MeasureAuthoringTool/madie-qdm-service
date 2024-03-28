package gov.cms.madie.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Identifier {
  String id;
  String version;
}

class Annotation {
  List<Map<String, Object>> statements = new ArrayList<>();
  Identifier identifier = new Identifier();
}
//TODO waiting for SME feedback
public class ElmAnnotationUtil {

  private static ObjectMapper mapper = new ObjectMapper();

  public static String parse(Object elmJson) {
    Annotation ret = new Annotation();
    Map<String, String> localIdToTypeMap = new HashMap<>();
    List<String> elements = List.of("expression", "operand", "suchThat");
    for (String element : elements) {
      String path = String.format("$.[?(@.%s)].[?(@.localId && @.type)]", element);
      JSONArray nodes = JsonPath.read(elmJson.toString(), path);
      for (Object node : nodes) {
        Map<String, Object> nodeMap = (Map<String, Object>) node;
        localIdToTypeMap.put(nodeMap.get("localId").toString(), nodeMap.get("type").toString());
      }
    }
    // Extract library identifier & version
    Map<String, Object> library = JsonPath.read(elmJson.toString(), "$.library.identifier");
    ret.identifier.id = library.get("id").toString();
    ret.identifier.version = library.get("version").toString();

    // All the define statements including functions
    JSONArray definitions =
        JsonPath.read(elmJson.toString(), "$.library.statements.def.[?(@.annotation)]");
    List<HashMap<String, Object>> cropDetailsList =
        definitions.stream()
            .map(
                eachCropJson ->
                    (HashMap<String, Object>) mapper.convertValue(eachCropJson, HashMap.class))
            .collect(Collectors.toList());
    for (HashMap<String, Object> definition : cropDetailsList) {
      List annotation = (ArrayList) definition.get("annotation");
      Map<String, Object> node =
          parseNode((Map<String, Object>) annotation.get(0), localIdToTypeMap);
      node.put("define_name", definition.get("name"));
      ret.statements.add(node);
    }
    return ret.toString();
  }

  private static Map<String, Object> parseNode(
      Map<String, Object> node, Map<String, String> localIdToTypeMap) {
    Map<String, Object> parsedNode = new HashMap<>();
    List<Map<String, Object>> children = new ArrayList<>();
    parsedNode.put("children", children);
    for (Map.Entry<String, Object> entry : node.entrySet()) {
      Object child = entry.getValue();
      if (child == null || entry.getKey().equals("t") || child instanceof String) continue;

      if (child instanceof Map) {
        Map<String, Object> childMap = (Map<String, Object>) child;
        if (childMap.containsKey("value")) {
          Object value = childMap.get("value");
          String text =
              value instanceof List ? String.join(" ", (List<String>) value) : value.toString();
          Map<String, Object> textNode = new HashMap<>();
          textNode.put("text", StringUtils.deleteWhitespace(text));
          List<Map<String, Object>> textChildren = new ArrayList<>();
          textChildren.add(textNode);
          Map<String, Object> textParentNode = new HashMap<>();
          textParentNode.put("children", textChildren);
          children.add(textParentNode);
        } else {
          String nodeType = null;
          if (childMap.containsKey("r")) {
            nodeType = localIdToTypeMap.get(childMap.get("r"));
          }
          Map<String, Object> nextNode = parseNode((Map<String, Object>) child, localIdToTypeMap);
          if (nodeType != null) {
            nextNode.put("node_type", nodeType);
          }
          if (childMap.containsKey("r")) {
            nextNode.put("ref_id", childMap.get("r"));
          }
          children.add(nextNode);
        }
      }
    }
    return parsedNode;
  }
}
