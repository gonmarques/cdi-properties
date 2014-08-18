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
package com.byteslounge.cdi.test.common;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.junit.Assert;

import com.byteslounge.cdi.test.configuration.TestConstants;

/**
 * Used in CDI Properties integration tests. See WarDefaultMethodIT.java,
 * WarProvidedMethodIT.java, EjbDefaultMethodIT.java and
 * EjbProvidedMethodIT.java.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@ApplicationScoped
public class ApplicationScopedBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private InjectedBean injectedBean;

    public String getText() {
        Assert.assertEquals(injectedBean.getText(), TestConstants.BEAN_TEST_RETURN_VALUE);
        return TestConstants.BEAN_TEST_RETURN_VALUE;
    }
}
