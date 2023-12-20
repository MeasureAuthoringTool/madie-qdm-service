package gov.cms.madie.madieqdmservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TranslatedLibrary {
  private String name;
  private String version;
  private String cql;
  private String elmXml;
  private String elmJson;
}
