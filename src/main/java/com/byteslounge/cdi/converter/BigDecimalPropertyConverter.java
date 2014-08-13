package com.byteslounge.cdi.converter;

import java.math.BigDecimal;

/**
 * Converts a resolved property into an expected {@link BigDecimal} value.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class BigDecimalPropertyConverter implements PropertyConverter<BigDecimal> {

    /**
     * See {@link PropertyConverter#convert(String)}
     */
    @Override
    public BigDecimal convert(String value) {
        return new BigDecimal(value);
    }

}
