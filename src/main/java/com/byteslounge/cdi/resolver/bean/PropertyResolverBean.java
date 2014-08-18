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
package com.byteslounge.cdi.resolver.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.annotation.PropertyBundle;
import com.byteslounge.cdi.annotation.PropertyLocale;
import com.byteslounge.cdi.resolver.PropertyResolverParameter;
import com.byteslounge.cdi.resolver.locale.LocaleResolverFactory;
import com.byteslounge.cdi.resolver.verifier.ParameterIndexVerifier;
import com.byteslounge.cdi.resolver.verifier.ResolverMethodParametersVerifier;
import com.byteslounge.cdi.utils.ValidationUtils;

/**
 * The bean which holds the reference to the property resolver instance and the
 * respective property resolver method, along with all the required metadata for
 * the resolver method invocation.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class PropertyResolverBean {

    private final AnnotatedMethod<?> propertyResolverMethod;
    private final BeanManager beanManager;
    private Object propertyResolverInstance;
    private List<InjectionPoint> propertyResolverParameters;
    private final int resolverLocaleParameterIndex;
    private final int resolverBundlenameParameterIndex;
    private static final Logger logger = LoggerFactory.getLogger(PropertyResolverBean.class);

    public PropertyResolverBean(AnnotatedMethod<?> propertyResolverMethod, BeanManager beanManager) {
        this.propertyResolverMethod = propertyResolverMethod;
        this.beanManager = beanManager;
        this.resolverLocaleParameterIndex = checkResolverParameter(PropertyLocale.class);
        this.resolverBundlenameParameterIndex = checkResolverParameter(PropertyBundle.class);
        ValidationUtils.validateResolverMethod(Arrays.asList(new ParameterIndexVerifier(resolverLocaleParameterIndex, resolverBundlenameParameterIndex),
                new ResolverMethodParametersVerifier(propertyResolverMethod, resolverLocaleParameterIndex, resolverBundlenameParameterIndex)));
    }

    /**
     * Initializes the property resolver bean instance and resolver method,
     * along with all method parameters which are eligible for CDI injection.
     */
    public void initializePropertyResolverBean() {
        Class<?> resolverMethodClass = propertyResolverMethod.getJavaMember().getDeclaringClass();
        Set<Bean<?>> beans = beanManager.getBeans(resolverMethodClass);
        Bean<?> propertyResolverBean = beanManager.resolve(beans);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(propertyResolverBean);
        propertyResolverInstance = beanManager.getReference(propertyResolverBean, resolverMethodClass, creationalContext);

        propertyResolverParameters = new ArrayList<>();

        int startIndex = 1;
        if (resolverLocaleParameterIndex > -1) {
            if (logger.isDebugEnabled()) {
                logger.debug("Resolver method expects Locale parameter at index " + resolverLocaleParameterIndex);
            }
            startIndex++;
        }
        if (resolverBundlenameParameterIndex > -1) {
            if (logger.isDebugEnabled()) {
                logger.debug("Resolver method expects Bundle Name parameter at index " + resolverBundlenameParameterIndex);
            }
            startIndex++;
        }

        int currentIndex = 0;
        for (final AnnotatedParameter<?> parameter : propertyResolverMethod.getParameters()) {
            if (currentIndex++ < startIndex) {
                continue;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Resolver method expects parameter of type " + ((Class<?>) parameter.getBaseType()).getSimpleName() + " at index " + currentIndex);
            }
            propertyResolverParameters.add(new PropertyResolverParameter(parameter, beanManager, propertyResolverBean));
        }
    }

    /**
     * Resolves a property to be injected into a CDI managed bean field.
     * 
     * @param key
     *            The property key to be resolved
     * @param bundleName
     *            The resource bundle name to which the property being resolved
     *            belongs to
     * @param ctx
     *            The CDI creational context
     * @return The resolved property
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public String resolveProperty(String key, String bundleName, CreationalContext<?> ctx) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        List<Object> parameters = new ArrayList<>();
        if (logger.isDebugEnabled()) {
            logger.debug("About to invoke property resolver method");
        }
        if (resolverLocaleParameterIndex > -1) {
            Locale locale = LocaleResolverFactory.getLocaleResolver().getLocale();
            parameters.add(locale);
            if (logger.isDebugEnabled()) {
                logger.debug("Added locale parameter to resolver method invocation: " + locale.toString());
            }
        }
        if (resolverBundlenameParameterIndex > -1) {
            parameters.add(bundleName);
            if (logger.isDebugEnabled()) {
                logger.debug("Added bundle name parameter to resolver method invocation: " + bundleName);
            }
        }
        parameters.add(key);
        if (logger.isDebugEnabled()) {
            logger.debug("Added key parameter to resolver method invocation: " + key);
        }
        for (InjectionPoint parameter : propertyResolverParameters) {
            parameters.add(beanManager.getInjectableReference(parameter, ctx));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Remaining parameters added. Invoking method.");
        }
        return (String) propertyResolverMethod.getJavaMember().invoke(propertyResolverInstance, parameters.toArray());
    }

    /**
     * Checks if a given property method resolver parameter is annotated with
     * {@link PropertyLocale} or {@link PropertyBundle} so they may be injected
     * by the CDI container.
     * 
     * @param annotationClass
     * @return The index of the parameter being checked. If the parameter is not
     *         present in the property resolver method the method returns -1
     */
    private int checkResolverParameter(Class<? extends Annotation> annotationClass) {
        int index = 0;
        for (Annotation[] annotations : propertyResolverMethod.getJavaMember().getParameterAnnotations()) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(annotationClass)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found resolver method parameter annotated with " + annotationClass.getSimpleName() + " at index " + index);
                    }
                    return index;
                }
                index++;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Resolver method parameter annotated with " + annotationClass.getSimpleName() + " was not found");
        }
        return -1;
    }

}
