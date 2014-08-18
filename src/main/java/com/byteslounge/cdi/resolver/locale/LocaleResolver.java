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
package com.byteslounge.cdi.resolver.locale;

import java.util.Locale;

/**
 * Represents a {@link Locale} resolver used to fetch the respective resource
 * bundle.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public interface LocaleResolver {

    /**
     * Get the contextual {@link Locale}
     * 
     * @return The contextual Locale
     */
    Locale getLocale();

}
