package org.code.javabuilder;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class CodeCompiler {
  private final String className;
  public CodeCompiler(String fileName) {
    if (fileName.indexOf(".java") > 0) {
      className = fileName.substring(0, fileName.indexOf(".java"));
    } else {
      // TODO: Error
      throw new RuntimeException("error!");
    }
  }

  public boolean compileProgram() {
    File tempFolder = null;
    try {
      tempFolder = Files.createTempDirectory("tmpdir").toFile();
    } catch (IOException e) {
      // TODO: Error
      throw new RuntimeException("error!");
    }
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

    JavaCompiler.CompilationTask task = getCompilationTask(userProgram, tempFolder, diagnostics);
    if (task == null) {
      return false;
    }

    boolean success = task.call();

    // diagnostics will include any compiler errors
    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
      // TODO: Diagnostics
      // this.compileRunService.sendMessages(principal.getName(), diagnostic.toString());
    }
    return success;
  }

  private JavaCompiler.CompilationTask getCompilationTask(
      UserProgram userProgram, File tempFolder, DiagnosticCollector<JavaFileObject> diagnostics) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    // set output of compilation to be a temporary folder
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    try {
      fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempFolder));
    } catch (IOException e) {
      e.printStackTrace();
      // if we can't set the file location we won't be able to run the class properly, so return
      // null
      return null;
    }

    // create file for user-provided code
    JavaFileObject file =
        new JavaSourceFromString(userProgram.getClassName(), userProgram.getCode());
    Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);

    // create compilation task
    CompilationTask task =
        compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
    return task;
  }
}
