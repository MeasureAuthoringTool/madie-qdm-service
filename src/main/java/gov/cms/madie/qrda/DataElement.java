package gov.cms.madie.qrda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataElement {

  private String codeId;
  private String codeListId;
  private String desc;
  private String description;
}
