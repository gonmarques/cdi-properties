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

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;

import com.byteslounge.cdi.annotation.Property;
import com.byteslounge.cdi.test.configuration.TestConstants;

/**
 * Used in CDI Properties integration tests. See WarDefaultMethodIT.java,
 * WarProvidedMethodIT.java, EjbDefaultMethodIT.java and
 * EjbProvidedMethodIT.java.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@Named
@RequestScoped
public class TestBean {

    @Property("hello.world")
    private String helloWorld;

    @Property(value = "system.linux.box", parameters = { "Linux", "16" })
    private String systemBox;

    @Property(value = "other.message", resourceBundleBaseName = TestConstants.OTHER_RESOURCE_BUNDLE_NAME)
    private String other;

    @Property(value = "other.parameter", parameters = { "B" }, resourceBundleBaseName = TestConstants.OTHER_RESOURCE_BUNDLE_NAME)
    private String otherAbc;

    @Inject
    private InjectedBean injectedBean;

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        Assert.assertEquals(injectedBean.getText(), TestConstants.BEAN_TEST_RETURN_VALUE);
    }

    public String getHelloWorld() {
        return helloWorld;
    }

    public String getSystemBox() {
        return systemBox;
    }

    public String getOther() {
        return other;
    }

    public String getOtherAbc() {
        return otherAbc;
    }

}