package com.byteslounge.cdi.utils;

import java.text.MessageFormat;

import javax.enterprise.inject.spi.AnnotatedMethod;

/**
 * Utility class related with messages presented by the extension.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class MessageUtils {

    private MessageUtils() {
    }

    /**
     * Builds a textual representation of an {@link AnnotatedMethod}
     * 
     * @param method
     *            The {@link AnnotatedMethod} instance
     * @return A textual representation of the {@link AnnotatedMethod}
     */
    public static String getMethodDefinition(AnnotatedMethod<?> method) {
        return method.getJavaMember().getDeclaringClass().getName() + "." + method.getJavaMember().getName() + "()";
    }

    /**
     * Formats a message with parameters
     * 
     * @param message
     *            The message being formatted
     * @param params
     *            The parameters to be used in message formatting
     * @return The formatted message
     */
    public static String formatMessage(String message, Object... params) {
        return MessageFormat.format(message, params);
    }

}
