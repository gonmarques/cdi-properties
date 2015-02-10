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
package com.byteslounge.cdi.resolver.extractor;

import com.byteslounge.cdi.annotation.PropertyLocale;
import com.byteslounge.cdi.extension.param.LocalePropertyResolverParameter;

/**
 * Represents the extractor for the Locale property resolver method parameter
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class LocaleResolverParameterExtractor extends
        ProvidedResolverParameterExtractor<LocalePropertyResolverParameter> {

    public LocaleResolverParameterExtractor() {
        super(PropertyLocale.class, new LocalePropertyResolverParameter());
    }

}
