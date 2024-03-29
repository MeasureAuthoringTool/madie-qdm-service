package gov.cms.madie.util;

import gov.cms.madie.dto.CQLDefinition;
import gov.cms.madie.models.measure.Group;
import gov.cms.madie.models.measure.MeasureMetaData;
import gov.cms.madie.models.measure.MeasureObservation;
import gov.cms.madie.models.measure.Population;
import gov.cms.madie.models.measure.PopulationType;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.models.measure.Stratification;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HumanReadableUtilTest {

  private QdmMeasure measure = QdmMeasure.builder().build();

  @Test
  void testGetMeasureDevelopersMetaDataNull() {
    var result = HumanReadableUtil.getMeasureDevelopers(measure);
    assertNull(result);
  }

  @Test
  void testGetMeasureDevelopersNullDevelopers() {
    measure.setMeasureMetaData(MeasureMetaData.builder().build());
    var result = HumanReadableUtil.getMeasureDevelopers(measure);
    assertNull(result);
  }

  @Test
  void testGetCbeNumberMetaDataNull() {
    measure.setMeasureMetaData(null);
    var result = HumanReadableUtil.getCbeNumber(measure);
    assertNull(result);
  }

  @Test
  void testGetCbeNumberCbeNumberNull() {
    measure.setMeasureMetaData(MeasureMetaData.builder().build());
    var result = HumanReadableUtil.getCbeNumber(measure);
    assertNull(result);
  }

  @Test
  void testGetMeasureObservationNull() {
    measure.setMeasureMetaData(MeasureMetaData.builder().build());
    var result = HumanReadableUtil.getMeasureObservationDescriptions(measure);
    assertNull(result);
  }

  @Test
  void testGetEndorsedByMetaDataNull() {
    measure.setMeasureMetaData(null);
    var result = HumanReadableUtil.getEndorsedBy(measure);
    assertNull(result);
  }

  @Test
  void testGetEndorsedByEndorsementNull() {
    measure.setMeasureMetaData(MeasureMetaData.builder().build());
    var result = HumanReadableUtil.getEndorsedBy(measure);
    assertNull(result);
  }

  @Test
  void testGetMeasureTypesMetaDataNull() {
    measure.setMeasureMetaData(null);
    var result = HumanReadableUtil.getMeasureTypes(measure);
    assertNull(result);
  }

  @Test
  void testGetStratificationNullGroups() {
    var result = HumanReadableUtil.getStratification(measure);
    assertNull(result);
  }

  @Test
  void testGetStratificationNullStratificationss() {
    measure.setGroups(List.of(Group.builder().build()));
    var result = HumanReadableUtil.getStratification(measure);
    assertNull(result);
  }

  @Test
  void testGetMeasureTypesMeasureTypesaNull() {
    measure.setMeasureMetaData(MeasureMetaData.builder().build());
    var result = HumanReadableUtil.getMeasureTypes(measure);
    assertNull(result);
  }

  @Test
  void testEscapeHtmlNullString() {
    measure.setMeasureMetaData(null);
    var result = HumanReadableUtil.escapeHtmlString(null);
    assertNull(result);
  }

  @Test
  void testEscapeHtmlString() {
    measure.setMeasureMetaData(MeasureMetaData.builder().build());
    var result = HumanReadableUtil.escapeHtmlString("this is <html> string");
    assertThat(result, is(equalTo("this is &lt;html&gt; string")));
  }

  @Test
  void testBuildReferencesMetaDataNull() {
    measure.setMeasureMetaData(null);
    var result = HumanReadableUtil.buildReferences(measure.getMeasureMetaData());
    assertNull(result);
  }

  @Test
  void testBuildReferencesMeasureDefinitionsNull() {
    measure.setMeasureMetaData(MeasureMetaData.builder().build());
    var result = HumanReadableUtil.buildReferences(measure.getMeasureMetaData());
    assertNull(result);
  }

  @Test
  void testGetPopulationDescriptionGroupsNull() {
    var result =
        HumanReadableUtil.getPopulationDescription(
            measure, PopulationType.INITIAL_POPULATION.name());
    assertTrue(StringUtils.isBlank(result));
  }

  @Test
  void testGetPopulationDescriptionPopulationsNull() {
    measure.setGroups(List.of(Group.builder().build()));
    var result =
        HumanReadableUtil.getPopulationDescription(
            measure, PopulationType.INITIAL_POPULATION.name());
    assertTrue(StringUtils.isBlank(result));
  }

  @Test
  void testGetPopulationDescriptionNoDefinition() {
    measure.setGroups(
        List.of(Group.builder().populations(List.of(Population.builder().build())).build()));
    var result =
        HumanReadableUtil.getPopulationDescription(
            measure, PopulationType.INITIAL_POPULATION.name());
    assertTrue(StringUtils.isBlank(result));
  }

  @Test
  void testStratificationDescriptionOutput() {
    Stratification s1 = Stratification.builder().description("G1S1").build();
    Group g1 = Group.builder().stratifications(List.of(s1)).build();
    Stratification s2 = Stratification.builder().description("G2S2").build();
    Group g2 = Group.builder().stratifications(List.of(s2)).build();
    QdmMeasure testMeasure = QdmMeasure.builder().groups(List.of(g1, g2)).build();

    String humanreadabledescription = HumanReadableUtil.getStratification(testMeasure);
    assertEquals("G1S1\nG2S2", humanreadabledescription);
  }

  @Test
  void testMeasureObservationDescriptionOutput() {
    MeasureObservation s1 = MeasureObservation.builder().description("G1M1").build();
    Group g1 = Group.builder().measureObservations(List.of(s1)).build();
    MeasureObservation s2 = MeasureObservation.builder().description("G2M2").build();
    Group g2 = Group.builder().measureObservations(List.of(s2)).build();
    QdmMeasure testMeasure = QdmMeasure.builder().groups(List.of(g1, g2)).build();

    String humanreadabledescription =
        HumanReadableUtil.getMeasureObservationDescriptions(testMeasure);
    assertEquals("G1M1\nG2M2", humanreadabledescription);
  }

  @Test
  void testGetObservationAssociationForNullObservation() {
    List<Population> populationList = List.of(Population.builder().build());
    assertThat(HumanReadableUtil.getObservationAssociation(null, populationList), is(nullValue()));
  }

  @Test
  void testGetObservationAssociationForNullPopulations() {
    List<Population> populationList = List.of();
    assertThat(HumanReadableUtil.getObservationAssociation("1", populationList), is(nullValue()));
  }

  @Test
  void testGetObservationAssociation() {
    List<Population> populationList =
        List.of(Population.builder().name(PopulationType.DENOMINATOR).id("1").build());
    Population population = HumanReadableUtil.getObservationAssociation("1", populationList);
    assertThat(population.getName().getDisplay(), is(PopulationType.DENOMINATOR.getDisplay()));
  }

  @Test
  void testGetDefinitionNameForNullInput() {
    assertThat(HumanReadableUtil.getDefinitionName(null), is(nullValue()));
  }

  @Test
  void testGetDefinitionName() {
    CQLDefinition cqlDefinition =
        CQLDefinition.builder().definitionName("test").definitionLogic("Sum(1 + 5)").build();
    assertThat(
        HumanReadableUtil.getDefinitionName(cqlDefinition),
        is(equalTo(cqlDefinition.getDefinitionName())));
  }

  @Test
  void testGetDefinitionNameForIncludedLibraryDefinition() {
    CQLDefinition cqlDefinition =
        CQLDefinition.builder()
            .definitionName("add")
            .definitionLogic("Sum(1 + 5)")
            .libraryDisplayName("Math")
            .build();
    assertThat(HumanReadableUtil.getDefinitionName(cqlDefinition), is(equalTo("Math.add")));
  }
}
