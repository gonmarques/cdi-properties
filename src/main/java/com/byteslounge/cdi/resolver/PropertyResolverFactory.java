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

import com.byteslounge.cdi.resolver.bean.PropertyResolverBean;

/**
 * Property resolver factory.
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class PropertyResolverFactory {

    private PropertyResolverFactory() {
    }

    /**
     * Creates the default property resolver.
     * 
     * @param propertyResolverBean
     *            Bean that holds a reference to the actual property resolver
     *            instance
     * @return The default property resolver
     */
    public static PropertyResolver getInstance(PropertyResolverBean propertyResolverBean) {
        return new DefaultPropertyResolver(propertyResolverBean);
    }

}
