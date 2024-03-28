package gov.cms.madie.qrda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Observation {
  private String id;
  private String aggregation_type;
  private String hqmf_id;
  private StatementReference observation_function;
  private StatementReference observation_parameter;
}
