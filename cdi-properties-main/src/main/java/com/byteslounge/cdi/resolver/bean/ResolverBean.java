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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;

import com.byteslounge.cdi.resolver.context.ResolverContext;

/**
 * Interface used to define an extension's resolver method.
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public interface ResolverBean<T> {

    /**
     * Processes a CDI managed bean and checks if it defines an extension's resolver method
     * 
     * @param at the CDI managed bean to check for an extension's resolver method
     */
    void process(AnnotatedType<?> at);

    /**
     * Initializes the extension's resolver method
     * 
     * @param beanManager the CDI container Bean Manager
     */
    void initialize(BeanManager beanManager);
    
    /**
     * Invokes the extension's resolver method represented by this bean
     * 
     * @param resolverContext the Resolver Context
     * @param ctx The CDI creational context
     * 
     * @return the value returned by this extension's resolver method
     */
    T invoke(ResolverContext resolverContext, CreationalContext<?> ctx);

}
