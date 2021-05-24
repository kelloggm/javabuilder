package org.code.protocol;

import java.util.HashMap;

/** An error message directed to the user. Equivalent of a user-visible 500 error. */
public class UserFacingThrowableMessage extends ClientMessage {
  UserFacingThrowableMessage(UserFacingThrowableKey key, HashMap<String, String> detail) {
    super(ClientMessageType.EXCEPTION, key, detail);
  }
}
