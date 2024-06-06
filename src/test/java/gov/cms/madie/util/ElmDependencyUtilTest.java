package gov.cms.madie.util;

import gov.cms.madie.Exceptions.QrdaServiceException;
import gov.cms.madie.packaging.utils.ResourceFileUtil;
import gov.cms.madie.models.cqm.StatementDependency;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ElmDependencyUtilTest implements ResourceFileUtil {

  @Test
  void findDependencies() throws Exception {
    String elms = getStringFromTestResource("/elm/libraryElm.json");
    String elms2 = getStringFromTestResource("/elm/libraryElm2.json");
    Map<String, List<StatementDependency>> libraries =
        ElmDependencyUtil.findDependencies(
            List.of(elms, elms2), "UrinarySymptomScoreChangeAfterBenignProstaticHyperplasia");

    assertEquals(2, libraries.size());
    assertEquals(
        25, libraries.get("UrinarySymptomScoreChangeAfterBenignProstaticHyperplasia").size());
    assertEquals(4, libraries.get("MATGlobalCommonFunctionsQDM").size());
    assertEquals(
        2,
        libraries.get("MATGlobalCommonFunctionsQDM").stream()
            .filter(
                statementDependency -> statementDependency.getStatement_name().equals("EarliestOf"))
            .findFirst()
            .get()
            .getStatement_references()
            .size());
  }

  @Test
  void findDependenciesNullElm() throws Exception {
    Exception ex =
        assertThrows(
            QrdaServiceException.class,
            () ->
                ElmDependencyUtil.findDependencies(
                    null, "UrinarySymptomScoreChangeAfterBenignProstaticHyperplasia"));

    assertThat(ex.getMessage(), containsString("elm json missing"));
  }

  @Test
  void findDependenciesEmptyElm() throws Exception {
    Exception ex =
        assertThrows(
            QrdaServiceException.class,
            () ->
                ElmDependencyUtil.findDependencies(
                    Collections.emptyList(),
                    "UrinarySymptomScoreChangeAfterBenignProstaticHyperplasia"));

    assertThat(ex.getMessage(), containsString("elm json missing"));
  }

  @Test
  void findDependenciesMalformedElm() throws Exception {
    String elm = "{\n" + "    \"library\": {}}";
    Exception ex =
        assertThrows(
            QrdaServiceException.class,
            () ->
                ElmDependencyUtil.findDependencies(
                    List.of(elm), "UrinarySymptomScoreChangeAfterBenignProstaticHyperplasia"));

    assertThat(ex.getMessage(), containsString("library or identifier missing"));
  }
}
