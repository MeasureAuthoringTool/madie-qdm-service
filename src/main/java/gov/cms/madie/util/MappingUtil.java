package gov.cms.madie.util;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import gov.cms.madie.Exceptions.PackagingException;
import gov.cms.madie.models.measure.BaseConfigurationTypes;
import gov.cms.madie.models.measure.Group;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.MeasureScoring;
import gov.cms.madie.models.measure.PopulationType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public final class MappingUtil {
  public static String getScoringAbbr(String scoring) {
    MeasureScoring measureScoring = MeasureScoring.valueOfText(scoring);
    return switch (measureScoring) {
      case COHORT -> MadieConstants.Scoring.COHORT_ABBREVIATION;
      case CONTINUOUS_VARIABLE -> MadieConstants.Scoring.CONTINUOUS_VARIABLE_ABBREVIATION;
      case PROPORTION -> MadieConstants.Scoring.PROPORTION_ABBREVIATION;
      case RATIO -> MadieConstants.Scoring.RATIO_ABBREVIATION;
    };
  }

  public static String getMeasureTypeId(BaseConfigurationTypes baseConfigurationType) {
    return switch (baseConfigurationType) {
      case APPROPRIATE_USE_PROCESS -> MadieConstants.MeasureType.APPROPRIATE_USE_PROCESS;
      case COST_OR_RESOURCE_USE -> MadieConstants.MeasureType.COST_OR_RESOURCE_USE;
      case EFFICIENCY -> MadieConstants.MeasureType.EFFICIENCY;
      case INTERMEDIATE_CLINICAL_OUTCOME -> MadieConstants.MeasureType
          .INTERMEDIATE_CLINICAL_OUTCOME;
      case OUTCOME -> MadieConstants.MeasureType.OUTCOME;
      case PATIENT_ENGAGEMENT_OR_EXPERIENCE -> MadieConstants.MeasureType
          .PATIENT_ENGAGEMENT_OR_EXPERIENCE;
      case PATIENT_REPORTED_OUTCOME_PERFORMANCE -> MadieConstants.MeasureType
          .PATIENT_REPORTED_OUTCOME;
      case PERFORMANCE -> MadieConstants.MeasureType.PERFORMANCE;
      case PROCESS -> MadieConstants.MeasureType.PROCESS;
      case STRUCTURE -> MadieConstants.MeasureType.STRUCTURE;
      default -> throw new PackagingException(
          "Unsupported base configuration: " + baseConfigurationType);
    };
  }

  public static boolean isPopulationObservation(PopulationType populationType) {
    return populationType == PopulationType.NUMERATOR_OBSERVATION
        || populationType == PopulationType.DENOMINATOR_OBSERVATION
        || populationType == PopulationType.MEASURE_OBSERVATION
        || populationType == PopulationType.MEASURE_POPULATION_OBSERVATION;
  }

  public static String getPopulationType(PopulationType populationType) {
    return switch (populationType) {
      case INITIAL_POPULATION -> "initialPopulation";
      case DENOMINATOR -> "denominator";
      case DENOMINATOR_EXCLUSION -> "denominatorExclusions";
      case DENOMINATOR_EXCEPTION -> "denominatorExceptions";
      case NUMERATOR -> "numerator";
      case NUMERATOR_EXCLUSION -> "numeratorExclusions";
      case MEASURE_POPULATION -> "measurePopulation";
      case MEASURE_POPULATION_EXCLUSION -> "measurePopulationExclusions";
      default -> "?";
    };
  }

  /**
   * Fetch all descriptions for the given population type (observations are a valid possible input)
   * Combine all descriptions for that population type with a space (not a newline)
   *
   * @param measure
   * @param populationType
   * @return
   */
  public static String getPopulationDescription(Measure measure, PopulationType populationType) {
    if (CollectionUtils.isEmpty(measure.getGroups())) {
      return null;
    }
    final String description =
        measure.getGroups().stream()
            .map(
                group -> {
                  if (CollectionUtils.isNotEmpty(group.getPopulations())
                      && !isPopulationObservation(populationType)) {
                    return group.getPopulations().stream()
                        .filter(
                            population ->
                                StringUtils.isNotBlank(population.getDefinition())
                                    && populationType
                                        .name()
                                        .equalsIgnoreCase(population.getName().name())
                                    && StringUtils.isNotBlank(population.getDescription()))
                        .map(p -> p.getDescription().replaceAll("[\\t\\n\\r]+", " "))
                        .collect(Collectors.joining(" "));
                  } else if (CollectionUtils.isNotEmpty(group.getMeasureObservations())
                      && isPopulationObservation(populationType)) {
                    // there is only one observation description field, so grab all available
                    // measure observation descriptions
                    return group.getMeasureObservations().stream()
                        .filter(
                            observation ->
                                StringUtils.isNotBlank(observation.getDefinition())
                                    && StringUtils.isNotBlank(observation.getDescription()))
                        .map(mo -> mo.getDescription().replaceAll("[\\t\\n\\r]+", " "))
                        .collect(Collectors.joining(" "));
                  }
                  return null;
                })
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
    return StringUtils.isBlank(description) ? null : description;
  }

  /**
   * Fetch descriptions of all the stratification from all the groups. Combine all descriptions with
   * a space (not a newline)
   *
   * @param groups - measure groups
   * @return
   */
  public static String getStratificationDescription(List<Group> groups) {
    if (CollectionUtils.isEmpty(groups)) {
      return null;
    }
    final String description =
        groups.stream()
            .map(
                group -> {
                  if (CollectionUtils.isNotEmpty(group.getStratifications())) {
                    // there is only one stratum description field, so grab all available
                    // measure stratification descriptions
                    return group.getStratifications().stream()
                        .filter(
                            stratum ->
                                StringUtils.isNotBlank(stratum.getCqlDefinition())
                                    && StringUtils.isNotBlank(stratum.getDescription()))
                        .map(stratum -> stratum.getDescription().replaceAll("[\\t\\n\\r]+", " "))
                        .collect(Collectors.joining(" "));
                  }
                  return null;
                })
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" "));
    return StringUtils.isBlank(description) ? null : description;
  }
}
