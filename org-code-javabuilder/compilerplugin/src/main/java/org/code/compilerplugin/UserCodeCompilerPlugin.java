package org.code.compilerplugin;

import com.sun.source.tree.ImportTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import java.util.Arrays;
import java.util.List;

public class UserCodeCompilerPlugin implements Plugin {
  private static final String[] validExactImports = new String[]{ "java.io.File", "org.code.neighborhood", "org.code.playground", "org.code.theater", "org.code.media" };
  private static final String[] validPackages = new String[]{"java.util"};

  @Override
  public String getName() {
    return "UserCodeCompilerPlugin";
  }

  @Override
  public void init(JavacTask task, String... args) {
    DiagnosticListener<JavaFileObject> listener = new DiagnosticCollector<>();
    task.addTaskListener(new TaskListener() {
      public void started(TaskEvent e) {
      }

      public void finished(TaskEvent e) {
        if (e.getKind() != TaskEvent.Kind.PARSE) {
          return;
        }
        // Perform instrumentation
        List<? extends ImportTree> importTreeList = e.getCompilationUnit().getImports();
        for(ImportTree importTree: importTreeList) {
          if (!isValidImport(importTree.getQualifiedIdentifier().toString())) {
            throw new InvalidImportException(ExceptionKeys.IMPORT_NOT_ALLOWED);
          }
        }
      }
    });
  }

  private boolean isValidImport(String packageName) {
    if (Arrays.asList(validExactImports).contains(packageName)) {
      return true;
    }
    // allow .* or .<specific-class> imports from valid packages
    for (int i = 0; i < validPackages.length; i++) {
      if (packageName.contains(validPackages[i])) {
        return true;
      }
    }
    return false;
  }
}
