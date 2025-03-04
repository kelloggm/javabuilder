package org.code.protocol;

/** Expected keys in the optional detail object of {@link ClientMessage}s */
public class ClientMessageDetailKeys {
  public static final String FILENAME = "filename";
  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  public static final String X = "x";
  public static final String Y = "y";
  public static final String ID = "id";

  // Playground specific
  public static final String TEXT = "text";
  public static final String COLOR_RED = "colorRed";
  public static final String COLOR_BLUE = "colorBlue";
  public static final String COLOR_GREEN = "colorGreen";
  public static final String FONT = "font";
  public static final String FONT_STYLE = "fontStyle";
  public static final String ROTATION = "rotation";
  public static final String UPDATES = "updates";

  // Theater specific
  public static final String PROMPT = "prompt";
  public static final String UPLOAD_URL = "uploadUrl";

  // Exception specific
  public static final String CONNECTION_ID = "connectionId";
  public static final String CAUSE = "cause";
  public static final String CAUSE_MESSAGE = "causeMessage";
}
