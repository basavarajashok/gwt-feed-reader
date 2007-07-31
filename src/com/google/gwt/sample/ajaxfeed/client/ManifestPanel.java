/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.sample.ajaxfeed.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;

import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO.
 * 
 */
public class ManifestPanel extends SliderPanel {
  private final Configuration configuration;
  private final HashMap/* <Feed, FeedPanel> */feedPanelMap = new HashMap();
  
  private boolean dirty = true;

  public ManifestPanel(final Configuration configuration) {
    super("Feeds", null);
    this.configuration = configuration;

    setEditCommand("Edit", "Edits feeds", new Command() {
      public void execute() {
        History.newItem("configuration");
      }
    });

    addStyleName("ManifestPanel");
    setStatus("Loading feeds...");
    refresh();
  }

  public void enter() {
    if (dirty) {
      refresh();
    }
    
    super.enter();
    History.newItem("manifest");
  }

  public PanelLabel getLabel() {
    throw new RuntimeException("This should not be called on the root panel");
  }

  public void showFeed(Configuration.Feed feed) {
    FeedPanel p = (FeedPanel) feedPanelMap.get(feed);
    if (p == null) {
      // We don't want to take the hit of reloading everything, so defer this
      // until the panel is re-shown.
      setDirty();
      p = new FeedPanel(feed, this);
      feedPanelMap.put(feed, p);
    }
    p.enter();
  }

  protected String getShortTitle() {
    return "Feeds";
  }

  void setDirty() {
    dirty = true;
  }
  
  void showConfiguration() {
    (new ConfigurationPanel(configuration, this)).enter();
  }
  
  private void refresh() {
    dirty = false;
    clear();
    feedPanelMap.clear();

    for (Iterator i = configuration.getFeeds().iterator(); i.hasNext();) {
      Configuration.Feed feed = (Configuration.Feed) i.next();

      FeedPanel panel = new FeedPanel(feed, this);
      feedPanelMap.put(feed, panel);
      add(panel.getLabel());
    }
    setStatus("Ready");
  }
}
