package dev.javabuilder;

import java.util.HashMap;
import org.code.protocol.ClientMessage;
import org.code.protocol.ClientMessageType;

/** A message directed to the client's terminal. Equivalent to System.out.print. */
public class DebuggingMessage extends ClientMessage {
  private enum MessageValue {
    DEBUG_MESSAGE
  }

  public DebuggingMessage(String message) {
    super(
        ClientMessageType.SYSTEM_OUT,
        DebuggingMessage.MessageValue.DEBUG_MESSAGE,
        buildDetail(message));
  }

  private static HashMap<String, String> buildDetail(String message) {
    HashMap<String, String> detail = new HashMap<>();
    detail.put("message", message);
    return detail;
  }
}
