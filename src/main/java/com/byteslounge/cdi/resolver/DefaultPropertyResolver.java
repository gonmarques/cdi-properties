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

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.context.spi.CreationalContext;

import com.byteslounge.cdi.resolver.bean.PropertyResolverBean;

/**
 * The default property resolver.
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class DefaultPropertyResolver implements PropertyResolver {

    private static final long serialVersionUID = 1L;
    private final PropertyResolverBean propertyResolverBean;

    public DefaultPropertyResolver(PropertyResolverBean propertyResolverBean) {
        this.propertyResolverBean = propertyResolverBean;
    }

    /**
     * See {@link PropertyResolver#resolve(String, String, CreationalContext)}
     */
    @Override
    public String resolve(String key, String bundleName, CreationalContext<?> ctx) {
        try {
            return propertyResolverBean.resolveProperty(key, bundleName, ctx);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Could not resolve property", e);
        }
    }

}
