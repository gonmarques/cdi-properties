package com.byteslounge.cdi.converter;

/**
 * Defines a converter which will convert a resolved {@link String} property
 * into the expected target field value.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public interface PropertyConverter<T> {

    /**
     * Converts a resolved {@link String} property into the expected target
     * field value.
     * 
     * @param value
     *            The resolved {@link String} property
     * @return The property converted into the expected target field type
     */
    T convert(String value);

}
