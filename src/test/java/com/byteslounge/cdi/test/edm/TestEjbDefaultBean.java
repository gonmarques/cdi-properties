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
package com.byteslounge.cdi.test.edm;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;

import com.byteslounge.cdi.test.common.InjectedBean;
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
public class TestEjbDefaultBean {

    @Inject
    private InjectedBean injectedBean;

    @EJB(lookup = "java:global/cdiproperties/cdipropertiesejb/ServiceEjbDefaultMethodBean")
    private ServiceEjbDefaultMethod serviceEjbDefaultMethod;

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        Assert.assertEquals(injectedBean.getText(), TestConstants.BEAN_TEST_RETURN_VALUE);
    }

    public String getHelloWorld() {
        return serviceEjbDefaultMethod.getHelloWorld();
    }

    public String getSystemBox() {
        return serviceEjbDefaultMethod.getSystemBox();
    }

    public String getOther() {
        return serviceEjbDefaultMethod.getOther();
    }

    public String getOtherAbc() {
        return serviceEjbDefaultMethod.getOtherAbc();
    }

}
