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
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;

import com.byteslounge.cdi.annotation.Property;
import com.byteslounge.cdi.test.common.InjectedBean;
import com.byteslounge.cdi.test.configuration.TestConstants;
import com.byteslounge.cdi.test.model.TestEntity;

/**
 * Used in CDI Properties integration tests. See WarDefaultMethodIT.java,
 * WarProvidedMethodIT.java, EjbDefaultMethodIT.java and
 * EjbProvidedMethodIT.java.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@Stateless
public class ServiceEjbDefaultMethodBean implements ServiceEjbDefaultMethod {

    private static final long serialVersionUID = 1L;

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

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        Assert.assertEquals(injectedBean.getText(), TestConstants.BEAN_TEST_RETURN_VALUE);
    }

    @Override
    public String getHelloWorld() {
        TestEntity entity = entityManager.find(TestEntity.class, 1L);
        if (entity != null) {
            entityManager.remove(entity);
            entityManager.flush();
        }
        entity = new TestEntity();
        entity.setId(1L);
        entity.setDescription("Description");
        entityManager.persist(entity);
        entityManager.flush();
        return helloWorld;
    }

    @Override
    public String getSystemBox() {
        return systemBox;
    }

    @Override
    public String getOther() {
        return other;
    }

    @Override
    public String getOtherAbc() {
        return otherAbc;
    }

}
