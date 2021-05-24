package org.code.protocol;

import org.code.protocol.InternalErrorKey;
import org.code.protocol.UserFacingError;

public class InternalJavabuilderError extends UserFacingError {
  public InternalJavabuilderError(InternalErrorKey key) {
    super(key);
  }
  public InternalJavabuilderError(InternalErrorKey key, Throwable cause) {
    super(key, cause);
  }
}
