package gov.cms.madie.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CQLCodeSystem {
  private String id;
  private String codeSystem;
  private String codeSystemName;
  private String codeSystemVersion;
  private String OID;

  /**
   * stores off the version uri. example: codesystem "SNOMEDCT:2017-09":
   * 'http://snomed.info/sct/731000124108' version
   * 'http://snomed.info/sct/731000124108/version/201709'
   */
  private String versionUri;
}
