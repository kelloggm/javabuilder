package org.code.javabuilder;

import org.code.protocol.UserFacingException;

public interface ProjectFileLoader {
  UserProjectFiles loadFiles() throws UserFacingException, UserInitiatedException;
}
