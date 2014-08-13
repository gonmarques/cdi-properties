package com.byteslounge.cdi.resolver.verifier;

/**
 * A resolver method verifier which checks for properly property resolver method
 * configuration
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public interface ResolverMethodVerifier {

    /**
     * Verifies if the property resolver method is properly configured
     */
    void verify();

}
