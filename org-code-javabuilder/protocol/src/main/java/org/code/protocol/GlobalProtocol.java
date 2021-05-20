package org.code.protocol;

public class GlobalProtocol {
  private static GlobalProtocol protocolInstance;
  private final OutputAdapter outputAdapter;
  private final InputAdapter inputAdapter;

  private GlobalProtocol(OutputAdapter outputAdapter, InputAdapter inputAdapter) {
    this.outputAdapter = outputAdapter;
    this.inputAdapter = inputAdapter;
  }

  public static void create(OutputAdapter outputAdapter, InputAdapter inputAdapter) throws UserFacingException {
    if (GlobalProtocol.protocolInstance != null) {
      throw new UserFacingException(UserFacingThrowableKey.INTERNAL_EXCEPTION);
    }

    GlobalProtocol.protocolInstance = new GlobalProtocol(outputAdapter, inputAdapter);
  }

  public static GlobalProtocol getInstance() {
    return GlobalProtocol.protocolInstance;
  }

  public OutputAdapter getOutputAdapter() {
    return this.outputAdapter;
  }

  public InputAdapter getInputAdapter() {
    return this.inputAdapter;
  }
}
