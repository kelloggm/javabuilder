package org.code.playground;

import java.util.HashMap;
import org.code.protocol.*;

public class Playground {
  private final OutputAdapter outputAdapter;
  private int nextIndex;
  private HashMap<String, ClickableItem> clickableItems;
  private HashMap<String, Item> items;
  private boolean isRunning;
  private final InputHandler inputHandler;

  public Playground() {
    this.outputAdapter = GlobalProtocol.getInstance().getOutputAdapter();
    this.nextIndex = 0;
    this.clickableItems = new HashMap<>();
    this.isRunning = false;
    this.inputHandler = GlobalProtocol.getInstance().getInputHandler();
  }

  public void addClickableItem(ClickableItem image) {
    if (this.clickableItems.containsKey(image.getId())) {
      return;
    } else {
      this.clickableItems.put(image.getId(), image);
    }
    HashMap<String, String> details = new HashMap<>();
    details.put("filename", image.getFilename());
    details.put("width", Integer.toString(image.getWidth()));
    details.put("height", Integer.toString(image.getHeight()));
    details.put("x", Integer.toString(image.getX()));
    details.put("y", Integer.toString(image.getY()));
    details.put("id", image.getId());
    details.put("index", Integer.toString(this.nextIndex));
    this.nextIndex++;
    this.outputAdapter.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.ADD_CLICKABLE_ITEM, details));
  }

  public void addItem(Item item) {
    if (this.items.containsKey(item.getId())) {
      return;
    } else {
      this.items.put(item.getId(), item);
    }
    HashMap<String, String> details = new HashMap<>();
    details.put("filename", item.getFilename());
    details.put("width", Integer.toString(item.getWidth()));
    details.put("height", Integer.toString(item.getHeight()));
    details.put("x", Integer.toString(item.getX()));
    details.put("y", Integer.toString(item.getY()));
    details.put("id", item.getId());
    details.put("index", Integer.toString(this.nextIndex));
    this.nextIndex++;
    this.outputAdapter.sendMessage(new PlaygroundMessage(PlaygroundSignalKey.ADD_ITEM, details));
  }

  protected void handleClickEvent(String id) {
    if (!clickableItems.containsKey(id)) {
      return;
    }
    ClickableItem image = clickableItems.get(id);
    image.playClickSound();
    image.onClick();
  }

  public void run() {
    if (this.isRunning) {
      // TODO throw exception
      return;
    }
    this.outputAdapter.sendMessage(new PlaygroundMessage(PlaygroundSignalKey.RUN, new HashMap<>()));
    this.isRunning = true;

    // Wait for next user input
    while (this.isRunning) {
      String message = this.inputHandler.getNextMessageForType(InputMessageType.PLAYGROUND);
      if (message != null) {
        this.handleClickEvent(message);
      }
    }
  }
}
