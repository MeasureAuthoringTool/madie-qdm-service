package gov.cms.madie.util;

import gov.cms.madie.Exceptions.PackagingException;
import gov.cms.madie.models.measure.BaseConfigurationTypes;
import gov.cms.madie.models.measure.Group;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.MeasureObservation;
import gov.cms.madie.models.measure.MeasureScoring;
import gov.cms.madie.models.measure.Population;
import gov.cms.madie.models.measure.PopulationType;
import gov.cms.madie.models.measure.Stratification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class MappingUtilTest {

  @Test
  void getScoringAbbrCohort() {
    String output = MappingUtil.getScoringAbbr(MeasureScoring.COHORT.toString());
    assertThat(output, is(equalTo(MadieConstants.Scoring.COHORT_ABBREVIATION)));
  }

  @Test
  void getScoringAbbrCV() {
    String output = MappingUtil.getScoringAbbr(MeasureScoring.CONTINUOUS_VARIABLE.toString());
    assertThat(output, is(equalTo(MadieConstants.Scoring.CONTINUOUS_VARIABLE_ABBREVIATION)));
  }

  @Test
  void getScoringAbbrProportion() {
    String output = MappingUtil.getScoringAbbr(MeasureScoring.PROPORTION.toString());
    assertThat(output, is(equalTo(MadieConstants.Scoring.PROPORTION_ABBREVIATION)));
  }

  @Test
  void getScoringAbbrRatio() {
    String output = MappingUtil.getScoringAbbr(MeasureScoring.RATIO.toString());
    assertThat(output, is(equalTo(MadieConstants.Scoring.RATIO_ABBREVIATION)));
  }

  @Test
  void getMeasureTypeIdAppropriate() {
    String output = MappingUtil.getMeasureTypeId(BaseConfigurationTypes.APPROPRIATE_USE_PROCESS);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.APPROPRIATE_USE_PROCESS)));
  }

  @Test
  void getMeasureTypeIdResource() {
    String output = MappingUtil.getMeasureTypeId(BaseConfigurationTypes.COST_OR_RESOURCE_USE);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.COST_OR_RESOURCE_USE)));
  }

  @Test
  void getMeasureTypeIdEfficiency() {
    String output = MappingUtil.getMeasureTypeId(BaseConfigurationTypes.EFFICIENCY);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.EFFICIENCY)));
  }

  @Test
  void getMeasureTypeIdIntermOm() {
    String output =
        MappingUtil.getMeasureTypeId(BaseConfigurationTypes.INTERMEDIATE_CLINICAL_OUTCOME);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.INTERMEDIATE_CLINICAL_OUTCOME)));
  }

  @Test
  void getMeasureTypeIdOutcome() {
    String output = MappingUtil.getMeasureTypeId(BaseConfigurationTypes.OUTCOME);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.OUTCOME)));
  }

  @Test
  void getMeasureTypeIdExperience() {
    String output =
        MappingUtil.getMeasureTypeId(BaseConfigurationTypes.PATIENT_ENGAGEMENT_OR_EXPERIENCE);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.PATIENT_ENGAGEMENT_OR_EXPERIENCE)));
  }

  @Test
  void getMeasureTypeIdProPm() {
    String output =
        MappingUtil.getMeasureTypeId(BaseConfigurationTypes.PATIENT_REPORTED_OUTCOME_PERFORMANCE);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.PATIENT_REPORTED_OUTCOME)));
  }

  @Test
  void getMeasureTypeIdPerformance() {
    String output = MappingUtil.getMeasureTypeId(BaseConfigurationTypes.PERFORMANCE);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.PERFORMANCE)));
  }

  @Test
  void getMeasureTypeIdProcess() {
    String output = MappingUtil.getMeasureTypeId(BaseConfigurationTypes.PROCESS);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.PROCESS)));
  }

  @Test
  void getMeasureTypeIdStructure() {
    String output = MappingUtil.getMeasureTypeId(BaseConfigurationTypes.STRUCTURE);
    assertThat(output, is(equalTo(MadieConstants.MeasureType.STRUCTURE)));
  }

  @Test
  void getMeasureTypeIdStructureForUnsupportedType() {
    Exception ex =
        Assertions.assertThrows(
            PackagingException.class,
            () -> MappingUtil.getMeasureTypeId(BaseConfigurationTypes.PATIENT_REPORTED_OUTCOME));
    assertThat(
        ex.getMessage(), is(equalTo("Unexpected base configuration: Patient Reported Outcome")));
  }

  @Test
  void isPopulationObservationNumerObs() {
    boolean output = MappingUtil.isPopulationObservation(PopulationType.NUMERATOR_OBSERVATION);
    assertThat(output, is(true));
  }

  @Test
  void isPopulationObservationDenomObs() {
    boolean output = MappingUtil.isPopulationObservation(PopulationType.DENOMINATOR_OBSERVATION);
    assertThat(output, is(true));
  }

  @Test
  void isPopulationObservationMeasureObs() {
    boolean output = MappingUtil.isPopulationObservation(PopulationType.MEASURE_OBSERVATION);
    assertThat(output, is(true));
  }

  @Test
  void isPopulationObservationMeasurePopObs() {
    boolean output =
        MappingUtil.isPopulationObservation(PopulationType.MEASURE_POPULATION_OBSERVATION);
    assertThat(output, is(true));
  }

  @Test
  void isPopulationObservation() {
    boolean output = MappingUtil.isPopulationObservation(PopulationType.NUMERATOR);
    assertThat(output, is(false));
  }

  @Test
  void getPopulationDescriptionNullGroups() {
    Measure measure = Measure.builder().build();
    measure.setGroups(null);
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.INITIAL_POPULATION);
    assertThat(output, is(nullValue()));
  }

  @Test
  void getPopulationDescriptionGroupEmptyPops() {
    Measure measure =
        Measure.builder().groups(List.of(Group.builder().build(), Group.builder().build())).build();
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.INITIAL_POPULATION);
    assertThat(output, is(nullValue()));
  }

  @Test
  void getPopulationDescriptionGroupNullPops() {
    Group group1 = Group.builder().build();
    group1.setPopulations(null);
    Group group2 = Group.builder().build();
    group2.setPopulations(null);
    Measure measure = Measure.builder().groups(List.of(group1, group2)).build();
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.INITIAL_POPULATION);
    assertThat(output, is(nullValue()));
  }

  @Test
  void getPopulationDescriptionGroupNoDescriptions() {
    Measure measure =
        Measure.builder()
            .groups(
                List.of(
                    Group.builder()
                        .populations(
                            List.of(
                                Population.builder()
                                    .name(PopulationType.INITIAL_POPULATION)
                                    .build()))
                        .build(),
                    Group.builder().build()))
            .build();
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.INITIAL_POPULATION);
    assertThat(output, is(nullValue()));
  }

  @Test
  void getPopulationDescriptionGroupOneDescription() {
    Measure measure =
        Measure.builder()
            .groups(
                List.of(
                    Group.builder()
                        .populations(
                            List.of(
                                Population.builder()
                                    .name(PopulationType.INITIAL_POPULATION)
                                    .description("IP Description")
                                    .definition("ipDef")
                                    .build()))
                        .build(),
                    Group.builder().build()))
            .build();
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.INITIAL_POPULATION);
    assertThat(output, equalTo("IP Description"));
  }

  @Test
  void getPopulationDescriptionGroupTwoDescription() {
    Measure measure =
        Measure.builder()
            .groups(
                List.of(
                    Group.builder()
                        .populations(
                            List.of(
                                Population.builder()
                                    .name(PopulationType.INITIAL_POPULATION)
                                    .description("IP Description")
                                    .definition("ipDef")
                                    .build()))
                        .measureObservations(
                            List.of(
                                MeasureObservation.builder()
                                    .description("Denominator Obs Description")
                                    .build()))
                        .build(),
                    Group.builder()
                        .populations(
                            List.of(
                                Population.builder()
                                    .name(PopulationType.INITIAL_POPULATION)
                                    .description("IPG2 Description")
                                    .definition("ipDef")
                                    .build()))
                        .build()))
            .build();
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.INITIAL_POPULATION);
    assertThat(output, equalTo("IP Description IPG2 Description"));
  }

  @Test
  void getPopulationDescriptionGroupMissingPopulation() {
    Measure measure =
        Measure.builder()
            .groups(
                List.of(
                    Group.builder()
                        .populations(
                            List.of(
                                Population.builder()
                                    .name(PopulationType.INITIAL_POPULATION)
                                    .description("IP Description")
                                    .definition("ipDef")
                                    .build()))
                        .build(),
                    Group.builder()
                        .populations(
                            List.of(
                                Population.builder()
                                    .name(PopulationType.INITIAL_POPULATION)
                                    .description("IPG2 Description")
                                    .definition("ipDef")
                                    .build()))
                        .build()))
            .build();
    String output = MappingUtil.getPopulationDescription(measure, PopulationType.NUMERATOR);
    assertThat(output, is(nullValue()));
  }

  @Test
  void getPopulationDescriptionGroupForObservationsNullObs() {
    Group group1 = Group.builder().build();
    group1.setMeasureObservations(null);
    Group group2 = Group.builder().build();
    group2.setMeasureObservations(null);
    Measure measure = Measure.builder().groups(List.of(group1, group2)).build();
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.DENOMINATOR_OBSERVATION);
    assertThat(output, is(nullValue()));
  }

  @Test
  void getPopulationDescriptionGroupForObservationsNoDescriptions() {
    Measure measure =
        Measure.builder()
            .groups(
                List.of(
                    Group.builder()
                        .measureObservations(List.of(MeasureObservation.builder().build()))
                        .build(),
                    Group.builder().build()))
            .build();
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.DENOMINATOR_OBSERVATION);
    assertThat(output, is(nullValue()));
  }

  @Test
  void getPopulationDescriptionForObservationsGroupOneDescription() {
    Measure measure =
        Measure.builder()
            .groups(
                List.of(
                    Group.builder()
                        .measureObservations(
                            List.of(
                                MeasureObservation.builder()
                                    .definition("obs1")
                                    .description("Denominator Obs Description")
                                    .build()))
                        .build(),
                    Group.builder().build()))
            .build();
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.DENOMINATOR_OBSERVATION);
    assertThat(output, equalTo("Denominator Obs Description"));
  }

  @Test
  void getPopulationDescriptionGroupMultipleDescriptions() {
    Measure measure =
        Measure.builder()
            .groups(
                List.of(
                    Group.builder()
                        .measureObservations(
                            List.of(
                                MeasureObservation.builder()
                                    .definition("obs1")
                                    .description("Denominator Obs Description")
                                    .build(),
                                MeasureObservation.builder()
                                    .definition("obs2")
                                    .description("Numerator Obs Description")
                                    .build()))
                        .build(),
                    Group.builder()
                        .measureObservations(
                            List.of(
                                MeasureObservation.builder()
                                    .definition("obs1")
                                    .description("G2 Denominator Obs Description")
                                    .build(),
                                MeasureObservation.builder()
                                    .definition("obs2")
                                    .description("G2 Numerator Obs Description")
                                    .build()))
                        .build()))
            .build();
    String output =
        MappingUtil.getPopulationDescription(measure, PopulationType.MEASURE_OBSERVATION);
    assertThat(
        output,
        equalTo(
            "Denominator Obs Description Numerator Obs Description G2 Denominator Obs Description G2 Numerator Obs Description"));
  }

  @Test
  void getStratificationDescriptionNullGroups() {
    Measure measure = Measure.builder().groups(null).build();
    String output = MappingUtil.getStratificationDescription(measure.getGroups());
    assertThat(output, is(nullValue()));
  }

  @Test
  void getStratificationDescriptionGroupEmpty() {
    Measure measure =
        Measure.builder().groups(List.of(Group.builder().build(), Group.builder().build())).build();
    String output = MappingUtil.getStratificationDescription(measure.getGroups());
    assertThat(output, is(nullValue()));
  }

  @Test
  void getStratificationDescriptionIfStratificationNull() {
    Group group = Group.builder().stratifications(null).build();
    Measure measure = Measure.builder().groups(List.of(group)).build();
    String output = MappingUtil.getStratificationDescription(measure.getGroups());
    assertThat(output, is(nullValue()));
  }

  @Test
  void getStratificationDescriptionIfStratificationDefinitionIsNull() {
    Group group =
        Group.builder().stratifications(List.of(Stratification.builder().build())).build();
    Measure measure = Measure.builder().groups(List.of(group)).build();
    String output = MappingUtil.getStratificationDescription(measure.getGroups());
    assertThat(output, is(nullValue()));
  }

  @Test
  void getGroupStratificationDescriptionIfDescriptionIsNull() {
    Group group =
        Group.builder()
            .stratifications(List.of(Stratification.builder().cqlDefinition("IPP").build()))
            .build();
    Measure measure = Measure.builder().groups(List.of(group)).build();
    String output = MappingUtil.getStratificationDescription(measure.getGroups());
    assertThat(output, is(nullValue()));
  }

  @Test
  void getStratificationDescriptionForValidStratifications() {
    Group group =
        Group.builder()
            .stratifications(
                List.of(
                    Stratification.builder().cqlDefinition("IPP").description("Stratum 1").build(),
                    Stratification.builder()
                        .cqlDefinition("Denominator")
                        .description("Stratum 2")
                        .build()))
            .build();
    Measure measure = Measure.builder().groups(List.of(group)).build();
    String output = MappingUtil.getStratificationDescription(measure.getGroups());
    assertThat(output, is(equalTo("Stratum 1 Stratum 2")));
  }
}
