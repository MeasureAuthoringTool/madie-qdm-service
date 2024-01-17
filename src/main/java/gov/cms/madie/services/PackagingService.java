package gov.cms.madie.services;

import gov.cms.madie.models.dto.TranslatedLibrary;
import gov.cms.madie.models.measure.Measure;
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
    String humanReadable = translationServiceClient.getMeasureBundleExport(measure, accessToken);
    if (humanReadable != null) {
      entries.put(
          measure.getEcqmTitle() + "-v" + measure.getVersion() + "-QDM" + ".html",
          humanReadable.getBytes());
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    return new ZipUtility().zipEntries(entries, outputStream);
  }
}
