package com.byteslounge.cdi.resolver.locale;

import java.util.Locale;

import javax.faces.context.FacesContext;

/**
 * Represents a JSF {@link Locale} resolver used to fetch the respective
 * resource bundle.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class JSFLocaleResolver implements LocaleResolver {

    /**
     * See {@link LocaleResolver#getLocale()}
     */
    @Override
    public Locale getLocale() {
        return FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }

}
