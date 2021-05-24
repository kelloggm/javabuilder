package org.code.javabuilder;

import java.util.HashMap;
import org.code.protocol.ClientMessage;
import org.code.protocol.ClientMessageType;

/** A message directed to the client's terminal. Equivalent to System.out.print. */
public class SystemOutMessage extends ClientMessage {
  private enum MessageValue {
    MESSAGE
  }

  public SystemOutMessage(String message) {
    super(ClientMessageType.SYSTEM_OUT, MessageValue.MESSAGE, buildDetail(message));
  }

  private static HashMap<String, String> buildDetail(String message) {
    HashMap<String, String> detail = new HashMap<>();
    detail.put("message", message);
    return detail;
  }
}
