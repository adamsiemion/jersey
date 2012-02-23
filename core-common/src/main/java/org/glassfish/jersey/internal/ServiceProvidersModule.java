/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.internal;

import org.glassfish.jersey.internal.inject.AbstractModule;
import org.glassfish.jersey.internal.inject.ReferencingFactory;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.process.internal.RequestScope;

import org.glassfish.hk2.Factory;
import org.glassfish.hk2.Scope;
import org.glassfish.hk2.TypeLiteral;
import org.glassfish.hk2.scopes.Singleton;

import org.jvnet.hk2.annotations.Inject;

/**
 * HK2 module that defines injection bindings for {@link ServiceProviders} contract
 * as well as {@code ServiceProviders} contract {@link Ref reference}. The service
 * providers contract reference ({@code Ref&lt;ServiceProviders&gt;}) can be used
 * to update the service providers instance that is being injected by HK2.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class ServiceProvidersModule extends AbstractModule {

    private final Class<? extends Scope> providersBuilderScope;
    private final Class<? extends Scope> providersRefScope;

    /**
     * Construct service providers module that binds service providers builder in the
     * {@link Singleton} scope.
     */
    public ServiceProvidersModule() {
        this(Singleton.class, Singleton.class);
    }

    /**
     * Construct service providers module.
     *
     * @param providersBuilderScope binding scope of the {@link ServiceProviders.Builder
     *     service providers builder}.
     * @param providersRefScope binding scope of the {@link ServiceProviders service
     *     providers} {@link Ref reference}.
     */
    public ServiceProvidersModule(Class<? extends Scope> providersBuilderScope, Class<? extends Scope> providersRefScope) {
        this.providersBuilderScope = providersBuilderScope;
        this.providersRefScope = providersRefScope;
    }

    private static final class ServiceProvidersReferencingFactory extends ReferencingFactory<ServiceProviders> {

        public ServiceProvidersReferencingFactory(@Inject Factory<Ref<ServiceProviders>> referenceFactory) {
            super(referenceFactory);
        }
    }

    @Override
    protected void configure() {
        bind().to(ServiceProviders.Builder.class).in(providersBuilderScope);

        bind(ServiceProviders.class)
                .toFactory(ServiceProvidersReferencingFactory.class)
                .in(RequestScope.class);
        bind(new TypeLiteral<Ref<ServiceProviders>>() {})
                .toFactory(ReferencingFactory.<ServiceProviders>referenceFactory())
                .in(providersRefScope);
    }
}