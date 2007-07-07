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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * TODO.
 */
public class ConfigurationPanel extends SliderPanel {
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
        exit();
      }
    });

    AboutPanel about = new AboutPanel(this);
    add(about.getLabel());

    Label title = new Label("Add feed...");
    title.addStyleName("title");
    HTML fastAdd = new HTML(
        "To quickly add many URLs, send yourself an IM with permalinks "
            + Images.INSTANCE.popout().getHTML() + " found "
            + "at the bottom of each feed panel.");
    fastAdd.addStyleName("snippit");

    VerticalPanel vp = new VerticalPanel();
    vp.add(title);
    vp.add(fastAdd);

    add(new PanelLabel(vp, new Command() {
      public void execute() {
        String url = Window.prompt("Feed address", "http://");
        if (url != null) {
          Configuration.Feed f = (Configuration.Feed) GWT.create(Configuration.Feed.class);
          f.setTitle(url);
          f.setUrl(url);
          feeds.add(f);
          addFeed(f);
        }
      }
    }));

    for (Iterator i = feeds.iterator(); i.hasNext();) {
      final Configuration.Feed feed = (Configuration.Feed) i.next();
      addFeed(feed);
    }
  }

  protected void addFeed(final Configuration.Feed feed) {
    Label title = new Label(feed.getTitle());
    title.addStyleName("title");
    Label snippit = new Label(feed.getUrl());
    snippit.addStyleName("snippit");

    VerticalPanel vp = new VerticalPanel();
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

  protected void exit() {
    parent.refresh();
    super.exit();
  }

  protected String getShortTitle() {
    return "Configuration";
  }

  protected void remove(Configuration.Feed feed) {
    if (Window.confirm("Do you wish to remove the feed?")) {
      configuration.getFeeds().remove(feed);
      remove((PanelLabel) feedLabels.get(feed));
    }
  }

}
