package com.byteslounge.cdi.converter;

/**
 * Builds and returns the converter which will convert a resolved {@link String} property into the expected target field type.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
import java.math.BigDecimal;

public class PropertyConverterFactory {

    private PropertyConverterFactory() {
    }

    /**
     * Returns a property converter based on the target field type
     * 
     * @param type
     *            The target field type
     * @return The converter matching the expected field type or null if the
     *         expected type is not supported
     */
    public static PropertyConverter<?> getConverter(Class<?> type) {

        if (type.equals(Integer.class)) {
            return new IntegerPropertyConverter();
        }
        if (type.equals(Long.class)) {
            return new LongPropertyConverter();
        }
        if (type.equals(Short.class)) {
            return new ShortPropertyConverter();
        }
        if (type.equals(Float.class)) {
            return new FloatPropertyConverter();
        }
        if (type.equals(Double.class)) {
            return new DoublePropertyConverter();
        }
        if (type.equals(Character.class)) {
            return new CharPropertyConverter();
        }
        if (type.equals(Boolean.class)) {
            return new BooleanPropertyConverter();
        }
        if (type.equals(BigDecimal.class)) {
            return new BigDecimalPropertyConverter();
        }
        return null;
    }
}
