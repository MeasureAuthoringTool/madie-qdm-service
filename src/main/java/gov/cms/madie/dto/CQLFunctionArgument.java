package gov.cms.madie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CQLFunctionArgument {
  private String id;
  private String argumentName;
  private String argumentType;
  private String otherType;
  private String qdmDataType;
  private String attributeName;

  @JsonProperty("valid")
  private boolean isValid;
}
