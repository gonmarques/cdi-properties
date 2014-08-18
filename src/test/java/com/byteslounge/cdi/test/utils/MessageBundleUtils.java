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
package com.byteslounge.cdi.test.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Used in CDI Properties integration tests. See WarDefaultMethodIT.java,
 * WarProvidedMethodIT.java, EjbDefaultMethodIT.java and
 * EjbProvidedMethodIT.java.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class MessageBundleUtils {

    private MessageBundleUtils() {
    }

    public static String resolveProperty(String key, String bundleName, Locale locale, Object... args) {
        String value = ResourceBundle.getBundle(bundleName, locale).getString(key);
        if (args != null && args.length > 0) {
            value = MessageFormat.format(value, args);
        }
        return value;
    }

}
