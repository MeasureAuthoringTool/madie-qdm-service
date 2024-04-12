package gov.cms.madie.qrda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopulationSet {
  private String id;
  private String title;
  private String population_set_id;
  private PopulationMap populations;
  private List<CqmStratification> stratifications;
  private List<StatementReference> supplemental_data_elements;
  private List<Observation> observations;
}
