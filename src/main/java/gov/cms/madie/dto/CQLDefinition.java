package gov.cms.madie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CQLDefinition {
  private String id;
  private String uuid;
  private String definitionName;
  private String definitionLogic;
  private String context = "Patient";
  private boolean supplDataElement;
  private boolean popDefinition;
  private String commentString = "";
  private String returnType;
  private String parentLibrary;
  private String libraryDisplayName;
  private String libraryVersion;
  private boolean isFunction;
  private List<CQLFunctionArgument> functionArguments;

  public String getLogic() {
    return getDefinitionLogic();
  }

  public void setLogic(String logic) {
    setDefinitionLogic(logic);
  }
}
