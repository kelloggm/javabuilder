package org.code.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class UserFacingThrowableMessageTest {
  @Test
  public void getFormattedMessageIncludesDetails() {
    HashMap<String, String> details = new HashMap<>();
    details.put("foo", "bar");
    ClientMessage message =
        new UserFacingThrowableMessage(InternalErrorKey.INTERNAL_ERROR, details);
    assertEquals(
        message.getFormattedMessage(),
        "{\"detail\":{\"foo\":\"bar\"},\"type\":\"EXCEPTION\",\"value\":\"INTERNAL_EXCEPTION\"}");
  }

  @Test
  public void getFormattedSkipsDetailsIfMissing() {
    ClientMessage message =
        new UserFacingThrowableMessage(InternalErrorKey.INTERNAL_ERROR, null);
    assertEquals(
        message.getFormattedMessage(), "{\"type\":\"EXCEPTION\",\"value\":\"INTERNAL_EXCEPTION\"}");
  }
}
