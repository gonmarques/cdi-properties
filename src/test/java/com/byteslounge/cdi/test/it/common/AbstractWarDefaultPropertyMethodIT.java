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
package com.byteslounge.cdi.test.it.common;

import java.io.IOException;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.byteslounge.cdi.test.it.common.IntegrationTestDeploymentUtils.DeploymentClassAppenderFactory;

/**
 * Base class for Integration Tests which use the default property resolver method
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public abstract class AbstractWarDefaultPropertyMethodIT {

    protected static WebArchive createArchive() throws IOException {

        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "cdipropertiestest.war");
        DeploymentClassAppenderFactory.create(webArchive).append("com.byteslounge.cdi.test.common.TestBean")
                .append("com.byteslounge.cdi.test.common.InjectedBean")
                .appendWebXml("src/test/resources/assets/warCommon/WEB-INF/web.xml")
                .appendWebResource("src/test/resources/assets/warCommon/webapp/cditest.xhtml",
                        "src/test/resources/assets/warCommon/webapp/cditestpt.xhtml").appendFacesConfig()
                .appendBeansXml()
                .appendCDIPropertiesLib().appendLogging().appendProperties().appendOtherProperties();

        return webArchive;
    }

}
