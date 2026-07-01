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
public class CqlLookups {
  private String context;
  private String library;
  private String version;
  private String usingModel;
  private String usingModelVersion;
  private String cqlContext;
  private Set<CQLParameter> parameters;
  private Set<CQLCode> codes;
  private Set<CQLCodeSystem> codeSystems;
  private Set<CQLValueSet> valueSets;
  private Set<CQLDefinition> definitions;
  private Set<CQLIncludeLibrary> includeLibraries;
  private Set<ElementLookup> elementLookups;
}
