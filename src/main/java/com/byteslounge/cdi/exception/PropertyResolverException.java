package com.byteslounge.cdi.exception;

/**
 * Represents an exception which is thrown while resolving a given property.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class PropertyResolverException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PropertyResolverException(String message) {
        super(message);
    }

    public PropertyResolverException(Throwable cause) {
        super(cause);
    }

    public PropertyResolverException(String message, Throwable cause) {
        super(message, cause);
    }

}
