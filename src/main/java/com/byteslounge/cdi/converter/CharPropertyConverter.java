package com.byteslounge.cdi.converter;

/**
 * Converts a resolved property into an expected {@link Character} value.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class CharPropertyConverter implements PropertyConverter<Character> {

    /**
     * See {@link PropertyConverter#convert(String)}
     */
    @Override
    public Character convert(String value) {
        return Character.valueOf(value.charAt(0));
    }

}
