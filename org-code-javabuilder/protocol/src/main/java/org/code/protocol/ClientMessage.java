package org.code.protocol;

import java.util.HashMap;
import org.json.JSONObject;

/**
 * This ensures all messages passed to the client match the client-server contract for messages
 * generated by Javabuilder. Messages will use the following format:
 *
 * <pre>
 * {
 *   "type": <"ClientMessageType">,
 *   "value": <"second-level message data">,
 *   "detail": {
 *     <"optional": "additional fields">
 *   }
 * }
 * </pre>
 *
 * The "type" maps to an output type on the client side. The "value" will be interpreted by a client
 * library keyed on the given "type." The optional "detail" field allows us to pass additional
 * details to the client about the message. This allows us to handle user-facing messages,
 * exceptions, and program signals in distinct ways on the client-side.
 */
public abstract class ClientMessage {
  private final ClientMessageType type;
  private final Enum value;
  private final JSONObject detail;

  protected ClientMessage(ClientMessageType type, Enum value, HashMap<String, String> detail) {
    this.type = type;
    this.value = value;
    this.detail = new JSONObject(detail);
  }

  protected ClientMessage(ClientMessageType type, Enum value) {
    this.type = type;
    this.value = value;
    this.detail = new JSONObject();
  }

  protected ClientMessage(ClientMessageType type, Enum value, JSONObject detail) {
    this.type = type;
    this.value = value;
    this.detail = detail;
  }

  public ClientMessageType getType() {
    return type;
  }

  public String getValue() {
    return value.toString();
  }

  public JSONObject getDetail() {
    return detail;
  }

  /** @return A stringified JSON blob representing the client message */
  public String getFormattedMessage() {
    JSONObject formattedMessage = new JSONObject();
    formattedMessage.put("type", this.type);
    formattedMessage.put("value", this.value);
    if (this.detail.length() > 0) {
      formattedMessage.put("detail", this.detail);
    }
    return formattedMessage.toString();
  }
}
