package org.code.compilerplugin;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;

public class UserCodeCompilerPlugin implements Plugin {
  @Override
  public String getName() {
    return "UserCodeCompilerPlugin";
  }

  @Override
  public void init(JavacTask task, String... args) {
    System.out.println("in the plugin!");
  }
}
