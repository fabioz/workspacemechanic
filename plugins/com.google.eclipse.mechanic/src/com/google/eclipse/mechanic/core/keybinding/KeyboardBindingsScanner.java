/*******************************************************************************
 * Copyright (C) 2010, Google Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.google.eclipse.mechanic.core.keybinding;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

import com.google.eclipse.mechanic.ListCollector;
import com.google.eclipse.mechanic.ResourceTaskScanner;
import com.google.eclipse.mechanic.IResourceTaskProvider;
import com.google.eclipse.mechanic.IResourceTaskReference;
import com.google.eclipse.mechanic.TaskCollector;

/**
 * Scanner for keyboard bindings.
 *
 * @author zorzella@google.com (Luiz-Otavio Zorzella)
 */
public class KeyboardBindingsScanner extends ResourceTaskScanner {

  private static final Logger LOG = Logger.getLogger(
      KeyboardBindingsScanner.class.getName());

  @Override
  public void scan(IResourceTaskProvider source, TaskCollector collector) {
    /**
     * Scan our source. Add a new Task for each KBD found.
     */
    ListCollector<IResourceTaskReference> taskCollector = ListCollector.create();
    source.collectTaskReferences(".kbd", taskCollector);
    for (IResourceTaskReference taskRef : taskCollector.get()) {
      LOG.fine(String.format("Loading keyboard file: %s", taskRef));

      // will throw a RuntimeException in the event of a problem reading
      // the kbd file
      Reader reader;
      try {
        reader = toReader(taskRef.newInputStream());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      KeyBindingsModel taskData = KeyBindingsParser.deSerialize(reader);
      collector.collect(new KeyboardBindingsTask(taskData, taskRef));
    }
  }

  private Reader toReader(InputStream is)  {
    BufferedInputStream bis = new BufferedInputStream(is);
    InputStreamReader reader = new InputStreamReader(bis);
    return new BufferedReader(reader);
  }
}
