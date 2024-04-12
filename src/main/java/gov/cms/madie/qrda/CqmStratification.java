package gov.cms.madie.qrda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CqmStratification {
  private String id;
  private String title;
  private String stratification_id;
  private String hqmf_id;
  private StatementReference statement;
}
