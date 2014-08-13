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
     * @return
     */
    Locale getLocale();

}
