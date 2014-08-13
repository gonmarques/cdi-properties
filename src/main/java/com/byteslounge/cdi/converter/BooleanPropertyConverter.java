package com.byteslounge.cdi.converter;

/**
 * Converts a resolved property into an expected {@link Boolean} value.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class BooleanPropertyConverter implements PropertyConverter<Boolean> {

    /**
     * See {@link PropertyConverter#convert(String)}
     */
    @Override
    public Boolean convert(String value) {
        return Boolean.parseBoolean(value);
    }

}
