package gov.cms.madie.hqmf.dto;

import gov.cms.madie.models.measure.Measure;

import gov.cms.madie.hqmf.XmlProcessor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeasureExport {

  private String id;

  private String simpleXml;

  private Measure measure;

  private String humanReadable;

  private String hqmf;

  private String cql;

  private String measureJson;

  private String elmXml;

  private String fhirIncludedLibsJson;

  private XmlProcessor hqmfXmlProcessor;

  private XmlProcessor simpleXmlProcessor;

  private XmlProcessor humanReadableProcessor;

  private String releaseVersion;
}
