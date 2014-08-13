package com.byteslounge.cdi.converter;

/**
 * Converts a resolved property into an expected {@link Integer} value.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class IntegerPropertyConverter implements PropertyConverter<Integer> {

    /**
     * See {@link PropertyConverter#convert(String)}
     */
    @Override
    public Integer convert(String value) {
        return Integer.parseInt(value);
    }

}
