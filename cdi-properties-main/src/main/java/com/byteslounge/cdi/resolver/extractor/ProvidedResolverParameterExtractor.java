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
package com.byteslounge.cdi.resolver.extractor;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.AnnotatedParameter;

import com.byteslounge.cdi.extension.param.ResolverParameter;

/**
 * Represents a strategy used to extract the value of a property resolver method parameter. 
 * Subclasses must provide the parameter type and the corresponding value extractor
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public abstract class ProvidedResolverParameterExtractor<T extends ResolverParameter<?>> implements ResolverParameterExtractor<T> {

    private final Class<? extends Annotation> parameterType;
    private final T resolverParameter;

    public ProvidedResolverParameterExtractor(Class<? extends Annotation> parameterType, T resolverParameter) {
        this.parameterType = parameterType;
        this.resolverParameter = resolverParameter;
    }

    /**
     * see {@link ResolverParameterExtractor#extract(AnnotatedParameter)}
     */
    @Override
    public T extract(AnnotatedParameter<?> parameter) {
        if (parameter.isAnnotationPresent(parameterType)) {
            return resolverParameter;
        }
        return null;
    }
}
