package org.code.javabuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.code.protocol.*;
import org.junit.jupiter.api.Test;

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
      this.findTestClasses(urlClassLoader);
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

  public List<Class> findTestClasses(URLClassLoader urlClassLoader) {
    List<Class> testClasses = new ArrayList<>();

    for (JavaProjectFile file : this.javaFiles) {
      try {
        Class c = urlClassLoader.loadClass(file.getClassName());
        Method[] declaredMethods = c.getDeclaredMethods();
        boolean foundTests = false;
        for (Method method : declaredMethods) {
          if (method.isAnnotationPresent(Test.class)) {
            System.out.println("found a test!");
            foundTests = true;
          }
        }
        if (foundTests) {
          testClasses.add(c);
        }
      } catch (ClassNotFoundException e) {
        // May be thrown if file is empty or contains only comments
        // throw new UserInitiatedException(UserInitiatedExceptionKey.CLASS_NOT_FOUND, e);
      }
    }
    return testClasses;
  }
}
