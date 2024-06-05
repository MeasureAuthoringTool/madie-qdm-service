package gov.cms.madie.dto.qrda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrdaReportDTO {
  String qrda;
  String report;
  String filename;
}
