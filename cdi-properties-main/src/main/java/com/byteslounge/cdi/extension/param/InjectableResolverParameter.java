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
package com.byteslounge.cdi.extension.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import com.byteslounge.cdi.resolver.context.ResolverContext;

/**
 * Represents a resolver that will be used to evaluate the value 
 * of a resolver method parameter that shall be injected by the CDI container
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class InjectableResolverParameter implements InjectionPoint, ResolverParameter<Object> {

    private final AnnotatedParameter<?> parameter;
    private BeanManager beanManager;
    private Bean<?> resolverBean;

    public InjectableResolverParameter(AnnotatedParameter<?> parameter) {
        this.parameter = parameter;
    }

    /**
     * See {@link InjectionPoint#getAnnotated()}
     */
    @Override
    public Annotated getAnnotated() {
        return parameter;
    }

    /**
     * See {@link InjectionPoint#getBean()}
     */
    @Override
    public Bean<?> getBean() {
        return resolverBean;
    }

    /**
     * See {@link InjectionPoint#getMember()}
     */
    @Override
    public Member getMember() {
        return parameter.getDeclaringCallable().getJavaMember();
    }

    /**
     * See {@link InjectionPoint#getQualifiers()}
     */
    @Override
    public Set<Annotation> getQualifiers() {
        Set<Annotation> qualifiers = new HashSet<Annotation>();
        for (Annotation annotation : parameter.getAnnotations()) {
            if (beanManager.isQualifier(annotation.annotationType())) {
                qualifiers.add(annotation);
            }
        }
        return qualifiers;
    }

    /**
     * See {@link InjectionPoint#getType()}
     */
    @Override
    public Type getType() {
        return parameter.getBaseType();
    }

    /**
     * See {@link InjectionPoint#isDelegate()}
     */
    @Override
    public boolean isDelegate() {
        return false;
    }

    /**
     * See {@link InjectionPoint#isTransient()}
     */
    @Override
    public boolean isTransient() {
        return false;
    }

    /**
     * see {@link ResolverParameter#resolve(String, String, CreationalContext)}
     */
    @Override
    public <T> Object resolve(ResolverContext resolverContext, CreationalContext<T> ctx) {
        resolverBean = resolverContext.getResolverBean();
        beanManager = resolverContext.getBeanManager();
        return beanManager.getInjectableReference(this, ctx);
    }

}
