package gov.cms.madie.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CqlLookupRequest {
  private String cql;
  private Set<String> measureExpressions;
}
