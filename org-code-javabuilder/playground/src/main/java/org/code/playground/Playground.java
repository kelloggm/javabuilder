package org.code.playground;

import java.util.HashMap;
import org.code.protocol.*;

public class Playground {
  private final OutputAdapter outputAdapter;
  private int nextIndex;
  private HashMap<String, ClickableImage> images;
  private final PlaygroundEventHandler playgroundEventHandler;

  public Playground() {
    this.outputAdapter = GlobalProtocol.getInstance().getOutputAdapter();
    this.nextIndex = 0;
    this.playgroundEventHandler = new PlaygroundEventHandler(this);
    this.images = new HashMap<>();
  }

  public void addImage(ClickableImage image) {
    if (this.images.containsKey(image.getId())) {
      return;
    } else {
      this.images.put(image.getId(), image);
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
    this.outputAdapter.sendMessage(new PlaygroundMessage(PlaygroundSignalKey.ADD_IMAGE, details));
  }

  protected void handleClickEvent(String id) {
    if (!images.containsKey(id)) {
      return;
    }
    ClickableImage image = images.get(id);
    image.onClick();
  }

  public void run() {
    if (!this.playgroundEventHandler.isListeningForEvents()) {
      this.playgroundEventHandler.startListeningForEvents();
    }
  }
}
