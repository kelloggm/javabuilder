package org.code.playground;

import java.io.FileNotFoundException;

public class ImageItem extends Item{
  
  /**
   * Creates an item that can be displayed in the Playground. An item consists of an image,
   * referenced by the name of the image file in the asset manager. The image for the item will
   * be scaled to fit the width and height provided, which may distort the image.
   *
   * @param filename the file name of the image for this item in the asset manager
   * @param x the distance, in pixels, from the left side of the board
   * @param y the distance, in pixels, from the top of the board
   * @param width the width of the item, in pixels
   * @param height the height of the item, in pixels
   * @throws FileNotFoundException if the file specified is not the in the asset manager
   */
  public ImageItem(String filename, int x, int y, int width, int height) throws FileNotFoundException;
  
  /**
   * Retrieve the filename for this item.
   *
   * @return the filename in the asset manager for the image associated with this item.
   */
  public String getFilename();
  
  /**
   * Sets the filename for this item.
   *
   * @param filename the filename in the asset manager for the image to associat with this item
   * @throws FileNotFoundException if the file specified is not the in the asset manager
   */
  public void getFilename(String filename) throws FileNotFoundException;
}
