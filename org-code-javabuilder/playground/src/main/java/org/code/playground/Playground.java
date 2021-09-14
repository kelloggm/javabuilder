package org.code.playground;

import java.util.HashMap;
import java.util.UUID;
import org.code.protocol.*;

public class Playground {
  private static final Playground playgroundInstance = new Playground();

  public static Playground getInstance() {
    return Playground.playgroundInstance;
  }

  private final OutputAdapter outputAdapter;
  private int nextIndex;
  private HashMap<String, ClickableItem> clickableItems;
  private HashMap<String, Item> items;
  private boolean isRunning;
  private final InputHandler inputHandler;
  private PlaygroundTimer playgroundTimer;

  public Playground() {
    this.outputAdapter = GlobalProtocol.getInstance().getOutputAdapter();
    this.nextIndex = 0;
    this.clickableItems = new HashMap<>();
    this.items = new HashMap<>();
    this.isRunning = false;
    this.inputHandler = GlobalProtocol.getInstance().getInputHandler();
    this.playgroundTimer = new PlaygroundTimer();
  }

  public void setBackgroundImage(String filename) {
    HashMap<String, String> details = new HashMap<>();
    details.put("filename", filename);
    this.outputAdapter.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.SET_BACKGROUND_IMAGE, details));
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

  public void removeClickableItem(ClickableItem item) {
    if (this.clickableItems.containsKey(item.getId())) {
      HashMap<String, String> details = new HashMap<>();
      details.put("id", item.getId());
      this.outputAdapter.sendMessage(
          new PlaygroundMessage(PlaygroundSignalKey.REMOVE_CLICKABLE_ITEM, details));
      this.clickableItems.remove(item.getId());
    }
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

  public void removeItem(Item item) {
    if (this.items.containsKey(item.getId())) {
      HashMap<String, String> details = new HashMap<>();
      details.put("id", item.getId());
      this.outputAdapter.sendMessage(
          new PlaygroundMessage(PlaygroundSignalKey.REMOVE_ITEM, details));
      this.items.remove(item.getId());
    }
  }

  protected void handleClickEvent(String id) {
    this.playgroundTimer.onMessageReceived();
    String eventId = UUID.randomUUID().toString();
    HashMap<String, String> clickEventDetails = new HashMap<>();
    clickEventDetails.put("id", eventId);
    this.outputAdapter.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.STARTED_CLICK_EVENT, clickEventDetails));
    if (!clickableItems.containsKey(id)) {
      return;
    }
    ClickableItem image = clickableItems.get(id);
    image.playClickSound();
    image.onClick();
    this.outputAdapter.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.FINISHED_CLICK_EVENT, clickEventDetails));
    this.playgroundTimer.onUpdateDispatched();
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

  public void exit() {
    this.isRunning = false;
  }
}
