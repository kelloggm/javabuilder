package org.code.javabuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import org.code.protocol.*;
import org.code.protocol.InternalJavabuilderError;

/** The orchestrator for code compilation and execution. */
public class CodeBuilder implements AutoCloseable {
  private final OutputAdapter outputAdapter;
  private final InputAdapter inputAdapter;
  private final File tempFolder;
  private final PrintStream sysout;
  private final InputStream sysin;
  private final UserProjectFiles userProjectFiles;

  public CodeBuilder(GlobalProtocol protocol, UserProjectFiles userProjectFiles)
      throws UserFacingException {
    this.sysout = System.out;
    this.sysin = System.in;
    this.outputAdapter = protocol.getOutputAdapter();
    this.inputAdapter = protocol.getInputAdapter();
    this.userProjectFiles = userProjectFiles;
    try {
      this.tempFolder = Files.createTempDirectory("tmpdir").toFile();
    } catch (IOException e) {
      throw new InternalJavabuilderError(InternalErrorKey.INTERNAL_ERROR, e);
    }
  }

  /**
   * Saves non-source code assets to storage and compiles the user's code.
   *
   * @throws InternalJavabuilderError if the user's code contains a compiler error or if we are
   *     unable to compile due to internal errors.
   */
  public void buildUserCode() throws UserFacingException, InternalJavabuilderError {
    this.saveProjectAssets();
    UserCodeCompiler codeCompiler =
        new UserCodeCompiler(
            this.userProjectFiles.getJavaFiles(), this.tempFolder, this.outputAdapter);
    codeCompiler.compileProgram();
  }

  /**
   * Replaces System.in and System.out with our custom implementation and executes the user's code.
   */
  public void runUserCode()
      throws UserFacingException, InternalFacingException, InternalJavabuilderError {
    System.setOut(new OutputPrintStream(this.outputAdapter));
    System.setIn(new InputRedirectionStream(this.inputAdapter));
    JavaRunner runner;
    try {
      runner =
          new JavaRunner(this.tempFolder.toURI().toURL(), this.userProjectFiles.getJavaFiles());
    } catch (MalformedURLException e) {
      throw new InternalJavabuilderError(InternalErrorKey.INTERNAL_RUNTIME_ERROR, e);
    }
    runner.runCode();
  }

  /**
   * Resets System.in and System.out. Removes the temporary folder we generated to compile the
   * user's code.
   *
   * @throws InternalFacingException if the folder cannot be deleted.
   */
  @Override
  public void close() throws InternalFacingException {
    System.setOut(this.sysout);
    System.setIn(this.sysin);
    if (this.tempFolder != null) {
      try {
        // Recursively delete the temp folder
        Files.walk(this.tempFolder.toPath())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      } catch (IOException e) {
        throw new InternalFacingException(e.toString(), e);
      }
    }
  }

  /** Save any non-source code files to storage */
  private void saveProjectAssets() throws InternalJavabuilderError {
    // Save all text files to current folder.
    List<TextProjectFile> textProjectFiles = this.userProjectFiles.getTextFiles();
    for (TextProjectFile projectFile : textProjectFiles) {
      String filePath = projectFile.getFileName();
      try {
        Files.writeString(Path.of(filePath), projectFile.getFileContents());
      } catch (IOException e) {
        throw new InternalJavabuilderError(InternalErrorKey.INTERNAL_COMPILER_ERROR, e);
      }
    }
  }
}
