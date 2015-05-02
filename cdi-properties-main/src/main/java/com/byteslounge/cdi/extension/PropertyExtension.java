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
package com.byteslounge.cdi.extension;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import com.byteslounge.cdi.configuration.ExtensionConfiguration;
import com.byteslounge.cdi.format.PropertyFormat;
import com.byteslounge.cdi.format.PropertyFormatFactory;
import com.byteslounge.cdi.resolver.bean.LocaleResolverBean;
import com.byteslounge.cdi.resolver.bean.PropertyResolverBean;
import com.byteslounge.cdi.resolver.bean.ResolverGateway;

/**
 * The CDI Properties extension.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class PropertyExtension implements Extension {

    private final ResolverGateway resolverGateway = new ResolverGateway(new PropertyResolverBean(),
            new LocaleResolverBean());

    /**
     * Passes every found CDI managed bean to the resolver gateway 
     * in order to check if it represents an extension's resolver method.
     * 
     * @param pat
     *            The CDI managed type being scanned
     */
    void processAnnotatedType(@Observes ProcessAnnotatedType<?> pat) {
        resolverGateway.process(pat.getAnnotatedType());
    }

    /**
     * Initializes the extension's resolver beans
     * 
     * @param adv
     *            The after deployment validation metadata
     * @param beanManager
     *            The CDI bean manager
     */
    void afterDeploymentValidation(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        PropertyResolverBean propertyResolverBean = resolverGateway.getResolver(PropertyResolverBean.class);
        LocaleResolverBean localeResolverBean = resolverGateway.getResolver(LocaleResolverBean.class);
        propertyResolverBean.setLocaleResolverBean(localeResolverBean);
        resolverGateway.intializeResolvers(beanManager);
        ExtensionConfiguration.INSTANCE.init();
    }

    /**
     * Processes every available CDI injection target and wires it up with the necessary
     * property resolution processing
     * 
     * @param pit
     *            The injection target being configured
     * @param beanManager
     *            The CDI bean manager
     */
    <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> pit, BeanManager beanManager) {
        InjectionTarget<T> it = pit.getInjectionTarget();
        AnnotatedType<T> at = pit.getAnnotatedType();
        PropertyFormat propertyFormat = PropertyFormatFactory.getInstance();
        pit.setInjectionTarget(new PropertyResolverInjectionTarget<T>(it, at, resolverGateway
                .getResolver(PropertyResolverBean.class), propertyFormat));
    }

}
