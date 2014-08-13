package com.byteslounge.cdi.resolver;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.annotation.PropertyBundle;
import com.byteslounge.cdi.annotation.PropertyLocale;
import com.byteslounge.cdi.annotation.PropertyResolver;

/**
 * The default property resolver.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@ApplicationScoped
public class DefaultPropertyResolver implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DefaultPropertyResolver.class);

    /**
     * The default property resolver method.
     * 
     * @param locale
     *            The locale which should be used to resolve the property
     * @param bundleName
     *            The resource bundle name which should be used to resolve the
     *            property
     * @param key
     *            The property key to be resolved
     * @return
     */
    @PropertyResolver
    public String resolveProperty(@PropertyLocale Locale locale, @PropertyBundle String bundleName, String key) {
        if (logger.isDebugEnabled()) {
            logger.debug("Resolving property. Locale " + locale.toString() + ", bundle: " + bundleName + ", key: " + key);
        }
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
        String value = bundle.getString(key);
        if (value != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Property [" + key + "] resolved to: " + value);
            }
            return value;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Could not resolve property: " + key);
        }
        return key;
    }

}
