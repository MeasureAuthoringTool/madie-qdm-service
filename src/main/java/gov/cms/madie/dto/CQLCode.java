package gov.cms.madie.dto;

import lombok.*;

/** The Class CQLCode. */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CQLCode {
  private String id;
  private String codeName;
  private String codeSystemName;
  private String codeSystemVersion;
  private String codeSystemVersionUri;
  private String codeSystemOID;
  private String codeOID;
  private String displayName;
  @EqualsAndHashCode.Exclude private String codeIdentifier;
  private boolean isUsed;
  private boolean readOnly;
  private String suffix;
  private boolean isCodeSystemVersionIncluded;
  private String isValidatedWithVsac;
}
