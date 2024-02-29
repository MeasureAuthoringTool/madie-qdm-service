package gov.cms.madie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CQLDefinition {
  private String id;
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
}
