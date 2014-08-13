package com.byteslounge.cdi.converter;

/**
 * Converts a resolved property into an expected {@link Double} value.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class DoublePropertyConverter implements PropertyConverter<Double> {

    /**
     * See {@link PropertyConverter#convert(String)}
     */
    @Override
    public Double convert(String value) {
        return Double.parseDouble(value);
    }

}
