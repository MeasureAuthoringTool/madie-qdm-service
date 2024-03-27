package gov.cms.madie.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SourceDataCriteria implements Comparable<SourceDataCriteria> {
  private String oid;
  private String title;
  private String description;
  private String type;
  private boolean drc;
  // MAT-6210: codeId used for drc
  private String codeId;
  private String name;

  @Override
  public int compareTo(SourceDataCriteria o) {
    return this.oid.compareTo(o.oid);
  }
}
