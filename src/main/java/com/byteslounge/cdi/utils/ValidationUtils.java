package com.byteslounge.cdi.utils;

import java.util.Collection;

import com.byteslounge.cdi.resolver.verifier.ResolverMethodVerifier;

/**
 * Utility class related property resolver method validation.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Validates the property resolver method against a list of property
     * resolver method verifiers
     * 
     * @param verifiers
     *            The list of property resolver method verifiers
     */
    public static void validateResolverMethod(Collection<? extends ResolverMethodVerifier> verifiers) {
        for (ResolverMethodVerifier verifier : verifiers) {
            verifier.verify();
        }
    }

}
