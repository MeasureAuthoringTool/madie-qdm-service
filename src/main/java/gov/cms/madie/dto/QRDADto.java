package gov.cms.madie.dto;

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
public class QRDADto {

  String measure;
  List<TestCase> testCases;
  // TODO waiting for SME feedback
  List<SourceDataCriteria> sourceDataCriteria;
  Object options;
}
