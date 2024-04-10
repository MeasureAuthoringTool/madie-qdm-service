package gov.cms.madie.util;

import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.QdmMeasure;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

public class MeasureUtils {
  public static Set<String> getMeasureDefinitions(Measure measure) {
    if (measure == null || CollectionUtils.isEmpty(measure.getGroups())) {
      return Set.of();
    }
    Set<String> measureDefinitions = new HashSet<>();
    measure
        .getGroups()
        .forEach(
            group -> {
              group
                  .getPopulations()
                  .forEach(
                      population -> {
                        if (StringUtils.isNotBlank(population.getDefinition())) {
                          measureDefinitions.add(population.getDefinition());
                        }
                      });
              if (!CollectionUtils.isEmpty(group.getMeasureObservations())) {
                group
                    .getMeasureObservations()
                    .forEach(
                        measureObservation -> {
                          if (StringUtils.isNotBlank(measureObservation.getDefinition())) {
                            measureDefinitions.add(measureObservation.getDefinition());
                          }
                        });
              }
              if (!CollectionUtils.isEmpty(group.getStratifications())) {
                group
                    .getStratifications()
                    .forEach(
                        stratification -> {
                          if (StringUtils.isNotBlank(stratification.getCqlDefinition())) {
                            measureDefinitions.add(stratification.getCqlDefinition());
                          }
                        });
              }
            });
    measure
        .getSupplementalData()
        .forEach(defDescPair -> measureDefinitions.add(defDescPair.getDefinition()));
    measure
        .getRiskAdjustments()
        .forEach(defDescPair -> measureDefinitions.add(defDescPair.getDefinition()));
    return measureDefinitions;
  }

  public static String getImprovementNotation(QdmMeasure measure) {
    if (measure == null || StringUtils.isBlank(measure.getImprovementNotation())) {
      return null;
    }
    if ("Other".equals(measure.getImprovementNotation())) {
      return measure.getImprovementNotationDescription();
    }
    if (StringUtils.isBlank(measure.getImprovementNotationDescription())) {
      return measure.getImprovementNotation();
    }
    return measure.getImprovementNotation() + " - " + measure.getImprovementNotationDescription();
  }
}
