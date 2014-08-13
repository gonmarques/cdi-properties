package com.byteslounge.cdi.exception;

/**
 * Represents an exception which is thrown during the extension initialization.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class ExtensionInitializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExtensionInitializationException(String message) {
        super(message);
    }

    public ExtensionInitializationException(Throwable cause) {
        super(cause);
    }

    public ExtensionInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
