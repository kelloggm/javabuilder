package org.code.playground;

import org.code.protocol.GlobalProtocol;
import org.code.protocol.InputHandler;
import org.code.protocol.InputMessageType;

public class PlaygroundEventHandler {
  private final InputHandler inputHandler;
  private Thread eventListener;
  private Playground playground;

  public PlaygroundEventHandler(Playground playground) {
    this.playground = playground;
    this.inputHandler = GlobalProtocol.getInstance().getInputHandler();
  }

  protected void startListeningForEvents() {
    this.eventListener =
        new Thread(
            () -> {
              this.listenForEvents();
            });
    this.eventListener.start();
  }

  protected void listenForEvents() {
    if (this.eventListener == null) {
      return;
    }
    while (this.eventListener.isAlive()) {
      String message = this.inputHandler.getNextMessageForType(InputMessageType.PLAYGROUND);
      if (message != null) {
        this.playground.handleClickEvent(message);
      }
    }
  }

  protected void stopListeningForEvents() {
    this.eventListener.interrupt();
  }

  protected boolean isListeningForEvents() {
    return this.eventListener != null;
  }
}
