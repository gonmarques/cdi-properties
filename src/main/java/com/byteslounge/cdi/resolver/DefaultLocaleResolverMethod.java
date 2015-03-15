/*
 * Copyright 2015 byteslounge.com (Gonçalo Marques).
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
package com.byteslounge.cdi.resolver;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.annotation.LocaleResolver;
import com.byteslounge.cdi.resolver.locale.LocaleResolverFactory;

/**
 * The default locale resolver method.
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
@ApplicationScoped
public class DefaultLocaleResolverMethod implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DefaultLocaleResolverMethod.class);

    /**
     * The default locale resolver method.
     * 
     * @return The resolved locale
     */
    @LocaleResolver
    public Locale resolveLocale() {
        if (logger.isDebugEnabled()) {
            logger.debug("Resolving locale");
        }
        return LocaleResolverFactory.getLocaleResolver().getLocale();
    }

}
