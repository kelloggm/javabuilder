package org.code.playground;

import java.util.HashMap;
import org.code.protocol.GlobalProtocol;
import org.code.protocol.OutputAdapter;

public abstract class ClickableItem extends Item {
  private final OutputAdapter outputAdapter;
  private String clickSoundFilename;

  public ClickableItem(String filename, int width, int height, int x, int y) {
    super(filename, width, height, x, y);
    this.outputAdapter = GlobalProtocol.getInstance().getOutputAdapter();
  }

  public abstract void onClick();

  public void setClickSound(String filename) {
    this.clickSoundFilename = filename;
  }

  protected void playClickSound() {
    if (this.clickSoundFilename != null) {
      HashMap<String, String> changeDetails = new HashMap<>();
      changeDetails.put("filename", this.clickSoundFilename);
      this.outputAdapter.sendMessage(
          new PlaygroundMessage(PlaygroundSignalKey.PLAY_SOUND, changeDetails));
    }
  }

  public void setX(int x) {
    super.setX(x);
    this.sendChangedImageMessage("x", Integer.toString(x));
  }

  public void setY(int y) {
    super.setY(y);
    this.sendChangedImageMessage("y", Integer.toString(y));
  }

  public void setWidth(int width) {
    super.setWidth(width);
    this.sendChangedImageMessage("width", Integer.toString(width));
  }

  public void setHeight(int height) {
    super.setHeight(height);
    this.sendChangedImageMessage("height", Integer.toString(height));
  }

  public void setFilename(String filename) {
    super.setFilename(filename);
    this.sendChangedImageMessage("filename", filename);
  }

  private void sendChangedImageMessage(String changeKey, String changeValue) {
    HashMap<String, String> changeDetails = new HashMap<>();
    changeDetails.put(changeKey, changeValue);
    changeDetails.put("id", this.getId());
    this.outputAdapter.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.CHANGED_ITEM, changeDetails));
  }
}
