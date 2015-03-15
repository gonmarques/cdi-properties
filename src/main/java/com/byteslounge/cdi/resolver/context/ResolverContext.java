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
package com.byteslounge.cdi.resolver.context;

/**
 * Represents the resolver context which is used during property resolution.
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class ResolverContext {

    private final String key;
    private final String bundleName;

    private ResolverContext(String key, String bundleName) {
        this.key = key;
        this.bundleName = bundleName;
    }

    /**
     * Creates a {@link com.byteslounge.cdi.resolver.context.ResolverContext} instance.
     * 
     * @param key the property key to be associated with the created context
     * @param bundleName the resource bundle name to be associated with the created context
     * 
     * @return the newly created {@link com.byteslounge.cdi.resolver.context.ResolverContext} instance
     */
    public static ResolverContext create(String key, String bundleName) {
        return new ResolverContext(key, bundleName);
    }

    /**
     * Gets the key associated with this {@link com.byteslounge.cdi.resolver.context.ResolverContext} instance
     * 
     * @return the key associated with this {@link com.byteslounge.cdi.resolver.context.ResolverContext} instance
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the resource bundle name associated with this {@link com.byteslounge.cdi.resolver.context.ResolverContext} instance
     * 
     * @return the resource bundle name associated with this {@link com.byteslounge.cdi.resolver.context.ResolverContext} instance
     */
    public String getBundleName() {
        return bundleName;
    }

}
