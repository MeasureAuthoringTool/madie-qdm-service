package gov.cms.madie.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HumanReadableComponentMeasureModel {
  private String id;
  private String name;
  private String version;
  private String measureSetId;
}
