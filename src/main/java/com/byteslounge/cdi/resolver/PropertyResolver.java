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

import java.io.Serializable;

import javax.enterprise.context.spi.CreationalContext;

/**
 * Represents a property resolver.
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public interface PropertyResolver extends Serializable {

    /**
     * Resolves a property based on the contextual property information.
     * 
     * @param key
     *            The property key to be resolved
     * @param bundleName
     *            The bundle name which contains the property to resolve
     * @param ctx
     *            The CDI Creational Context used to fetch auxiliary property
     *            resolver CDI managed beans
     * @return The resolved property
     */
    String resolve(String key, String bundleName, CreationalContext<?> ctx);

}
