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
package com.byteslounge.cdi.resolver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.annotation.Property;
import com.byteslounge.cdi.configuration.ExtensionConfiguration;
import com.byteslounge.cdi.converter.PropertyConverter;
import com.byteslounge.cdi.converter.PropertyConverterFactory;
import com.byteslounge.cdi.exception.PropertyResolverException;
import com.byteslounge.cdi.resolver.bean.PropertyResolverBean;
import com.byteslounge.cdi.utils.MessageUtils;

/**
 * Checks if the CDI injection target has any fields annotated with
 * {@link Property} and injects the respective resolved properties accordingly.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class PropertyResolverInjectionTarget<T> implements InjectionTarget<T> {

    private final InjectionTarget<T> injectionTarget;
    private final AnnotatedType<T> annotatedType;
    private final PropertyResolverBean propertyResolverBean;
    private static final Logger logger = LoggerFactory.getLogger(PropertyResolverInjectionTarget.class);

    public PropertyResolverInjectionTarget(InjectionTarget<T> injectionTarget, AnnotatedType<T> annotatedType, PropertyResolverBean propertyResolverBean) {
        this.injectionTarget = injectionTarget;
        this.annotatedType = annotatedType;
        this.propertyResolverBean = propertyResolverBean;
    }

    /**
     * See {@link InjectionTarget#dispose(Object)}
     */
    @Override
    public void dispose(T instance) {
        injectionTarget.dispose(instance);
    }

    /**
     * See {@link InjectionTarget#getInjectionPoints()}
     */
    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return injectionTarget.getInjectionPoints();
    }

    /**
     * See {@link InjectionTarget#produce(CreationalContext)}
     */
    @Override
    public T produce(CreationalContext<T> ctx) {
        return injectionTarget.produce(ctx);
    }

    /**
     * See {@link InjectionTarget#inject(Object, CreationalContext)}
     */
    @Override
    public void inject(T instance, CreationalContext<T> ctx) {
        injectionTarget.inject(instance, ctx);
        for (Field field : annotatedType.getJavaClass().getDeclaredFields()) {
            Property annotation = field.getAnnotation(Property.class);
            if (annotation != null) {
                String key = annotation.value();
                String bundleName = annotation.resourceBundleBaseName().length() > 0 ? annotation.resourceBundleBaseName() : ExtensionConfiguration.INSTANCE
                        .getResourceBundleDefaultBaseName();
                if (bundleName == null) {
                    String errorMessage = "Property bundle name must have a configured default value (see github project instructions) or it must be configured in @"
                            + Property.class.getSimpleName() + " annotation.";
                    logger.error(errorMessage);
                    throw new PropertyResolverException(errorMessage);
                }
                field.setAccessible(true);
                try {
                    Object value = propertyResolverBean.resolveProperty(key, bundleName, ctx);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Resolved property with key " + key + " to " + value);
                    }
                    if (annotation.parameters().length > 0) {
                        if (field.getType().equals(String.class)) {
                            value = MessageUtils.formatMessage((String) value, (Object[]) annotation.parameters());
                        } else {
                            logger.warn("Found property with defined parameters for formatting but property type is not of type "
                                    + String.class.getSimpleName() + ". Skipping message format... [" + field.getDeclaringClass().getSimpleName() + "."
                                    + field.getName() + "]");
                        }
                    }
                    PropertyConverter<?> propertyConverter = PropertyConverterFactory.getConverter(field.getType());
                    if (propertyConverter != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Converting property using converter " + propertyConverter.getClass().getSimpleName());
                        }
                        value = propertyConverter.convert((String) value);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Applying property value " + value + " to field " + field.getDeclaringClass().getSimpleName() + "." + field.getName());
                    }
                    field.set(instance, value);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Could not resolve property", e);
                }
            }
        }
    }

    /**
     * See {@link InjectionTarget#postConstruct(Object)}
     */
    @Override
    public void postConstruct(T instance) {
        injectionTarget.postConstruct(instance);
    }

    /**
     * See {@link InjectionTarget#preDestroy(Object)}
     */
    @Override
    public void preDestroy(T instance) {
        injectionTarget.preDestroy(instance);
    }

}
