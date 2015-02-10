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
package com.byteslounge.cdi.resolver.verifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.annotation.Property;
import com.byteslounge.cdi.annotation.PropertyBundle;
import com.byteslounge.cdi.annotation.PropertyKey;
import com.byteslounge.cdi.annotation.PropertyLocale;
import com.byteslounge.cdi.exception.ExtensionInitializationException;

/**
 * Checks if the custom injected property method resolver parameters are
 * properly configured
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class ResolverMethodParametersVerifier implements ResolverMethodVerifier {

    private final AnnotatedMethod<?> propertyResolverMethod;
    private static final Logger logger = LoggerFactory.getLogger(ResolverMethodParametersVerifier.class);

    public ResolverMethodParametersVerifier(AnnotatedMethod<?> propertyResolverMethod) {
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
        for (final AnnotatedParameter<?> parameter : propertyResolverMethod.getParameters()) {
            if (checkDependentScope((Class<?>) parameter.getBaseType())) {
                checkPropertyField((Class<?>) parameter.getBaseType(), (Class<?>) parameter.getBaseType());
            }
        }
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

    /**
     * Checks if a custom injected parameter scope is {@link Dependent}
     * 
     * @param type
     *            The parameter type being checked
     * @return Returns true if the paraeter scope is {@link Dependent}
     */
    private boolean checkDependentScope(Class<?> type) {
        for (Annotation annotation : type.getAnnotations()) {
            if (annotation.annotationType().equals(SessionScoped.class) || annotation.annotationType().equals(RequestScoped.class)
                    || annotation.annotationType().equals(ApplicationScoped.class)) {
                return false;
            }
            Class<?> viewScopedClass = null;
            try {
                // Account for JEE 7 @ViewScoped scope
                viewScopedClass = Class.forName("javax.faces.view.ViewScoped");
            } catch (Exception e) {
                // JEE 6 environment
                if (logger.isDebugEnabled()) {
                    logger.debug("Class javax.faces.view.ViewScoped was not found: Running in a Java EE 6 environment.");
                }
            }
            if (viewScopedClass != null) {
                if (annotation.annotationType().equals(viewScopedClass)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if a custom injected parameter has any field annotated with
     * {@link Property} (this function calls itself recursively).
     * 
     * @param originalType
     *            The type being checked
     * @param type
     *            The current type or original type super class being checked
     *            (this function calls itself recursively).
     */
    private void checkPropertyField(Class<?> originalType, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().equals(Property.class)) {
                    throw new ExtensionInitializationException(
                            "Property resolver method is injecting a Dependent scoped bean which in turn also has an injected @"
                                    + Property.class.getSimpleName()
                                    + " (this would cause a stack overflow because Dependent scoped bean instances are injected directly and not proxied). Type: "
                                    + originalType.getSimpleName() + ", field: " + field.getName());
                }
            }
        }
        if (type.getSuperclass() != null && !type.getSuperclass().equals(Object.class)) {
            checkPropertyField(originalType, type.getSuperclass());
        }
    }

}
