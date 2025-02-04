package gov.cms.madie.services;

import gov.cms.madie.dto.CqlLookups;
import gov.cms.madie.dto.qrda.QrdaExportResponseDto;
import gov.cms.madie.dto.qrda.QrdaReportDTO;
import gov.cms.madie.dto.qrda.QrdaRequestDTO;
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
  private final HumanReadableService humanReadableService;
  private final QrdaService qrdaService;

  public byte[] createMeasurePackage(Measure measure, String accessToken) {
    log.info("Creating the measure package for measure [{}]", measure.getId());
    QdmMeasure qdmMeasure = (QdmMeasure) measure;
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
      entries.put(
          resourcesDir + entryName.trim() + ".json", translatedLibrary.getElmJson().getBytes());
      entries.put(
          resourcesDir + entryName.trim() + ".xml", translatedLibrary.getElmXml().getBytes());
      entries.put(cqlDir + entryName.trim() + ".cql", translatedLibrary.getCql().getBytes());
    }
    CqlLookups cqlLookups = translationServiceClient.getCqlLookups(qdmMeasure, accessToken);
    final String humanReadable = humanReadableService.generate(measure, cqlLookups);

    if (humanReadable != null) {
      entries.put(
          measure.getEcqmTitle().trim() + "-v" + measure.getVersion() + "-QDM" + ".html",
          humanReadable.getBytes());
    }
    String hqmf = hqmfService.generateHqmf(qdmMeasure, cqlLookups);
    entries.put(
        measure.getEcqmTitle().trim() + "-v" + measure.getVersion() + "-QDM" + ".xml",
        hqmf.getBytes());
    return new ZipUtility().zipEntries(entries, new ByteArrayOutputStream());
  }

  public byte[] createQRDA(QrdaRequestDTO qrdaRequestDTO, String accessToken) {
    QdmMeasure measure = (QdmMeasure) qrdaRequestDTO.getMeasure();
    log.info("Creating QRDA for measure [{}]", measure.getId());

    QrdaExportResponseDto qrdaExport = qrdaService.generateQrda(qrdaRequestDTO, accessToken);

    if (CollectionUtils.isEmpty(qrdaExport.getIndividualReports())) {
      return new byte[0];
    }

    log.info(
        "Adding measure package artifacts to the measure package for measure {}", measure.getId());
    String htmlDir = "html/";
    String qrdaDir = "qrda/";
    Map<String, byte[]> entries = new HashMap<>();
    entries.put(
        measure.getEcqmTitle() + "_patients_results.html",
        qrdaExport.getSummaryReport().getBytes());
    for (QrdaReportDTO qrda : qrdaExport.getIndividualReports()) {
      if (qrda.getQrda() == null) {
        log.warn(
            "QRDA export for Measure [{}], testcase filename [{}] is missing QRDA",
            measure.getId(),
            qrda.getFilename());
      } else {
        entries.put(qrdaDir + qrda.getFilename() + ".xml", qrda.getQrda().getBytes());
      }
      if (qrda.getReport() == null) {
        log.warn(
            "QRDA export for Measure [{}], testcase filename [{}] is missing HTML",
            measure.getId(),
            qrda.getFilename());
      } else {
        entries.put(htmlDir + qrda.getFilename() + ".html", qrda.getReport().getBytes());
      }
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    return new ZipUtility().zipEntries(entries, outputStream);
  }
}
