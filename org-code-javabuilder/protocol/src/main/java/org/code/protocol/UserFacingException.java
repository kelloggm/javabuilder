package org.code.protocol;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * A checked exception caused by us that is intended to be seen by the user. These are the conceptual
 * equivalent of HTTP 500 errors.
 */
public class UserFacingException extends Exception {
  private final UserFacingThrowableKey key;

  public UserFacingException(UserFacingThrowableKey key) {
    super(key.toString());
    this.key = key;
  }

  public UserFacingException(UserFacingThrowableKey key, Exception cause) {
    super(key.toString(), cause);
    this.key = key;
  }

  public UserFacingThrowableMessage getExceptionMessage() {
    HashMap<String, String> detail = new HashMap<>();
    detail.put("connectionId", Properties.getConnectionId());
    if (this.getCause() != null) {
      detail.put("cause", this.getLoggingString());
    }
    return new UserFacingThrowableMessage(this.key, detail);
  }

  /** @return A pretty version of the exception and stack trace. */
  public String getLoggingString() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    this.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
