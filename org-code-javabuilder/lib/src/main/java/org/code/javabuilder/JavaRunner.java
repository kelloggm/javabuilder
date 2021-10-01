package org.code.javabuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.code.protocol.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/** The class that executes the student's code */
public class JavaRunner {
  private final URL executableLocation;
  private final List<JavaProjectFile> javaFiles;
  private final OutputAdapter outputAdapter;

  public JavaRunner(
      URL executableLocation, List<JavaProjectFile> javaFiles, OutputAdapter outputAdapter) {
    this.executableLocation = executableLocation;
    this.javaFiles = javaFiles;
    this.outputAdapter = outputAdapter;
  }

  /**
   * Run the compiled user code.
   *
   * @throws InternalServerError When the user's code hits a runtime error or fails due to an
   *     internal error.
   * @throws InternalFacingException When we hit an internal error after the user's code has
   *     finished executing.
   */
  public void runCode() throws InternalFacingException, JavabuilderException {
    // Include the user-facing api jars in the code we are loading so student code can access them.
    URL[] classLoaderUrls = Util.getAllJarURLs(this.executableLocation);

    // Create a new URLClassLoader. Use the current class loader as the parent so IO settings are
    // preserved.
    URLClassLoader urlClassLoader =
        new URLClassLoader(classLoaderUrls, JavaRunner.class.getClassLoader());

    try {
      // load and run the main method of the class
      Method mainMethod = this.findMainMethod(urlClassLoader);
      this.outputAdapter.sendMessage(new StatusMessage(StatusMessageKey.RUNNING));
      mainMethod.invoke(null, new Object[] {null});
    } catch (IllegalAccessException e) {
      // TODO: this error message may not be not very friendly
      this.outputAdapter.sendMessage(new StatusMessage(StatusMessageKey.EXITED));
      throw new UserInitiatedException(UserInitiatedExceptionKey.ILLEGAL_METHOD_ACCESS, e);
    } catch (InvocationTargetException e) {
      this.outputAdapter.sendMessage(new StatusMessage(StatusMessageKey.EXITED));
      // If the invocation exception is wrapping another JavabuilderException or
      // JavabuilderRuntimeException, we don't need to wrap it in a UserInitiatedException
      if (e.getCause() instanceof JavabuilderException) {
        throw (JavabuilderException) e.getCause();
      }
      if (e.getCause() instanceof JavabuilderRuntimeException) {
        throw (JavabuilderRuntimeException) e.getCause();
      }
      // FileNotFoundExceptions may be thrown from student code, so we treat them as a
      // specific case of a UserInitiatedException
      if (e.getCause() instanceof FileNotFoundException) {
        throw new UserInitiatedException(UserInitiatedExceptionKey.FILE_NOT_FOUND, e.getCause());
      }
      throw new UserInitiatedException(UserInitiatedExceptionKey.RUNTIME_ERROR, e);
    }
    try {
      this.outputAdapter.sendMessage(new StatusMessage(StatusMessageKey.EXITED));
      urlClassLoader.close();
    } catch (IOException e) {
      // The user code has finished running. We don't want to confuse them at this point with an
      // error message.
      throw new InternalFacingException("Error closing urlClassLoader: " + e, e);
    }
  }

  /**
   * Finds the main method in the set of files in fileManager if it exists.
   *
   * @param urlClassLoader class loader pointing to location of compiled classes
   * @return the main method if it is found
   * @throws InternalServerError if there is an issue loading a class
   * @throws UserInitiatedException if there is more than one main method or no main method
   */
  public Method findMainMethod(URLClassLoader urlClassLoader)
      throws InternalServerError, UserInitiatedException {

    Method mainMethod = null;
    for (JavaProjectFile file : this.javaFiles) {
      try {
        Method[] declaredMethods =
            urlClassLoader.loadClass(file.getClassName()).getDeclaredMethods();
        for (Method method : declaredMethods) {
          Class[] parameterTypes = method.getParameterTypes();
          if (method.getName().equals("main")
              && parameterTypes.length == 1
              && parameterTypes[0].equals(String[].class)) {
            if (mainMethod != null) {
              throw new UserInitiatedException(UserInitiatedExceptionKey.TWO_MAIN_METHODS);
            }
            mainMethod = method;
          }
        }
      } catch (ClassNotFoundException e) {
        // May be thrown if file is empty or contains only comments
        throw new UserInitiatedException(UserInitiatedExceptionKey.CLASS_NOT_FOUND, e);
      }
    }

    if (mainMethod == null) {
      throw new UserInitiatedException(UserInitiatedExceptionKey.NO_MAIN_METHOD);
    }
    return mainMethod;
  }

  public void runTests() {
    // Include the user-facing api jars in the code we are loading so student code can access them.
    URL[] classLoaderUrls = Util.getAllJarURLs(this.executableLocation);

    // Create a new URLClassLoader. Use the current class loader as the parent so IO settings are
    // preserved.
    URLClassLoader urlClassLoader =
        new URLClassLoader(classLoaderUrls, JavaRunner.class.getClassLoader());

    this.outputAdapter.sendMessage(new StatusMessage(StatusMessageKey.RUNNING));
    LauncherDiscoveryRequest request = null;
    try {
      List<ClassSelector> classSelectors = new ArrayList<>();
      for (JavaProjectFile file : this.javaFiles) {
        classSelectors.add(
            DiscoverySelectors.selectClass(urlClassLoader.loadClass(file.getClassName())));
      }
      request = LauncherDiscoveryRequestBuilder.request().selectors(classSelectors).build();

      SummaryGeneratingListener listener = new SummaryGeneratingListener();
      try (LauncherSession session = LauncherFactory.openSession()) {
        Launcher launcher = session.getLauncher();
        // Register a listener of your choice
        launcher.registerTestExecutionListeners(listener);
        // Discover tests and build a test plan
        TestPlan testPlan = launcher.discover(request);
        // Execute test plan
        launcher.execute(testPlan);
      }

      TestExecutionSummary summary = listener.getSummary();
      PrintWriter printWriter = new PrintWriter(System.out);
      summary.printTo(printWriter);
      summary.printFailuresTo(printWriter);
      printWriter.close();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}
