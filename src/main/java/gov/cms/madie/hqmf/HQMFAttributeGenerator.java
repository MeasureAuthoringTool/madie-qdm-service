package gov.cms.madie.hqmf;

import gov.cms.madie.hqmf.dto.MeasureExport;

/**
 * @deprecated this class is deprecated since it is an old version of QDM. It should not be
 *     modified.
 */
public class HQMFAttributeGenerator extends HQMFDataCriteriaElementGenerator {

  private MeasureExport measureExport;

  @Override
  public String generate(MeasureExport measureExport) throws Exception {
    return null;
  }

  public MeasureExport getMeasureExport() {
    return measureExport;
  }

  public void setMeasureExport(MeasureExport measureExport) {
    this.measureExport = measureExport;
  }
}
