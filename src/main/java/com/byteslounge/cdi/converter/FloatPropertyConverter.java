package com.byteslounge.cdi.converter;

/**
 * Converts a resolved property into an expected {@link Float} value.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class FloatPropertyConverter implements PropertyConverter<Float> {

    /**
     * See {@link PropertyConverter#convert(String)}
     */
    @Override
    public Float convert(String value) {
        return Float.parseFloat(value);
    }

}
