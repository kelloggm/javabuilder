package org.code.javabuilder;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.code.protocol.InternalErrorKey;
import org.code.protocol.OutputAdapter;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

/** Runs all tests for a given set of Java files */
public class TestRunner implements CodeRunner {
  private final List<JavaProjectFile> javaFiles;
  private final JavabuilderTestExecutionListener listener;

  public TestRunner(List<JavaProjectFile> javaFiles, OutputAdapter outputAdapter) {
    this(javaFiles, new JavabuilderTestExecutionListener(outputAdapter));
  }

  TestRunner(List<JavaProjectFile> javaFiles, JavabuilderTestExecutionListener listener) {
    this.javaFiles = javaFiles;
    this.listener = listener;
  }

  /**
   * Finds and runs all tests in the given set of Java files using the given URLClassLoader
   *
   * @param urlClassLoader class loader to load compiled classes
   * @throws InternalServerError if there is an error running tests
   */
  public void run(URLClassLoader urlClassLoader) throws InternalServerError {
    try {
      // Search all project files for tests
      final List<ClassSelector> classSelectors = new ArrayList<>();
      for (JavaProjectFile file : this.javaFiles) {
        classSelectors.add(
            DiscoverySelectors.selectClass(urlClassLoader.loadClass(file.getClassName())));
      }
      final LauncherDiscoveryRequest request =
          LauncherDiscoveryRequestBuilder.request().selectors(classSelectors).build();

      try (LauncherSession session = LauncherFactory.openSession()) {
        final Launcher launcher = session.getLauncher();
        // Register listener
        launcher.registerTestExecutionListeners(this.listener);
        // Discover tests and build a test plan
        final TestPlan testPlan = launcher.discover(request);
        // Execute test plan
        launcher.execute(testPlan);
      }
    } catch (PreconditionViolationException | ClassNotFoundException e) {
      throw new InternalServerError(InternalErrorKey.INTERNAL_EXCEPTION, e);
    }
  }
}
