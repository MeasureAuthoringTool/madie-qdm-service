package gov.cms.madie.services;

import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.Measure;
import gov.cms.madie.models.measure.QdmMeasure;
import gov.cms.madie.packaging.utils.ZipUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackagingService {
  private final TranslationServiceClient translationServiceClient;
  private final HqmfService hqmfService;

  public byte[] createMeasurePackage(Measure measure, String accessToken) {
    log.info("Creating the measure package for measure [{}]", measure.getId());
    List<TranslatedLibrary> translatedLibraries =
        translationServiceClient.getTranslatedLibraries(measure.getCql(), accessToken);
    if (CollectionUtils.isEmpty(translatedLibraries)) {
      return new byte[0];
    }
    log.info("Adding measure package artifacts to the measure package");
    String resourcesDir = "resources/";
    String cqlDir = "cql/";
    Map<String, byte[]> entries = new HashMap<>();
    for (TranslatedLibrary translatedLibrary : translatedLibraries) {
      String entryName = translatedLibrary.getName() + "-" + translatedLibrary.getVersion();
      entries.put(resourcesDir + entryName + ".json", translatedLibrary.getElmJson().getBytes());
      entries.put(resourcesDir + entryName + ".xml", translatedLibrary.getElmXml().getBytes());
      entries.put(cqlDir + entryName + ".cql", translatedLibrary.getCql().getBytes());
    }
    String humanReadable = translationServiceClient.getHumanReadable(measure, accessToken);
    if (humanReadable != null) {
      entries.put(
          measure.getEcqmTitle() + "-v" + measure.getVersion() + "-QDM" + ".html",
          humanReadable.getBytes());
    }
    // TODO: remove this after HQMF is complete, but for now don't prevent export if HQMF fails
    try {
      String hqmf = hqmfService.generateHqmf((QdmMeasure) measure, accessToken);
      entries.put(
          measure.getEcqmTitle() + "-v" + measure.getVersion() + "-QDM" + ".xml", hqmf.getBytes());
    } catch (Exception ex) {
      // TODO: this is temporary - remove it after!!
      log.error("An error occurred during HQMF generation for measure: {}", measure.getId(), ex);
      entries.put(
          measure.getEcqmTitle() + "-v" + measure.getVersion() + "-QDM-ERROR" + ".xml",
          "<error>An error occurred that caused the HQMF generation to fail.</error>".getBytes());
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    return new ZipUtility().zipEntries(entries, outputStream);
  }
}
