package org.code.javabuilder;

import static org.code.protocol.LoggerNames.MAIN_LOGGER;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import java.util.logging.Logger;

public class UserCodeCompilerPlugin implements Plugin {
  @Override
  public String getName() {
    return "UserCodeCompilerPlugin";
  }

  @Override
  public void init(JavacTask task, String... args) {
    Logger.getLogger(MAIN_LOGGER).info("in the plugin!");
  }
}
