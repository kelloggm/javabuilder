package org.code.playground;

import java.util.HashMap;
import java.util.UUID;
import org.code.protocol.GlobalProtocol;
import org.code.protocol.OutputAdapter;

public abstract class ClickableImage {
  private String filename;
  private int width;
  private int height;
  private int x;
  private int y;
  private final String id;
  private final OutputAdapter outputAdapter;

  public ClickableImage(String filename, int width, int height, int x, int y) {
    this.filename = filename;
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
    this.id = UUID.randomUUID().toString();
    this.outputAdapter = GlobalProtocol.getInstance().getOutputAdapter();
  }

  public abstract void onClick();

  public String getId() {
    return this.id;
  }

  public int getX() {
    return this.x;
  }

  public void setX(int x) {
    this.x = x;
    this.sendChangedImageMessage("x", Integer.toString(this.x));
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
    this.sendChangedImageMessage("y", Integer.toString(this.y));
  }

  public int getWidth() {
    return this.width;
  }

  public void setWidth(int width) {
    this.width = width;
    this.sendChangedImageMessage("width", Integer.toString(this.width));
  }

  public int getHeight() {
    return this.height;
  }

  public void setHeight(int height) {
    this.height = height;
    this.sendChangedImageMessage("height", Integer.toString(this.height));
  }

  public String getFilename() {
    return this.filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
    this.sendChangedImageMessage("filename", this.filename);
  }

  private void sendChangedImageMessage(String changeKey, String changeValue) {
    HashMap<String, String> changeDetails = new HashMap<>();
    changeDetails.put(changeKey, changeValue);
    changeDetails.put("id", this.getId());
    this.outputAdapter.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.CHANGED_IMAGE, changeDetails));
  }
}
