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

/**
 * Creates {@link LocaleResolver} instances based on the property resolver
 * extension context (Web versus Stand alone)
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class LocaleResolverFactory {

    private static LocaleResolver localeResolver;

    private LocaleResolverFactory() {
    }

    /**
     * Gets the configured extension locale resolver
     * 
     * @return The extension configured locale resolver
     */
    public static LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    /**
     * Sets the configured extension locale resolver
     * 
     * @param localeResolver
     *            The configured extension locale resolver
     */
    public static void setLocaleResolver(LocaleResolver localeResolver) {
        LocaleResolverFactory.localeResolver = localeResolver;
    }

}
