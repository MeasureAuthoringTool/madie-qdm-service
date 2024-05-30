package gov.cms.madie.dto.qrda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrdaExportResponseDto {
  String summaryReport;
  List<QrdaReportDTO> individualReports;
}
