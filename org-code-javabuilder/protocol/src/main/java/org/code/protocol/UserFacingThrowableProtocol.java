package org.code.protocol;

public interface UserFacingThrowableProtocol {
  String getLoggingString();
  ClientMessage getExceptionMessage();
}
