package dev.javabuilder;

import static org.code.protocol.LoggerNames.MAIN_LOGGER;

import java.util.logging.Logger;
import org.code.javabuilder.*;
import org.code.protocol.*;

/**
 * Intended for local testing only. This is a local version of the Javabuilder lambda function. The
 * LocalInputAdapter can be used to pass input to the program. The "MyClass.java" program in the
 * resources folder is the "user program." Output goes to the console.
 */
public class LocalMain {
  public static void main(String[] args) {
    final LocalInputAdapter inputAdapter = new LocalInputAdapter();
    final LocalOutputAdapter outputAdapter = new LocalOutputAdapter(System.out);
    final LocalProjectFileLoader fileLoader = new LocalProjectFileLoader();

    Logger logger = Logger.getLogger(MAIN_LOGGER);
    logger.addHandler(new LocalLogHandler(System.out, "levelId", "channelId"));
    // turn off the default console logger
    logger.setUseParentHandlers(false);

    GlobalProtocol.create(outputAdapter, inputAdapter, "", "", "", new LocalFileWriter());

    // Create and invoke the code execution environment
    CodeBuilderWrapper codeBuilderWrapper = new CodeBuilderWrapper(fileLoader, outputAdapter);
    codeBuilderWrapper.executeCodeBuilder(ExecutionType.RUN);
  }
}
