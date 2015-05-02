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
package com.byteslounge.cdi.test.it;

import java.io.IOException;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.byteslounge.cdi.test.common.ApplicationScopedBean;
import com.byteslounge.cdi.test.common.DependentScopedBean;
import com.byteslounge.cdi.test.common.InjectedBean;
import com.byteslounge.cdi.test.common.TestBean;
import com.byteslounge.cdi.test.it.common.IntegrationTestDeploymentUtils.DeploymentAppenderFactory;
import com.byteslounge.cdi.test.model.TestEntity;
import com.byteslounge.cdi.test.wpm.ProvidedPropertyMethodResolver;
import com.byteslounge.cdi.test.wpm.RequestScopedBean;
import com.byteslounge.cdi.test.wpm.Service;
import com.byteslounge.cdi.test.wpm.ServiceBean;
import com.byteslounge.cdi.test.wpm.SessionScopedBean;

/**
 * Integration Test
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class CommonWarProvidedPropertyMethod {

    public static WebArchive createArchive() throws IOException {

        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "cdipropertiestest.war").addClasses(TestBean.class,
                InjectedBean.class, ApplicationScopedBean.class, DependentScopedBean.class,
                ProvidedPropertyMethodResolver.class, RequestScopedBean.class, SessionScopedBean.class, Service.class,
                ServiceBean.class, TestEntity.class);
        DeploymentAppenderFactory
                .create(webArchive)
                .appendWebXml("../cdi-properties-test-common/src/test/resources/assets/warCommon/WEB-INF/web.xml")
                .appendWebResource(
                        "../cdi-properties-test-common/src/test/resources/assets/warCommon/webapp/cditest.xhtml",
                        "../cdi-properties-test-common/src/test/resources/assets/warCommon/webapp/cditestpt.xhtml")
                .appendBeansXml()
                .appendPersistenceXml()
                .appendFacesConfig().appendCDIPropertiesLib().appendLogging().appendProperties()
                .appendOtherProperties();

        return webArchive;
    }
}
