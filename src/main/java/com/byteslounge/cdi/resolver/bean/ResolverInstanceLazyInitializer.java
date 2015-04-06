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
package com.byteslounge.cdi.resolver.bean;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteslounge.cdi.exception.ExtensionInitializationException;

/**
 * Lazy resolver bean initializer created to solve bean resolution problems 
 * in older versions of Weld (ex: Weld 1.1.3 that is shipped with Weblogic 12.1.1).
 * 
 * Basically we fall back to the Bean Manager that is fetched through JNDI in case
 * the Bean Manager provided to the extension fails to resolve the Property/Locale resolvers.
 * 
 * @author Gonçalo Marques
 * @since 1.1.1
 */
public class ResolverInstanceLazyInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ResolverInstanceLazyInitializer.class);
    private volatile ResolverInstance resolverInstance;
    private final Class<?> resolverClass;
    private final BeanManagerList beanManagers;

    public ResolverInstanceLazyInitializer(BeanManager beanManager, Class<?> resolverClass) {
        this.beanManagers = new BeanManagerList(beanManager);
        this.resolverClass = resolverClass;
    }

    public ResolverInstance get() {
        ResolverInstance result = resolverInstance;
        if (result == null) {
            synchronized (this) {
                result = resolverInstance;
                if (result == null) {
                    BeanResolverResult beanResolverResult = CDIBeanResolver.resolve(beanManagers, resolverClass);
                    BeanManager activeBeanManager = beanResolverResult.activeBeanManager;
                    Bean<?> resolverBean = activeBeanManager.resolve(beanResolverResult.beans);
                    if (resolverBean == null) {
                        throw new ExtensionInitializationException(
                                "Could not resolve bean for class: "
                                        + resolverClass.getName()
                                        + ". The class is probably deployed in a module that is not accessible by the CDI Properties extension classloader."
                                        + " Try to deploy the resolver class in a library JAR instead.");
                    }
                    CreationalContext<?> creationalContext = activeBeanManager.createCreationalContext(resolverBean);
                    resolverInstance = result = new ResolverInstance(activeBeanManager.getReference(resolverBean,
                            resolverClass, creationalContext), resolverBean, activeBeanManager);
                }
            }
        }
        return result;
    }

    public static class ResolverInstance {

        private final Object resolverInstance;
        private final Bean<?> resolverBean;
        private final BeanManager activeBeanManager;

        public ResolverInstance(Object resolverInstance, Bean<?> resolverBean,BeanManager activeBeanManager) {
            this.resolverInstance = resolverInstance;
            this.resolverBean = resolverBean;
            this.activeBeanManager = activeBeanManager;
        }

        public Object getResolverInstance() {
            return resolverInstance;
        }

        public Bean<?> getResolverBean() {
            return resolverBean;
        }

        public BeanManager getActiveBeanManager() {
            return activeBeanManager;
        }

    }

    static class CDIBeanResolver {

        static BeanResolverResult resolve(Iterable<BeanManager> beanManagers, Class<?> beanClass) {
            Set<Bean<?>> beans = Collections.emptySet();
            Iterator<BeanManager> it = beanManagers.iterator();
            BeanManager activeBeanManager = null;
            while (it.hasNext()) {
                activeBeanManager = it.next();
                beans = activeBeanManager.getBeans(beanClass);
                if (!beans.isEmpty()) {
                    break;
                }
            }
            return new BeanResolverResult(beans, activeBeanManager);
        }
    }

    static class BeanResolverResult {
        private final Set<Bean<?>> beans;
        private final BeanManager activeBeanManager;

        public BeanResolverResult(Set<Bean<?>> beans, BeanManager activeBeanManager) {
            this.beans = beans;
            this.activeBeanManager = activeBeanManager;
        }

    }

    private static class BeanManagerList implements Iterable<BeanManager> {

        private final BeanManager[] providedBeanManagers;

        BeanManagerList(BeanManager... providedBeanManagers) {
            this.providedBeanManagers = providedBeanManagers;
        }

        @Override
        public Iterator<BeanManager> iterator() {
            return new BeanManagerIterator();
        }

        private class BeanManagerIterator implements Iterator<BeanManager> {

            private static final String BEAN_MANAGER_JNDI = "java:comp/BeanManager";
            private int currentIndex = 0;
            private BeanManager jndiBeanManager;

            BeanManagerIterator() {
                try {
                    jndiBeanManager = (BeanManager) new InitialContext().lookup(BEAN_MANAGER_JNDI);
                } catch (NamingException e) {
                    logger.warn("Could not fetch Bean Manager from JNDI. Fallback strategy will not be used.", e);
                }
            }

            @Override
            public boolean hasNext() {
                return currentIndex < providedBeanManagers.length || jndiBeanManager != null;
            }

            @Override
            public BeanManager next() {
                if (currentIndex < providedBeanManagers.length) {
                    return providedBeanManagers[currentIndex++];
                }
                if (jndiBeanManager == null) {
                    throw new NoSuchElementException();
                }
                BeanManager result = jndiBeanManager;
                jndiBeanManager = null;
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not implemented");
            }

        }

    }

}
