package gov.cms.madie.qrda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementReference {

  private String id;
  private String library_name;
  private String statement_name;
  private String hqmf_id;
}
