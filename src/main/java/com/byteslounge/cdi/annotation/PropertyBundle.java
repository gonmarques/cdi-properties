package com.byteslounge.cdi.annotation;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to decorate a property resolver method parameter.
 * When the property resolver method is invoked, the parameter annotated with
 * this annotation will receive the invocation's contextual resource bundle.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ PARAMETER })
public @interface PropertyBundle {

}
