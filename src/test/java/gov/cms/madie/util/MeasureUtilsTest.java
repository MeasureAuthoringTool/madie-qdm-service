package gov.cms.madie.util;

import gov.cms.madie.models.measure.AggregateMethodType;
import gov.cms.madie.models.measure.DefDescPair;
import gov.cms.madie.models.measure.Group;
import gov.cms.madie.models.measure.MeasureObservation;
import gov.cms.madie.models.measure.Population;
import gov.cms.madie.models.measure.PopulationType;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.models.measure.Stratification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MeasureUtilsTest {

  private QdmMeasure measure;

  @BeforeEach
  void setUp() {
    Stratification strat1 = new Stratification();
    strat1.setId("strat-1");
    strat1.setCqlDefinition("Initial Population");
    Stratification strat2 = new Stratification();
    DefDescPair pair1 =
        DefDescPair.builder().definition("SDE Race").description("race data").build();
    DefDescPair pair2 =
        DefDescPair.builder().definition("SDE Sex").description("gender data").build();
    List<MeasureObservation> observations =
        List.of(
            new MeasureObservation(
                "mo-id-1",
                "Measure Observation",
                "calculate number of episodes",
                null,
                AggregateMethodType.COUNT.getValue()));

    Group group =
        Group.builder()
            .id("xyz-p12r-12ert")
            .populationBasis("Encounter")
            .populations(
                List.of(
                    Population.builder()
                        .id("id-1")
                        .name(PopulationType.INITIAL_POPULATION)
                        .definition("Initial Population")
                        .build(),
                    Population.builder()
                        .id("id-2")
                        .name(PopulationType.MEASURE_POPULATION)
                        .definition("Measure Population")
                        .build(),
                    Population.builder()
                        .id("id-3")
                        .name(PopulationType.MEASURE_POPULATION_EXCLUSION)
                        .build()))
            .stratifications(List.of(strat1, strat2))
            .groupDescription("Description")
            .measureObservations(observations)
            .scoringUnit("test-scoring-unit")
            .build();
    measure =
        QdmMeasure.builder()
            .cqlLibraryName("Test")
            .measureName("Test")
            .groups(List.of(group))
            .supplementalData(List.of(pair1, pair2))
            .build();
  }

  @Test
  void testGetMeasureDefinitionsWhenMeasureIsNull() {
    Set<String> output = MeasureUtils.getMeasureDefinitions(null);
    assertThat(output.size(), is(equalTo(0)));
  }

  @Test
  void testGetMeasureDefinitionsWhenGroupsAreNull() {
    Set<String> output = MeasureUtils.getMeasureDefinitions(QdmMeasure.builder().build());
    assertThat(output.size(), is(equalTo(0)));
  }

  @Test
  void testGetMeasureDefinitions() {
    Set<String> output = MeasureUtils.getMeasureDefinitions(measure);
    assertThat(output.size(), is(equalTo(5)));
  }
}
