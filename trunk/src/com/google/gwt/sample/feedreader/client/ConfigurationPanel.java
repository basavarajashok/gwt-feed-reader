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

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * The panel by which the application is configured.
 */
public class ConfigurationPanel extends WallToWallPanel {
  private final Configuration configuration;
  private final HashMap feedLabels = new HashMap();
  private final ManifestPanel parent;

  public ConfigurationPanel(final Configuration configuration,
      final ManifestPanel parent) {
    super("Configuration", parent);
    this.configuration = configuration;
    this.parent = parent;
    final List feeds = configuration.getFeeds();

    setEditCommand("Reset", "Delete all application settings", new Command() {
      public void execute() {
        if (!Window.confirm("Do you wish to reset the application?")) {
          return;
        }
        configuration.reset();
        parent.setDirty();
        exit();
      }
    });

    add(new PanelLabel("About...", new Command() {
      public void execute() {
        History.newItem("about");
      }
    }));

    // Create the search option
    {
      UnsunkLabel title = new UnsunkLabel("Search for new feeds...");
      title.addStyleName("title");
      UnsunkLabel info = new UnsunkLabel("Add feeds by searching.");
      info.addStyleName("snippit");

      FlowPanel vp = new FlowPanel();
      vp.add(title);
      vp.add(info);

      add(new PanelLabel(vp, new Command() {
        public void execute() {
          final String query = Window.prompt("Search query", "");
          if ((query != null) && (query.length() > 0)) {
            History.newItem("search||" + URL.encodeComponent(query));
          }
        }
      }));
    }

    // Add all of the feeds to the panel.
    for (Iterator i = feeds.iterator(); i.hasNext();) {
      final Configuration.Feed feed = (Configuration.Feed) i.next();
      addFeed(feed);
    }
  }

  public void enter() {
    super.enter();
    History.newItem("configuration");
  }

  /**
   * Perform the necessary UI updates to display a new feed configuration
   * option.
   */
  protected void addFeed(final Configuration.Feed feed) {
    UnsunkLabel title = new UnsunkLabel("Remove " + feed.getTitle());
    title.addStyleName("title");
    UnsunkLabel snippit = new UnsunkLabel(feed.getUrl());
    snippit.addStyleName("snippit");

    FlowPanel vp = new FlowPanel();
    vp.add(title);
    vp.add(snippit);

    PanelLabel p = new PanelLabel(vp, new Command() {
      public void execute() {
        remove(feed);
      }
    });
    p.setTitle("Delete this feed");

    feedLabels.put(feed, p);
    add(p);
  }

  protected String getShortTitle() {
    return "Configuration";
  }

  /**
   * Remove a feed's representation from the UI.
   */
  protected void remove(Configuration.Feed feed) {
    if (Window.confirm("Do you wish to remove the feed?")) {
      configuration.getFeeds().remove(feed);
      remove((PanelLabel) feedLabels.get(feed));
      parent.setDirty();
    }
  }

  void showAbout() {
    (new AboutPanel(this)).enter();
  }

  void showSearch(String query) {
    FeedSelectPanel selector =
        new FeedSelectPanel(ConfigurationPanel.this, configuration, query);
    selector.enter();
  }
}
