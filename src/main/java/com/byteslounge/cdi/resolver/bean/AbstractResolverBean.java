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
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.exception.ExtensionInitializationException;
import com.byteslounge.cdi.extension.param.InjectableResolverParameter;
import com.byteslounge.cdi.extension.param.ResolverParameter;
import com.byteslounge.cdi.resolver.context.ResolverContext;
import com.byteslounge.cdi.resolver.extractor.BundleResolverParameterExtractor;
import com.byteslounge.cdi.resolver.extractor.KeyResolverParameterExtractor;
import com.byteslounge.cdi.resolver.extractor.LocaleResolverParameterExtractor;
import com.byteslounge.cdi.resolver.extractor.ResolverParameterExtractor;
import com.byteslounge.cdi.utils.MessageUtils;

/**
 * Represents the base class used to wrap an extension's resolver method.
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public abstract class AbstractResolverBean<T> implements ResolverBean<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractResolverBean.class);
    private final Class<? extends Annotation> typeToSearch;
    private final Class<?> defaulResolverClass;
    private final String resolverDescription;
    private AnnotatedMethod<?> providedResolverMethod = null;
    private List<ResolverParameter<?>> resolverParameters;
    private ResolverBean<Locale> localeResolverBean;
    protected Object resolverInstance;
    protected AnnotatedMethod<?> resolverMethod = null;

    public AbstractResolverBean(Class<? extends Annotation> typeToSearch, String resolverDescription,
            Class<?> defaulResolverClass) {
        this.typeToSearch = typeToSearch;
        this.resolverDescription = resolverDescription;
        this.defaulResolverClass = defaulResolverClass;
    }

    /**
     * Validates an extension's resolver method for correctness 
     * in what matters to the method configuration.
     * 
     * @param resolverMethod The resolver method being verified
     */
    abstract void validate(AnnotatedMethod<?> resolverMethod);

    /**
     * @see {@link com.byteslounge.cdi.resolver.bean.ResolverBean#process(AnnotatedType)}
     */
    @Override
    public void process(AnnotatedType<?> at) {
        for (AnnotatedMethod<?> method : at.getMethods()) {
            if (method.isAnnotationPresent(typeToSearch)) {
                if (method.getJavaMember().getDeclaringClass().equals(defaulResolverClass)) {
                    resolverMethod = method;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found default " + resolverDescription + "resolver method: "
                                + MessageUtils.getMethodDefinition(method));
                    }
                } else {
                    if (providedResolverMethod != null) {
                        String errorMessage = "Found multiple provided " + resolverDescription + " resolver methods: "
                                + MessageUtils.getMethodDefinition(providedResolverMethod) + ", "
                                + MessageUtils.getMethodDefinition(method);
                        logger.error(errorMessage);
                        throw new ExtensionInitializationException(errorMessage);
                    }
                    providedResolverMethod = method;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found provided " + resolverDescription + "resolver method: "
                                + MessageUtils.getMethodDefinition(providedResolverMethod));
                    }
                }
            }
        }
    }

    /**
     * @see {@link com.byteslounge.cdi.resolver.bean.ResolverBean#initialize(BeanManager)}
     */
    @Override
    public void initialize(final BeanManager beanManager) {
        validate();
        Class<?> resolverMethodClass = resolverMethod.getJavaMember().getDeclaringClass();
        Set<Bean<?>> beans = beanManager.getBeans(resolverMethodClass);
        final Bean<?> resolverBean = beanManager.resolve(beans);
        if (resolverBean == null) {
            throw new ExtensionInitializationException(
                    "Could not resolve bean for class: "
                            + resolverMethodClass.getName()
                            + ". The class is probably deployed in a module that is not accessible by the CDI Properties extension classloader."
                            + " Try to deploy the resolver class in a library JAR instead.");
        }
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(resolverBean);
        resolverInstance = beanManager.getReference(resolverBean, resolverMethodClass,
                creationalContext);
        resolverParameters = new ArrayList<>();
        List<ResolverParameterExtractor<? extends ResolverParameter<?>>> extractors = Arrays
                .asList(new ResolverParameterExtractor<?>[] { new KeyResolverParameterExtractor(),
                        new LocaleResolverParameterExtractor(localeResolverBean),
                        new BundleResolverParameterExtractor(),
                        new ResolverParameterExtractor<InjectableResolverParameter>() {
                            @Override
                            public InjectableResolverParameter extract(AnnotatedParameter<?> parameter) {
                                return new InjectableResolverParameter(parameter, beanManager,
                                        resolverBean);
                            }
                        } });
        for (final AnnotatedParameter<?> parameter : resolverMethod.getParameters()) {
            for (ResolverParameterExtractor<? extends ResolverParameter<?>> extractor : extractors) {
                ResolverParameter<?> resolverParameter = extractor.extract(parameter);
                if (resolverParameter != null) {
                    resolverParameters.add(resolverParameter);
                    break;
                }
            }
        }
        logger.info("Configured " + resolverDescription + " resolver method: "
                + MessageUtils.getMethodDefinition(resolverMethod));
    }

    /**
     * @see {@link com.byteslounge.cdi.resolver.bean.ResolverBean#invoke(ResolverContext, CreationalContext)}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T invoke(ResolverContext resolverContext, CreationalContext<?> ctx) {
        List<Object> parameters = new ArrayList<>();
        if (logger.isDebugEnabled()) {
            logger.debug("About to resolve parameters of " + resolverDescription + " resolver method");
        }
        for (ResolverParameter<?> parameter : resolverParameters) {
            parameters.add(parameter.resolve(resolverContext, ctx));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Parameters resolved. Invoking " + resolverDescription + " resolver method.");
        }
        try {
            return (T) resolverMethod.getJavaMember().invoke(resolverInstance, parameters.toArray());
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Could not invoke resolver", e);
        }
    }

    /**
     * Validates if the extension's resolver method represented by this bean was properly found
     * 
     */
    private void validate() {
        if (providedResolverMethod != null) {
            resolverMethod = providedResolverMethod;
        }
        if (resolverMethod == null) {
            String errorMessage = "Could not find any " + resolverDescription + " resolver method.";
            logger.error(errorMessage);
            throw new ExtensionInitializationException(errorMessage);
        }
        validate(resolverMethod);
    }

    /**
     * Sets a Locale resolver bean to be used as a helper for the current resolver bean, if required
     * 
     * @param localeResolverBean the Locale resolver bean
     */
    public void setLocaleResolverBean(ResolverBean<Locale> localeResolverBean) {
        this.localeResolverBean = localeResolverBean;
    }

}
