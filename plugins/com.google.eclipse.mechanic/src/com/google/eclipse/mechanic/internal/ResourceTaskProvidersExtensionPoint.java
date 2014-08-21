/*******************************************************************************
 * Copyright (C) 2014, Google Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.google.eclipse.mechanic.internal;

import java.util.List;

import org.eclipse.core.runtime.Platform;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.eclipse.mechanic.IResourceTaskProvider;
import com.google.eclipse.mechanic.plugin.core.MechanicPlugin;

/**
 * Code behind the {@code com.google.eclipse.mechanic.resourcetaskproviders} extension point.
 *
 * <p>This class interfaces with the {@link Platform}, reading all extensions of the
 * {@code tasks} extension point, providing a mechanism for translating their
 * implementations to instances of {@link IResourceTaskProvider}.
 */
public class ResourceTaskProvidersExtensionPoint {
  private static final String EXTENSION_POINT_NAME = "resourcetaskproviders";
  private static final String TAG_TASK = "provider";
  private static final String ATTR_CLASS = "class";

  // Initialization On Demand Holder Idiom
  // http://crazybob.org/2007/01/lazy-loading-singletons.html
  private static class SingletonHolder {
    static SimpleExtensionPointManager<IResourceTaskProvider> instance =
        SimpleExtensionPointManager.newInstance(
            EXTENSION_POINT_NAME,
            IResourceTaskProvider.class,
            TAG_TASK,
            ATTR_CLASS,
            null);
  }

  private ResourceTaskProvidersExtensionPoint() {
  }

  /**
   * Return the {@link IResourceTaskProvider} supplier, initializing it if required.
   *
   * <p>The supplier is memoized, so it will return the same instantiated
   * objects upon repeated calls.
   */
  public static Supplier<List<IResourceTaskProvider>> getInstance() {
    return Suppliers.memoize(new Supplier<List<IResourceTaskProvider>>() {
      public List<IResourceTaskProvider> get() {
        return SingletonHolder.instance.getInstances();
      }
    });
  }

  /**
   * Clears the list of task providers.
   * 
   * <p><em>This should only be called by {@link MechanicPlugin#stop}.</em>
   */
  public static void dispose() {
    SingletonHolder.instance = null;
  }
}
