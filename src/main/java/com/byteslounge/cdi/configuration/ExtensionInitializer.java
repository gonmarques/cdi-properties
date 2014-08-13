package com.byteslounge.cdi.configuration;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.resolver.locale.JSFLocaleResolver;
import com.byteslounge.cdi.resolver.locale.LocaleResolverFactory;

/**
 * Initializer which will configure the extension in a web application context.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class ExtensionInitializer implements ServletContainerInitializer {

    public static final String DEFAULT_RESOURCE_BUNDLE_BASE_NAME_PARAM = "defaultResourceBundleBaseName";
    private static final Logger logger = LoggerFactory.getLogger(ExtensionInitializer.class);

    /**
     * See {@link ServletContainerInitializer#onStartup(Set, ServletContext)}
     */
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext context) throws ServletException {
        ExtensionConfiguration.INSTANCE.init();
        String defaultResourceBundleBaseName = context.getInitParameter(DEFAULT_RESOURCE_BUNDLE_BASE_NAME_PARAM);
        if (defaultResourceBundleBaseName != null) {
            logger.info("Found a default resource bundle name in web application context parameters. Will set it as: " + defaultResourceBundleBaseName);
            ExtensionConfiguration.INSTANCE.setResourceBundleDefaultBaseName(defaultResourceBundleBaseName);
        }
        LocaleResolverFactory.setLocaleResolver(new JSFLocaleResolver());
    }

}
