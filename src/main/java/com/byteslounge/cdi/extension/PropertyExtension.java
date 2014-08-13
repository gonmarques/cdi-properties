package com.byteslounge.cdi.extension;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.annotation.Property;
import com.byteslounge.cdi.annotation.PropertyResolver;
import com.byteslounge.cdi.configuration.ExtensionConfiguration;
import com.byteslounge.cdi.exception.ExtensionInitializationException;
import com.byteslounge.cdi.resolver.DefaultPropertyResolver;
import com.byteslounge.cdi.resolver.PropertyResolverInjectionTarget;
import com.byteslounge.cdi.resolver.bean.PropertyResolverBean;
import com.byteslounge.cdi.utils.MessageUtils;

/**
 * The CDI Properties extension.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class PropertyExtension implements Extension {

    private static final Logger logger = LoggerFactory.getLogger(PropertyExtension.class);
    private PropertyResolverBean propertyResolverBean;
    private AnnotatedMethod<?> resolverMethod = null;
    private AnnotatedMethod<?> providedResolverMethod = null;

    /**
     * Scans any CDI managed bean for a property resolver method.
     * 
     * @param pat
     *            The CDI managed type being scanned
     */
    void processAnnotatedType(@Observes ProcessAnnotatedType<?> pat) {

        AnnotatedType<?> at = pat.getAnnotatedType();

        for (AnnotatedMethod<?> method : at.getMethods()) {
            if (method.isAnnotationPresent(PropertyResolver.class)) {
                if (method.getJavaMember().getDeclaringClass().equals(DefaultPropertyResolver.class)) {
                    resolverMethod = method;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found default resolver method: " + MessageUtils.getMethodDefinition(method));
                    }
                } else {
                    if (providedResolverMethod != null) {
                        String errorMessage = "Found multiple provided property resolver methods: " + MessageUtils.getMethodDefinition(providedResolverMethod)
                                + ", " + MessageUtils.getMethodDefinition(method);
                        logger.error(errorMessage);
                        throw new ExtensionInitializationException(errorMessage);
                    }
                    providedResolverMethod = method;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found provided resolver method: " + MessageUtils.getMethodDefinition(providedResolverMethod));
                    }
                }
            }
        }
    }

    /**
     * Initializes the property resolver bean
     * 
     * @param adv
     *            The after deployment validation metadata
     * @param beanManager
     *            The CDI bean manager
     */
    void afterDeploymentValidation(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        propertyResolverBean.initializePropertyResolverBean();
        ExtensionConfiguration.INSTANCE.init();
    }

    /**
     * Processes every available CDI injection target and prepares property
     * injection if any target field is annotated with {@link Property}
     * 
     * @param pit
     *            The injection target being configured
     * @param beanManager
     *            The CDI bean manager
     */
    <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> pit, BeanManager beanManager) {
        if (propertyResolverBean == null) {
            initializePropertyResolverBean(beanManager);
        }
        InjectionTarget<T> it = pit.getInjectionTarget();
        AnnotatedType<T> at = pit.getAnnotatedType();
        pit.setInjectionTarget(new PropertyResolverInjectionTarget<T>(it, at, propertyResolverBean));
    }

    /**
     * Initializes the property resolver bean
     * 
     * @param beanManager
     *            The CDI bean manager
     */
    private void initializePropertyResolverBean(BeanManager beanManager) {
        if (providedResolverMethod != null) {
            resolverMethod = providedResolverMethod;
        }
        if (resolverMethod == null) {
            String errorMessage = "Could not find any property resolver method.";
            logger.error(errorMessage);
            throw new ExtensionInitializationException(errorMessage);
        }
        propertyResolverBean = new PropertyResolverBean(resolverMethod, beanManager);
        if (logger.isDebugEnabled()) {
            logger.info("Configured resolver method: " + MessageUtils.getMethodDefinition(resolverMethod));
        }
    }

}
