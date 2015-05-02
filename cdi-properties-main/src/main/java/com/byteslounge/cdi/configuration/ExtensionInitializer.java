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
        try {
            Class.forName("javax.faces.context.FacesContext");
            LocaleResolverFactory.setLocaleResolver(new JSFLocaleResolver());
        } catch (ClassNotFoundException e) {
            logger.info("FacesContext not present in the application's classpath");
        }
    }

}
