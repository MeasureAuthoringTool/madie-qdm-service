package gov.cms.madie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HumanReadablePopulationCriteriaModel {
  private String name;
  private String id;
  private int sequence;
  private List<HumanReadablePopulationModel> populations;
  @Builder.Default private String scoreUnit = "";
}
