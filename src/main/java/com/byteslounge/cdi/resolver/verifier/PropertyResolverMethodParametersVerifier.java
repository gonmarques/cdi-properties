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

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;

import com.byteslounge.cdi.annotation.PropertyBundle;
import com.byteslounge.cdi.annotation.PropertyKey;
import com.byteslounge.cdi.annotation.PropertyLocale;
import com.byteslounge.cdi.exception.ExtensionInitializationException;

/**
 * Checks if the custom injected property method resolver parameters are
 * properly configured
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class PropertyResolverMethodParametersVerifier implements ResolverMethodVerifier {

    private final AnnotatedMethod<?> propertyResolverMethod;

    public PropertyResolverMethodParametersVerifier(AnnotatedMethod<?> propertyResolverMethod) {
        this.propertyResolverMethod = propertyResolverMethod;
    }

    /**
     * See {@link ResolverMethodVerifier#verify()}
     */
    @Override
    public void verify() {
        checkPropertyKeyExists();
        checkRepeatedParameterType(PropertyKey.class);
        checkRepeatedParameterType(PropertyLocale.class);
        checkRepeatedParameterType(PropertyBundle.class);
        checkMultipleAnnotationParameter();
    }

    /**
     * Checks if there is at least one property resolver method parameter annotated with {@link PropertyKey}
     */
    private void checkPropertyKeyExists() {
        boolean foundKeyProperty = false;
        for (final AnnotatedParameter<?> parameter : propertyResolverMethod.getParameters()) {
            if (parameter.isAnnotationPresent(PropertyKey.class)) {
                foundKeyProperty = true;
                break;
            }
        }
        if (!foundKeyProperty) {
            throw new ExtensionInitializationException(
                    "At least one parameter of the custom property resolver must represent de property key, annotated with "
                            + PropertyKey.class.getName());
        }
    }

    /**
     * Checks if the given parameter type exists in more than a single property resolver
     * method parameter
     * @param annotation
     *            The parameter type being checked
     */
    private void checkRepeatedParameterType(Class<? extends Annotation> annotation) {
        int count = 0;
        for (final AnnotatedParameter<?> parameter : propertyResolverMethod.getParameters()) {
            if (parameter.isAnnotationPresent(annotation)) {
                count++;
            }
        }
        if (count > 1) {
            throw new ExtensionInitializationException("There must be only a single param annotated with "
                    + annotation.getSimpleName() + " in the property resolver method");
        }
    }

    /**
     * Checks if any property resolver method parameter is annotated with 
     * more than one of the following: {@link PropertyKey}, {@link PropertyBundle}, 
     * {@link PropertyLocale}
     */
    private void checkMultipleAnnotationParameter() {
        int count;
        for (final AnnotatedParameter<?> parameter : propertyResolverMethod.getParameters()) {
            count = 0;
            if (parameter.isAnnotationPresent(PropertyKey.class)) {
                count++;
            }
            if (parameter.isAnnotationPresent(PropertyBundle.class)) {
                count++;
            }
            if (parameter.isAnnotationPresent(PropertyLocale.class)) {
                count++;
            }
            if (count > 1) {
                throw new ExtensionInitializationException(
                        "A property resolver method parameter must not be annotated with more than one of the following: "
                                + PropertyKey.class.getSimpleName() + ", " + PropertyBundle.class.getSimpleName()
                                + " or " + PropertyLocale.class.getSimpleName());
            }
        }
    }

}
