package org.code.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InternalJavabuilderErrorTest {
  @Test
  public void getExceptionMessageIncludesConnectionId() {
    InternalJavabuilderError exception =
        new InternalJavabuilderError(InternalErrorKey.INTERNAL_ERROR);
    UserFacingThrowableMessage message = exception.getExceptionMessage();
    assertEquals(message.getDetail().get("connectionId"), Properties.getConnectionId());
  }

  @Test
  public void getExceptionMessageIncludesCause() {
    InternalJavabuilderError exception =
        new InternalJavabuilderError(
            InternalErrorKey.INTERNAL_ERROR, new Exception("the cause of the exception"));
    UserFacingThrowableMessage message = exception.getExceptionMessage();
    assertTrue(message.getDetail().getString("cause").contains("the cause of the exception"));
  }
}
