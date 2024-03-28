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
public class RatioPopulationMap extends PopulationMap {
  @JsonProperty(value = "IPP")
  private StatementReference IPP;

  @JsonProperty(value = "DENOM")
  private StatementReference DENOM;

  @JsonProperty(value = "NUMER")
  private StatementReference NUMER;

  @JsonProperty(value = "NUMEX")
  private StatementReference NUMEX;

  @JsonProperty(value = "DENEX")
  private StatementReference DENEX;
}
