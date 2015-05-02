/*
 * Copyright 2015 byteslounge.com (Gonçalo Marques).
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
package com.byteslounge.cdi.test.wpm;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;

import com.byteslounge.cdi.annotation.LocaleResolver;
import com.byteslounge.cdi.test.common.session.UserSession;

@ApplicationScoped
public class ProvidedLocaleMethodResolverSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @LocaleResolver
    public Locale resolveLocale(UserSession userSession) {
        return userSession.getLocale();
    }

}
