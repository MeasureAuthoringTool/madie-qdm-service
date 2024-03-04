package gov.cms.madie.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CQLFunctionArgument {
  private String id;
  private String argumentName;
  private String argumentType;
  private String otherType;
  private String qdmDataType;
  private String attributeName;
  private boolean isValid;
}
