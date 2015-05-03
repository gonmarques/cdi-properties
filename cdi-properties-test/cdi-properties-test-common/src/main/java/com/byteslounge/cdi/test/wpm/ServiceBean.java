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
package com.byteslounge.cdi.test.wpm;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;

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
public class ServiceBean implements Service {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private InjectedBean injectedBean;

    @Override
    public String getText() {
        Assert.assertEquals(injectedBean.getText(), TestConstants.BEAN_TEST_RETURN_VALUE);
        return TestConstants.BEAN_TEST_RETURN_VALUE;
    }

    @Override
    public TestEntity merge(TestEntity testEntity) {
        return entityManager.merge(testEntity);
    }

    @Override
    public TestEntity findById(Long id) {
        return entityManager.find(TestEntity.class, id);
    }

    @Override
    public void remove(Long id) {
        TestEntity testEntity = entityManager.find(TestEntity.class, id);
        if (testEntity != null) {
            entityManager.remove(testEntity);
        }
    }

}
