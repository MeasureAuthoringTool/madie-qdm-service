package gov.cms.madie.qrda;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CohortPopulationMap extends PopulationMap {
  @JsonProperty(value = "IPP")
  private StatementReference IPP;
}
