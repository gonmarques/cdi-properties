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
