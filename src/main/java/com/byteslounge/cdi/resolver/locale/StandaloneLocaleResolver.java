package com.byteslounge.cdi.resolver.locale;

import java.util.Locale;

/**
 * Represents a stand alone {@link Locale} resolver used to fetch the respective
 * resource bundle.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class StandaloneLocaleResolver implements LocaleResolver {

    /**
     * See {@link LocaleResolver#getLocale()}
     */
    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

}
