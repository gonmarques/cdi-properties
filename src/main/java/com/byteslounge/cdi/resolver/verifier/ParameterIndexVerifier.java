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
