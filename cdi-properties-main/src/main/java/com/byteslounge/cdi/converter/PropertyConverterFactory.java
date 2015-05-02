/*
 * Copyright 2014 byteslounge.com (Gonçalo Marques).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
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
