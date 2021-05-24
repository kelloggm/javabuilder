package org.code.javabuilder;

import org.code.protocol.UserFacingException;

/** An exception caused by a user action. */
public class UserInitiatedException extends UserFacingException {

  public UserInitiatedException(UserInitiatedExceptionKey key) {
    super(key);
  }

  public UserInitiatedException(UserInitiatedExceptionKey key, Exception cause) {
    super(key, cause);
  }
}
