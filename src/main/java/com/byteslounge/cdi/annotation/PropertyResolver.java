package com.byteslounge.cdi.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to define the method which responsibility is to
 * resolve resource bundle keys into concrete values.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface PropertyResolver {

}
