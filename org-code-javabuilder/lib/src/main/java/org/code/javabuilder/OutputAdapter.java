package org.code.javabuilder;

import org.code.util.ClientMessage;

public interface OutputAdapter {
  /** @param message An output from the user program */
  void sendMessage(ClientMessage message);
}
