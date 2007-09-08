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
package com.google.gwt.sample.feedreader.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * The main view; a list of all of the configured feeds.
 */
public class ManifestPanel extends WallToWallPanel {
  /**
   * This class serves as a synchronization point for downloading the contents
   * of a feed. If it contains any elements and there is no currently-loading
   * feed, the first feed in the list will be loaded every 100ms.
   */
  private static class LoaderList extends ArrayList {
    final Timer t = new Timer() {
      public void run() {
        if (loadingFeed != null && !loadingFeed.isLoadFinished()) {
          return;
        }

        if (size() == 0) {
          return;
        }

        FeedPanel p = (FeedPanel) get(0);
        if (p.isLoadFinished()) {
          remove(0);
        } else if (!p.isLoadStarted()) {
          loadingFeed = p;
          p.loadFeed();
        }
      }
    };

    public LoaderList() {
      t.scheduleRepeating(100);
    }

    public boolean add(Object o) {
      if (contains(o)) {
        return false;
      }
      return super.add(o);
    }
  }

  /**
   * The list of feeds that need to be loaded.
   */
  private static final List toLoad = new LoaderList();

  /**
   * Used to prevent more than one FeedPanel from being automatically loaded at
   * a time.
   */
  private static FeedPanel loadingFeed;

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
    
    // Start a timer to refresh the feed data.
    (new Timer() {
      public void run() {
        refresh();
      }
    }).scheduleRepeating(30 * 60 * 1000);

    addStyleName("ManifestPanel");
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
      toLoad.add(panel);
    }
  }
}
