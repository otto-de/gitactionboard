package de.otto.platform.gitactionboard.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(
    packages = "de.otto.platform.gitactionboard",
    importOptions = DoNotIncludeTests.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class ArchitectureFitnessTest {

  @ArchTest
  static void domainDoesNotImportFromAdapterPackage(JavaClasses classes) {
    noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..adapters..")
        .check(classes);
  }

  @ArchTest
  static void applicationDoesNotHaveCyclicDependencies(JavaClasses classes) {
    slices().matching("..gitactionboard.(*)..").should().beFreeOfCycles().check(classes);
  }
}
