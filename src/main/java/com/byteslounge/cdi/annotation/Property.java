package com.byteslounge.cdi.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to decorate any CDI managed bean field which should
 * be populated with an existing resource bundle property.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface Property {

    String value();

    String resourceBundleBaseName() default "";

    String[] parameters() default {};

}
