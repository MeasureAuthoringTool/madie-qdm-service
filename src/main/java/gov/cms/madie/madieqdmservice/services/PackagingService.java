package gov.cms.madie.madieqdmservice.services;

import gov.cms.madie.madieqdmservice.dto.TranslatedLibrary;
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
  private final String CQL_DIR = "cql/";
  private final String RESOURCES_DIR = "resources/";

  public byte[] createMeasurePackage(Measure measure, String accessToken) {
    log.info("Creating the measure package for measure [{}]", measure.getId());
    List<TranslatedLibrary> translatedLibraries =
        translationServiceClient.getTranslatedLibraries(measure.getCql(), accessToken);
    if (CollectionUtils.isEmpty(translatedLibraries)) {
      return new byte[0];
    }
    log.info("Adding measure package artifacts to the measure package");
    Map<String, byte[]> entries = new HashMap<>();
    for (TranslatedLibrary translatedLibrary : translatedLibraries) {
      String entryName = translatedLibrary.getName() + "-v" + translatedLibrary.getVersion();
      entries.put(RESOURCES_DIR + entryName + ".json", translatedLibrary.getElmJson().getBytes());
      entries.put(RESOURCES_DIR + entryName + ".xml", translatedLibrary.getElmXml().getBytes());
      entries.put(CQL_DIR + entryName + ".cql", translatedLibrary.getCql().getBytes());
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    return new ZipUtility().zipEntries(entries, outputStream);
  }
}
