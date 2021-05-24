package dev.javabuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.code.javabuilder.*;
import org.code.protocol.InternalErrorKey;
import org.code.protocol.InternalJavabuilderError;
import org.code.protocol.UserFacingException;

/** Intended for local testing only. Loads the main.json file from the resources folder. */
public class LocalProjectFileLoader implements ProjectFileLoader {

  @Override
  public UserProjectFiles loadFiles() throws UserFacingException, UserInitiatedException {
    try {
      String mainJson =
          Files.readString(Paths.get(getClass().getClassLoader().getResource("main.json").toURI()));
      return new UserProjectFileParser().parseFileJson(mainJson);
    } catch (IOException | URISyntaxException e) {
      throw new InternalJavabuilderError(InternalErrorKey.INTERNAL_ERROR, e);
    }
  }
}
