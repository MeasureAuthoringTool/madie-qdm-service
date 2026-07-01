package gov.cms.madie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CqlLookupRequest {
  private String cql;
  private Set<String> measureExpressions;
}
