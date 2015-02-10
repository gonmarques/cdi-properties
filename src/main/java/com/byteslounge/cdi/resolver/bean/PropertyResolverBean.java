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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.extension.param.InjectablePropertyResolverParameter;
import com.byteslounge.cdi.extension.param.ResolverParameter;
import com.byteslounge.cdi.resolver.extractor.BundleResolverParameterExtractor;
import com.byteslounge.cdi.resolver.extractor.KeyResolverParameterExtractor;
import com.byteslounge.cdi.resolver.extractor.LocaleResolverParameterExtractor;
import com.byteslounge.cdi.resolver.extractor.ResolverParameterExtractor;
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
    private List<ResolverParameter> propertyResolverParameters;
    private static final Logger logger = LoggerFactory.getLogger(PropertyResolverBean.class);

    public PropertyResolverBean(AnnotatedMethod<?> propertyResolverMethod, BeanManager beanManager) {
        this.propertyResolverMethod = propertyResolverMethod;
        this.beanManager = beanManager;
        ValidationUtils.validateResolverMethod(Arrays.asList(new ResolverMethodParametersVerifier(
                propertyResolverMethod)));
    }

    /**
     * Initializes the property resolver bean instance and resolver method,
     * along with all method parameters.
     */
    public void initializePropertyResolverBean() {
        Class<?> resolverMethodClass = propertyResolverMethod.getJavaMember().getDeclaringClass();
        Set<Bean<?>> beans = beanManager.getBeans(resolverMethodClass);
        final Bean<?> propertyResolverBean = beanManager.resolve(beans);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(propertyResolverBean);
        propertyResolverInstance = beanManager.getReference(propertyResolverBean, resolverMethodClass, creationalContext);
        propertyResolverParameters = new ArrayList<>();
        List<ResolverParameterExtractor<? extends ResolverParameter>> extractors = Arrays
                .asList(new ResolverParameterExtractor<?>[] {
                new KeyResolverParameterExtractor(), new LocaleResolverParameterExtractor(),
                new BundleResolverParameterExtractor(), new ResolverParameterExtractor<InjectablePropertyResolverParameter>() {
                    @Override
                    public InjectablePropertyResolverParameter extract(AnnotatedParameter<?> parameter) {
                        return new InjectablePropertyResolverParameter(parameter, beanManager, propertyResolverBean);
                    }
                } });
        for (final AnnotatedParameter<?> parameter : propertyResolverMethod.getParameters()) {
            for (ResolverParameterExtractor<? extends ResolverParameter> extractor : extractors) {
                ResolverParameter resolverParameter = extractor.extract(parameter);
                if (resolverParameter != null) {
                    propertyResolverParameters.add(resolverParameter);
                    break;
                }
            }
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
            logger.debug("About to resolve parameters of property resolver method");
        }
        for (ResolverParameter parameter : propertyResolverParameters) {
            parameters.add(parameter.resolve(key, bundleName, ctx));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Parameters resolved. Invoking resolver method.");
        }
        return (String) propertyResolverMethod.getJavaMember().invoke(propertyResolverInstance, parameters.toArray());
    }

}
