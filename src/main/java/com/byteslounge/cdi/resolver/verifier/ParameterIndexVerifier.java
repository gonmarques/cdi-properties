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
package com.byteslounge.cdi.resolver.verifier;

import com.byteslounge.cdi.exception.ExtensionInitializationException;

/**
 * Checks if the reserved property resolver method parameters order is properly
 * configured
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class ParameterIndexVerifier implements ResolverMethodVerifier {

    private final int localeParameterIndex;
    private final int bundleNameParameterIndex;

    public ParameterIndexVerifier(int localeParameterIndex, int bundleNameParameterIndex) {
        this.localeParameterIndex = localeParameterIndex;
        this.bundleNameParameterIndex = bundleNameParameterIndex;
    }

    /**
     * See {@link ResolverMethodVerifier#verify()}
     */
    @Override
    public void verify() {
        if (localeParameterIndex > -1 && localeParameterIndex != 0) {
            throw new ExtensionInitializationException("Locale parameter, if present in property resolver method, must be the first one.");
        }
        if (bundleNameParameterIndex > -1 && localeParameterIndex == -1 && bundleNameParameterIndex != 0) {
            throw new ExtensionInitializationException(
                    "BundleName parameter, if present in property resolver method without Locale parameter, must be the first one.");
        }
        if (localeParameterIndex > -1 && bundleNameParameterIndex > -1 && (bundleNameParameterIndex - localeParameterIndex != 1)) {
            throw new ExtensionInitializationException("BundleName parameter must be right after Locale parameter in property resolver method.");
        }
    }

}
