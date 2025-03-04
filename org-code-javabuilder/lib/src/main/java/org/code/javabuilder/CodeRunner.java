package org.code.javabuilder;

import java.net.URLClassLoader;
import org.code.protocol.JavabuilderException;

/** Runs code using a {@link URLClassLoader} to load compiled classes */
public interface CodeRunner {
  /**
   * Run code with the compiled classes provided by the {@link URLClassLoader}
   *
   * @param urlClassLoader class loader to load compiled code
   * @throws JavabuilderException if there is an error running code
   */
  void run(URLClassLoader urlClassLoader) throws JavabuilderException;
}
