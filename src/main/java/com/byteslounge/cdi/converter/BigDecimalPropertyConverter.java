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
