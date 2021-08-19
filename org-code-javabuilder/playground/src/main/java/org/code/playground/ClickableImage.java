package org.code.playground;

import java.util.UUID;

public class ClickableImage {
  private String filename;
  private int width;
  private int height;
  private int x;
  private int y;
  private final String id;

  public ClickableImage(String filename, int width, int height, int x, int y) {
    this.filename = filename;
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
    this.id = UUID.randomUUID().toString();
  }

  public void onClick() {
    System.out.println("clicked image " + this.id + "!");
  }

  public String getId() {
    return this.id;
  }

  public int getX() {
    return this.x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getWidth() {
    return this.width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return this.height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getFilename() {
    return this.filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }
}
