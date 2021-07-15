package org.code.javabuilder;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.code.protocol.InternalErrorKey;
import org.code.protocol.JavabuilderException;
import org.code.protocol.JavabuilderFileWriter;
import org.code.protocol.JavabuilderLogger;
import org.json.JSONObject;

public class AWSFileWriter implements JavabuilderFileWriter {
  private final String outputBucketName;
  private final AmazonS3 s3Client;
  private final String javabuilderSessionId;
  private final String getOutputURL;
  private final JavabuilderLogger logger;
  private int writes;

  // Temporary limit on writes per session until we can more fully limit usage.
  private static final int WRITES_PER_SESSION = 2;
  // Number of times to retry writing to S3 if we hit a failure
  private static final int MAX_RETRIES = 3;
  private static final int RETRY_TIMEOUT = 250;

  public AWSFileWriter(
      AmazonS3 s3Client,
      String outputBucketName,
      String javabuilderSessionId,
      String getOutputURL,
      JavabuilderLogger logger) {
    this.outputBucketName = outputBucketName;
    this.s3Client = s3Client;
    this.javabuilderSessionId = javabuilderSessionId;
    this.writes = 0;
    this.getOutputURL = getOutputURL;
    this.logger = logger;
  }

  @Override
  public String writeToFile(String filename, byte[] inputBytes, String contentType)
      throws JavabuilderException {
    if (this.writes >= WRITES_PER_SESSION) {
      throw new UserInitiatedException(UserInitiatedExceptionKey.TOO_MANY_WRITES);
    }
    String filePath = this.javabuilderSessionId + "/" + filename;
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(contentType);
    metadata.setContentLength(inputBytes.length);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
    if (this.putObjectWithRetry(filePath, inputStream, metadata)) {
      this.writes++;
    } else {
      // We couldn't write to S3, send a message to the user
      throw new UserFacingException(InternalErrorKey.INTERNAL_RUNTIME_EXCEPTION);
    }

    return this.getOutputURL + "/" + filePath;
  }

  private boolean putObjectWithRetry(String filePath, InputStream input, ObjectMetadata metadata) {
    boolean success = false;
    int tries = 0;
    while (tries < MAX_RETRIES && !success) {
      try {
        this.s3Client.putObject(this.outputBucketName, filePath, input, metadata);
        success = true;
      } catch (SdkClientException e) {
        JSONObject eventData = new JSONObject();
        eventData.put("type", "S3_WRITE_ERROR");
        eventData.put("stackTrace", e.getStackTrace());
        eventData.put("message", e.getMessage());
        this.logger.logError(eventData);
        try {
          Thread.sleep(RETRY_TIMEOUT);
        } catch (InterruptedException interruptedException) {
          /* No action required */
        }
      }
      tries++;
    }
    return success;
  }
}
