package gov.cms.madie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CQLIncludeLibrary {

  private String id;
  private String aliasName;
  private String cqlLibraryId;
  private String version;
  private String cqlLibraryName;
  private String qdmVersion;
  private String setId;
  private String isComponent;
  private String measureId;
  private String libraryModelType;
}
