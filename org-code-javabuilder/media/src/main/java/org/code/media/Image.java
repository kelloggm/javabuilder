package org.code.media;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.code.protocol.GlobalProtocol;

public class Image {
  private BufferedImage bufferedImage;

  /**
   * Creates a new image object, using the pixel information from the file uploaded to the asset
   * manager.
   *
   * @param filename the name of the image loaded into the asset manager for the project
   * @throws FileNotFoundException if the file doesn't exist in the asset manager.
   */
  public Image(String filename) throws FileNotFoundException {
    try {
      this.bufferedImage =
          ImageIO.read(new URL(GlobalProtocol.getInstance().generateAssetUrl(filename)));
    } catch (IOException e) {
      // TODO: improve error handling
      throw new FileNotFoundException();
    }
  }

  /**
   * Create a new image object, copying the source image provided.
   *
   * @param source the image to duplicate
   */
  public Image(Image source) {}

  /**
   * Creates an empty image filled with white pixels.
   *
   * @param width the width of the image to create.
   * @param height the height of the image to create.
   */
  public Image(int width, int height) {}

  /**
   * Get an array with all of the pixels of the image.
   *
   * @return the pixels in the image. This array will have a length equals to the width multiplied
   *     by the height.
   */
  public Pixel[] getPixels() {
    return null;
  }

  /**
   * Get the color value at the pixel specified.
   *
   * @param x the x position of the pixel
   * @param y the y position of the pixel
   * @return the color of the pixel
   */
  public Pixel getPixel(int x, int y) {
    return null;
  }

  /**
   * Set the color value at the pixel specified.
   *
   * @param x the x position of the pixel
   * @param y the y position of the pixel
   * @param color the color to set the pixel
   */
  public void setPixel(int x, int y, Color color) {}

  /**
   * Gets the width of the image in pixels.
   *
   * @return the width of the image in pixels.
   */
  public int getWidth() {
    return this.bufferedImage.getWidth();
  }

  /**
   * Gets the height of the image in pixels.
   *
   * @return the height of the image in pixels.
   */
  public int getHeight() {
    return this.bufferedImage.getHeight();
  }

  /**
   * Clears the image, filling it with the color provided.
   *
   * @param color the color with which to fill the image.
   */
  public void clear(Color color) {}

  public BufferedImage getBufferedImage() {
    return this.bufferedImage;
  }
}
