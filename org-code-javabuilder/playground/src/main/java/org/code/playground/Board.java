package org.code.playground;

import java.io.FileNotFoundException;
import java.util.HashMap;
import org.code.protocol.*;

public class Board {

  private static final int BOARD_WIDTH = 400;
  private static final int BOARD_HEIGHT = 400;

  private final PlaygroundMessageHandler playgroundMessageHandler;
  private final InputHandler inputHandler;
  private final AssetFileHelper assetFileHelper;

  private boolean firstRunStarted;
  private boolean isRunning;

  private final HashMap<String, ClickableImage> clickableImages;
  private final HashMap<String, Item> items;
  private int nextItemIndex;

  protected Board() {
    this(
        PlaygroundMessageHandler.getInstance(),
        GlobalProtocol.getInstance().getInputHandler(),
        GlobalProtocol.getInstance().getAssetFileHelper());
  }

  Board(
      PlaygroundMessageHandler playgroundMessageHandler,
      InputHandler inputHandler,
      AssetFileHelper assetFileHelper) {
    this.playgroundMessageHandler = playgroundMessageHandler;
    this.inputHandler = inputHandler;
    this.assetFileHelper = assetFileHelper;
    this.firstRunStarted = false;
    this.isRunning = false;
    this.items = new HashMap<>();
    this.clickableImages = new HashMap<>();
    this.nextItemIndex = 0;
  }

  /**
   * Returns the width of the board. This will always be 400.
   *
   * @return the width of the board in pixels.
   */
  public int getWidth() {
    return BOARD_WIDTH;
  }

  /**
   * Returns the height of the board. This will always be 400.
   *
   * @return the height of the board in pixels.
   */
  public int getHeight() {
    return BOARD_HEIGHT;
  }

