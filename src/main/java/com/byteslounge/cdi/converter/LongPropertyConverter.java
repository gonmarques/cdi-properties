package com.byteslounge.cdi.converter;

/**
 * Converts a resolved property into an expected {@link Long} value.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class LongPropertyConverter implements PropertyConverter<Long> {

    /**
     * See {@link PropertyConverter#convert(String)}
     */
    @Override
    public Long convert(String value) {
        return Long.parseLong(value);
    }

}
