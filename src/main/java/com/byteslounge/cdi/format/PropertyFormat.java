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
package com.byteslounge.cdi.format;

/**
 * Formats resolved properties.
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public interface PropertyFormat {

    /**
     * Formats a message with parameters
     * 
     * @param message
     *            The message being formatted
     * @param params
     *            The parameters to be used in message formatting
     * @return The formatted message
     */
    String formatMessage(String message, Object... params);

}
