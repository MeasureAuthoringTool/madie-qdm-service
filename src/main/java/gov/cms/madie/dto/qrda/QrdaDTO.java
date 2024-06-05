package gov.cms.madie.dto.qrda;

import gov.cms.madie.dto.SourceDataCriteria;
import gov.cms.madie.models.measure.TestCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrdaDTO {

  String measure;
  List<TestCase> testCases;
  // TODO waiting for SME feedback
  List<SourceDataCriteria> sourceDataCriteria;
  Object options;
  QrdaGroupDTO[] groupDTOs;
}
