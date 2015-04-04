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
package com.byteslounge.cdi.resolver.verifier;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;

import com.byteslounge.cdi.annotation.PropertyLocale;
import com.byteslounge.cdi.exception.ExtensionInitializationException;

/**
 * Checks if a custom Locale resolver method parameter is annotated with {@link PropertyLocale}
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class LocaleResolverMethodParametersVerifier implements ResolverMethodVerifier {

    private final AnnotatedMethod<?> localeResolverMethod;

    public LocaleResolverMethodParametersVerifier(AnnotatedMethod<?> localeResolverMethod) {
        this.localeResolverMethod = localeResolverMethod;
    }

    /**
     * See {@link ResolverMethodVerifier#verify()}
     */
    @Override
    public void verify() {
        checkLocaleResolverParameterAnnotations();
    }

    /**
     * Checks if any property resolver method parameter is annotated with 
     * {@link PropertyLocale}
     */
    private void checkLocaleResolverParameterAnnotations() {
        for (final AnnotatedParameter<?> parameter : localeResolverMethod.getParameters()) {
            if (parameter.isAnnotationPresent(PropertyLocale.class)) {
                throw new ExtensionInitializationException(
                        "A Locale resolver method parameter must not be annotated with: "
                                + PropertyLocale.class.getSimpleName());
            }
        }
    }

}
