package gov.cms.madie.util;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import gov.cms.madie.models.measure.BaseConfigurationTypes;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.MeasureScoring;
import gov.cms.madie.models.measure.PopulationType;
import org.apache.commons.lang3.StringUtils;

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
      case PATIENT_REPORTED_OUTCOME -> MadieConstants.MeasureType.PATIENT_REPORTED_OUTCOME;
      case PERFORMANCE -> MadieConstants.MeasureType.PERFORMANCE;
      case PROCESS -> MadieConstants.MeasureType.PROCESS;
      case STRUCTURE -> MadieConstants.MeasureType.STRUCTURE;
    };
  }

  public static boolean isPopulationObservation(PopulationType populationType) {
    return populationType == PopulationType.NUMERATOR_OBSERVATION ||
            populationType == PopulationType.DENOMINATOR_OBSERVATION ||
            populationType == PopulationType.MEASURE_OBSERVATION ||
            populationType == PopulationType.MEASURE_POPULATION_OBSERVATION;
  }

  public static String getPopulationDescription(Measure measure, PopulationType populationType) {
    if (CollectionUtils.isEmpty(measure.getGroups())) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    measure.getGroups().stream()
        .forEach(
            group -> {
              if (CollectionUtils.isNotEmpty(group.getPopulations()) && !isPopulationObservation(populationType)) {
                group.getPopulations().stream()
                    .forEach(
                        population -> {
                          if (StringUtils.isNotBlank(population.getDefinition())
                              && populationType.name().equalsIgnoreCase(population.getName().name())
                              && StringUtils.isNotBlank(population.getDescription())) {
                            sb.append(population.getDescription() + "\n");
                          }
                        });
              } else if (CollectionUtils.isNotEmpty(group.getMeasureObservations()) && isPopulationObservation(populationType)) {
                group.getMeasureObservations().stream()
                        .forEach(
                                observation -> {
                                  if (StringUtils.isNotBlank(observation.getDefinition())
                                          && StringUtils.isNotBlank(observation.getDescription())) {
                                    sb.append(observation.getDescription() + "\n");
                                  }
                                });
              }
            });
    return sb.toString();
  }
}
