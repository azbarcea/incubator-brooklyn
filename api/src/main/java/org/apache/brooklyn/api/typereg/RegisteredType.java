/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.brooklyn.api.typereg;

import java.util.Collection;

import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.api.objs.Identifiable;

public interface RegisteredType extends Identifiable {
    
    @Override
    String getId();
    
    String getSymbolicName();
    String getVersion();

    Collection<OsgiBundleWithUrl> getLibraries();

    String getDisplayName();
    String getDescription();
    String getIconUrl();

    /** @return the java type or a supertype thereof that this registered type represents.
     * <p>
     * For beans, this is the type that the {@link BrooklynTypeRegistry} will create. 
     * For specs, this is what the spec that will be created points at 
     * (e.g. the concrete {@link Entity}, not the {@link EntitySpec});
     * <p>
     * In some cases this may return an interface or a super-type of what will actually be created, 
     * such as if the concrete type is private and callers should know only about a particular public interface,
     * or if precise type details are unavailable and all that is known at creation is some higher level interface/supertype
     * (e.g. this may return {@link Entity} even though the spec points at a specific subclass,
     * for instance because the YAML has not yet been parsed or OSGi bundles downloaded). 
     */
    Class<?> getJavaType();

    /**
     * @return True if the item has been deprecated (i.e. its use is discouraged)
     */
    boolean isDeprecated();
    
    /**
     * @return True if the item has been disabled (i.e. its use is forbidden, except for pre-existing apps)
     */
    boolean isDisabled();
    
}