  /**
   * Sets the background of the board to the provided image. The image will be scaled to fit the
   * full board, which may distort the image if it is not square.
   *
   * @param filename the name of the file from the asset manager to put in the background
   * @throws FileNotFoundException if the file cannot be found in the asset manager
   */
  public void setBackgroundImage(String filename) throws FileNotFoundException {
    this.assetFileHelper.verifyAssetFilename(filename);

    final HashMap<String, String> details = new HashMap<>();
    details.put(ClientMessageDetailKeys.FILENAME, filename);

    this.playgroundMessageHandler.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.SET_BACKGROUND_IMAGE, details));
  }

  /**
   * Adds a clickable image to the board.
   *
   * @param image the image to add. If the image is already on the board, this method does nothing.
   */
  public void addClickableImage(ClickableImage image) {
    if (this.clickableImages.containsKey(image.getId())) {
      return;
    }
    HashMap<String, String> details = image.getDetails();
    this.addIndexToDetails(details);
    this.clickableImages.put(image.getId(), image);
    this.playgroundMessageHandler.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.ADD_CLICKABLE_ITEM, details));
    image.turnOnChangeMessages();
  }

  /**
   * Removes the clickable image from the board.
   *
   * @param image the image to remove. If the image is not on the board, this method does nothing.
   */
  public void removeClickableImage(ClickableImage image) {
    if (!this.clickableImages.containsKey(image.getId())) {
      return;
    }
    this.clickableImages.remove(image.getId());
    HashMap<String, String> details = image.getRemoveDetails();
    this.playgroundMessageHandler.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.REMOVE_ITEM, details));
    image.turnOffChangeMessages();
  }

  /**
   * Adds a non-clickable ImageItem to the board.
   *
   * @param item the image item to add. If the item is already on the board, this method does
   *     nothing.
   */
  public void addImageItem(ImageItem item) {
    this.addItemHelper(item, PlaygroundSignalKey.ADD_IMAGE_ITEM);
  }

  /**
   * Adds a TextItem to the board.
   *
   * @param item the text item to add. If the item is already on the board, this method does
   *     nothing.
   */
  public void addTextItem(TextItem item) {
    this.addItemHelper(item, PlaygroundSignalKey.ADD_TEXT_ITEM);
  }

  /**
   * Removes the non-clickable item from the board.
   *
   * @param item the item to remove. If the image is not on the board, this method does nothing.
   */
  public void removeItem(Item item) {
    if (!this.items.containsKey(item.getId())) {
      return;
    }
    this.items.remove(item.getId());
    HashMap<String, String> details = item.getRemoveDetails();
    this.playgroundMessageHandler.sendMessage(
        new PlaygroundMessage(PlaygroundSignalKey.REMOVE_ITEM, details));
    item.turnOffChangeMessages();
  }

  /**
   * Plays a sound from the asset manager.
   *
   * @param filename the name of the sound file from the asset manager to play
   * @throws FileNotFoundException when the sound file cannot be found.
   */
  public void playSound(String filename) throws FileNotFoundException {
    this.assetFileHelper.verifyAssetFilename(filename);

    HashMap<String, String> details = new HashMap<>();
    details.put(ClientMessageDetailKeys.FILENAME, filename);

    PlaygroundMessage playSoundMessage =
        new PlaygroundMessage(PlaygroundSignalKey.PLAY_SOUND, details);
    this.playgroundMessageHandler.sendMessage(playSoundMessage);
  }

  /**
   * Starts the playground game, waiting for the user to click on images and executing the
   * appropriate code. To end the game, call the end() method. The start() method may only be called
   * once per execution of a program.
   *
   * @throws PlaygroundException if the start() method has already been called.
   */
  public void start() throws PlaygroundException {
    if (this.firstRunStarted) {
      throw new PlaygroundException(PlaygroundExceptionKeys.PLAYGROUND_RUNNING);
    }
    this.playgroundMessageHandler.sendMessage(new PlaygroundMessage(PlaygroundSignalKey.RUN));
    // send all initial setup messages now
    this.playgroundMessageHandler.sendBatchedMessages();

    this.firstRunStarted = true;
    this.isRunning = true;

    // Keep waiting for user input while game is running
    while (this.isRunning) {
      final String message = this.inputHandler.getNextMessageForType(InputMessageType.PLAYGROUND);
      if (message != null) {
        this.handleClickEvent(message);
      }
      if (this.isRunning) {
        // Only need to send update complete if the game is still running
        this.playgroundMessageHandler.sendMessage(
            new PlaygroundMessage(PlaygroundSignalKey.UPDATE_COMPLETE));
      }
      this.playgroundMessageHandler.sendBatchedMessages();
    }
  }

  /**
   * Ends the game, plays the sound supplied, and stops program execution.
   *
   * @param endingSound the name of a sound file in the asset manager to play at the end of the
   *     game.
   * @throws PlaygroundException if the start() method has not been called.
   * @throws FileNotFoundException if the sound file cannot be found.
   */
  public void end(String endingSound) throws PlaygroundException, FileNotFoundException {
    this.confirmIsRunning();
    this.playSound(endingSound);
    this.sendExitMessageAndEndRun();
  }

  /**
   * Ends the game and stops program execution.
   *
   * @throws PlaygroundException if the start() method has not been called.
   */
  public void end() throws PlaygroundException {
    this.confirmIsRunning();
    this.sendExitMessageAndEndRun();
  }

  private void handleClickEvent(String id) {
    if (!this.clickableImages.containsKey(id)) {
      return;
    }
    ClickableImage image = this.clickableImages.get(id);
    image.onClick();
  }

  private void addItemHelper(Item item, PlaygroundSignalKey signalKey) {
    if (this.items.containsKey(item.getId())) {
      return;
    }
    this.items.put(item.getId(), item);
    HashMap<String, String> itemDetails = item.getDetails();
    this.addIndexToDetails(itemDetails);
    this.playgroundMessageHandler.sendMessage(new PlaygroundMessage(signalKey, itemDetails));
    item.turnOnChangeMessages();
  }

  private void addIndexToDetails(HashMap<String, String> details) {
    details.put("index", Integer.toString(this.nextItemIndex));
    this.nextItemIndex++;
  }

  private void sendExitMessageAndEndRun() {
    this.playgroundMessageHandler.sendMessage(new PlaygroundMessage(PlaygroundSignalKey.EXIT));
    this.isRunning = false;
    this.playgroundMessageHandler.disableMessages();
  }

  private void confirmIsRunning() throws PlaygroundException {
    if (!this.isRunning) {
      throw new PlaygroundException(PlaygroundExceptionKeys.PLAYGROUND_NOT_RUNNING);
    }
  }
}
