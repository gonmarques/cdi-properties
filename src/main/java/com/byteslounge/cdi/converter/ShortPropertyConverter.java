package com.byteslounge.cdi.converter;

/**
 * Converts a resolved property into an expected {@link Short} value.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class ShortPropertyConverter implements PropertyConverter<Short> {

    /**
     * See {@link PropertyConverter#convert(String)}
     */
    @Override
    public Short convert(String value) {
        return Short.parseShort(value);
    }

}
