package gov.cms.madie.qrda;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CqmMeasure {
  private String id;
  private String hqmf_id;
  private String hqmf_set_id;
  private String hqmf_version_number;
  private String cms_id;

  //  @Builder.Default
  private String title = "";
  //  @Builder.Default
  private String description = "";
  //  @Builder.Default
  private boolean composite = false;
  //  @Builder.Default
  private boolean component = false;
  //  @Builder.Default
  private List<String> component_hqmf_set_ids = new ArrayList<>();
  private String composite_hqmf_set_id;
  //  @Builder.Default
  private String measure_scoring = "PROPORTION";
  //  @Builder.Default
  private String calculation_method = "PATIENT"; // jmapper conversion
  private boolean calculate_sdes;

  private List<CQLLibrary> cql_libraries;
  private String main_cql_library;
  private MeasurePeriod measure_period; // ?
  private List<DataElement> source_data_criteria; // ?
  private List<PopulationSet> population_sets; // ?

  //TODO waiting for SME feedback
  //  private String population_criteria;//?
  //  private List<Attribute> measure_attributes;//?
  //  private List<ValueSet> valueSets = new ArrayList<>(); //?
  //  private List<IndividualResult> calculationResults = new ArrayList<>(); //?
  //
  //  private List<Stratification> all_stratifications; //?

}
