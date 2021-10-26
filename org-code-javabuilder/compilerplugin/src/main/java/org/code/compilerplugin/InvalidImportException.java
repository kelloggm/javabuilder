package org.code.compilerplugin;
import org.code.protocol.JavabuilderRuntimeException;

public class InvalidImportException extends JavabuilderRuntimeException {
    protected InvalidImportException(Enum key) {
        super(key);
    }
}
