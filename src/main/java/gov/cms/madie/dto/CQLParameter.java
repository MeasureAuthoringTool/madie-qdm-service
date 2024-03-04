package gov.cms.madie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CQLParameter {
  private String parameterName;
  private String cqlType;
  private String defaultValue;
  private String parameterLogic;
  private String id;
  private boolean readOnly;
  private String commentString;
}
