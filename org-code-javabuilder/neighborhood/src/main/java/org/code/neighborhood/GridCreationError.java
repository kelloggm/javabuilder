package org.code.neighborhood;

import org.code.protocol.UserFacingError;
import org.code.protocol.InternalErrorKey;

public class GridCreationError extends UserFacingError {
  public GridCreationError(InternalErrorKey key) {
    super(key);
  }

  public GridCreationError(InternalErrorKey key, Throwable cause) {
    super(key, cause);
  }
}
