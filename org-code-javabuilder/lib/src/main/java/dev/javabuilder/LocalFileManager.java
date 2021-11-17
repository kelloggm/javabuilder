package dev.javabuilder;

import static dev.javabuilder.LocalWebserverConstants.DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.code.javabuilder.InternalServerError;
import org.code.protocol.InternalErrorKey;
import org.code.protocol.JavabuilderException;
import org.code.protocol.JavabuilderFileManager;

public class LocalFileManager implements JavabuilderFileManager {
  @Override
  public String writeToFile(String filename, byte[] inputBytes, String contentType)
      throws JavabuilderException {
    File file = Paths.get(System.getProperty("java.io.tmpdir"), DIRECTORY, filename).toFile();
    try {
      File parentDirectory = Paths.get(System.getProperty("java.io.tmpdir"), DIRECTORY).toFile();
      if (!parentDirectory.exists()) {
        parentDirectory.mkdirs();
      }
      Files.write(file.toPath(), inputBytes);
    } catch (IOException e) {
      throw new InternalServerError(InternalErrorKey.INTERNAL_RUNTIME_EXCEPTION, e);
    }
    return String.format("http://localhost:8080/%s/%s", DIRECTORY, filename);
  }

  @Override
  public String getUploadUrl(String filename) throws JavabuilderException {
    // TODO: Return upload URL for localhost uploads
    return null;
  }

  @Override
  public URL getFileUrl(String filename) {
    // TODO: Return file URL for locally uploaded files
    return null;
  }
}
