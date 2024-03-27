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
public class ContinuousVariablePopulationMap extends PopulationMap {
  @JsonProperty(value = "IPP")
  private StatementReference IPP;

  @JsonProperty(value = "MSRPOPL")
  private StatementReference MSRPOPL;

  @JsonProperty(value = "MSRPOPLEX")
  private StatementReference MSRPOPLEX;
}
