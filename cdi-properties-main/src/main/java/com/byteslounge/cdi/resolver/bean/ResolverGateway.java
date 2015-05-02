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
package com.byteslounge.cdi.resolver.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;

/**
 * A resolver gateway used by the extension during application startup.
 * The extension interacts with the resolver beans through this resolver bean gateway.
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class ResolverGateway {

    private final Map<Class<? extends ResolverBean<?>>, ResolverBean<?>> resolverBeanMap = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
    public ResolverGateway(ResolverBean<?>... resolvers) {
        for (ResolverBean<?> resolver : resolvers) {
            resolverBeanMap.put((Class<? extends ResolverBean<?>>) resolver.getClass(), resolver);
        }
    }

    /**
     * Processes a CDI managed bean through the managed resolver beans
     * 
     * @param at the CDI managed bean being processed
     */
    public void process(AnnotatedType<?> at) {
        for (ResolverBean<?> resolverBean : resolverBeanMap.values()) {
            resolverBean.process(at);
        }
    }

    /**
     * Initializes the resolvers which are managed by this gateway
     * 
     * @param beanManager the CDI container Bean Manager
     */
    public void intializeResolvers(BeanManager beanManager) {
        for (ResolverBean<?> resolverBean : resolverBeanMap.values()) {
            resolverBean.initialize(beanManager);
        }
    }

    /**
     * Returns a resolver which is managed by this gateway
     * 
     * @param klass the resolver class
     * 
     * @return the managed resolver which is mapped by the provided class
     */
    @SuppressWarnings("unchecked")
    public <T extends ResolverBean<?>> T getResolver(Class<T> klass) {
        return (T) resolverBeanMap.get(klass);
    }

}
