package org.code.javabuilder;

import java.net.URL;

public class ProjectHandler extends Thread {
  private final URL projectURL;
  private final String fileName;
  public ProjectHandler(String projectURL, String fileName) {
    this.projectURL = new URL(projectURL);
    this.fileName = fileName;
  }

  public void run() {
    ProjectLoader loader = new ProjectLoader(projectURL);
    String userCode = loader.getProject();
    CodeCompiler compiler = new CodeCompiler(fileName);
  }
}
