package gov.cms.madie.qrda;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CQLLibrary {

  private String library_name;
  private String library_version;
  private String cql;

  //  private String elm;
  //  private String elm_annotations;
  @JsonProperty(value = "is_main_library")
  @Builder.Default
  private boolean is_main_library = false;

  @JsonProperty(value = "is_top_level")
  @Builder.Default
  private boolean is_top_level = true;

  private List<StatementDependency> statement_dependencies;
}
