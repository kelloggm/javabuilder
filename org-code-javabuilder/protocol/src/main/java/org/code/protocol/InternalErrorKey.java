package org.code.protocol;

/** These keys map to client-side strings that are translatable. */
public enum InternalErrorKey {
  // We caused an error while executing the user's program.
  INTERNAL_RUNTIME_ERROR,
  // We caused an error while compiling the user's program.
  INTERNAL_COMPILER_ERROR,
  // We caused an error.
  INTERNAL_ERROR
}
