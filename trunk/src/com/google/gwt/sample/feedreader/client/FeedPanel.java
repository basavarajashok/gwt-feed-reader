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

import com.google.gwt.ajaxfeed.client.impl.EntryWrapper;
import com.google.gwt.ajaxfeed.client.impl.ErrorWrapper;
import com.google.gwt.ajaxfeed.client.impl.FeedApi;
import com.google.gwt.ajaxfeed.client.impl.FeedCallback;
import com.google.gwt.ajaxfeed.client.impl.FeedResultApi;
import com.google.gwt.ajaxfeed.client.impl.JsonFeedApi;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Display all of the entries in a Feed so that the user may select one to view.
 */
public class FeedPanel extends SliderPanel {
  /**
   * This class serves as a synchronization point for downloading the contents
   * of a feed.  If it contains any elements and there is no currently-loading
   * feed, the first feed in the list will be loaded every 100ms.
   */
  private static class LoaderList extends ArrayList {
    Timer t = new Timer() {
      public void run() {
        if (loadingFeed != null && !loadingFeed.isLoadFinished()) {
          return;
        }

        if (size() == 0) {
          cancel();
          return;
        }

        FeedPanel p = (FeedPanel) get(0);
        if (p.isLoadFinished()) {
          remove(0);
        } else if (!p.isLoadStarted()) {
          p.loadFeed();
        }
      }
    };

    public boolean add(Object o) {
      t.scheduleRepeating(100);
      return super.add(o);
    }
  }
  
  private static final FeedResultApi resultApi = (FeedResultApi) GWT.create(FeedResultApi.class);
  private static final FeedApi feedApi = (FeedApi) GWT.create(FeedApi.class);
  private static final JsonFeedApi jsonFeedApi = (JsonFeedApi) GWT.create(JsonFeedApi.class);
  
  /**
   * The list of feeds that need to be loaded.
   */
  private static final List toLoad = new LoaderList();
  
  /**
   * Used to prevent more than one FeedPanel from being automatically loaded at
   * a time.
   */
  private static FeedPanel loadingFeed;

  private final PanelLabel panelLabel;
  private final Configuration.Feed feed;
  private boolean loadStarted = false;
  private boolean loadFinished = false;
  private List entries;
  private int numEntries = 0;

  private boolean dirty = false;

  public FeedPanel(final Configuration.Feed feed, ManifestPanel parent) {
    super(feed.getTitle(), parent);
    this.feed = feed;
    
    panelLabel = new PanelLabel(feed.getTitle(), new Command() {
      public void execute() {
        enter();
      }
    });
    panelLabel.setTitle(feed.getUrl());

    addStyleName("FeedPanel");
    setPermalink(URL.encodeComponent(feed.getUrl()));
    setText("Loading");
    toLoad.add(this);
  }
  
  public PanelLabel getLabel() {
    return panelLabel;
  }

  public boolean isLoadFinished() {
    return loadFinished;
  }

  public boolean isLoadStarted() {
    return loadStarted;
  }

  /**
   * Load a single feed.
   */
  public void loadFeed() {
    if (loadStarted) {
      return;
    }
    loadingFeed = this;
    loadStarted = true;
    loadFinished = false;

    getLabel().setText(feed.getTitle() + " (Loading)");

    JavaScriptObject feedJso = feedApi.construct(feed.getUrl());
    feedApi.setNumEntries(feedJso, 20);
    feedApi.load(feedJso, new FeedCallback() {
      public void onLoad(JavaScriptObject feedResult) {
        loadStarted = true;
        loadFinished = true;
        setFeedResult(feedResult);
      }
    });
  }
  
  public void setDirty() {
    dirty = true;
  }

  public void setFeedResult(JavaScriptObject feedResult) {
    // Fix up any missing fields
    resultApi.bind(feedResult);

    ErrorWrapper errorResponse = resultApi.getError(feedResult);
    if (errorResponse != null) {
      getLabel().setText(feed.getTitle() + " (Error)");
      setText("Unable to load feed (" + errorResponse.getMessage() + ")");
      return;
    }

    setResponse(resultApi.getFeed(feedResult));
  }

  public void setResponse(JavaScriptObject jsonFeed) {
    dirty = true;
    entries = jsonFeedApi.getEntries(jsonFeed);
    numEntries = entries.size();

    feed.setTitle(jsonFeedApi.getTitle(jsonFeed));
    getLabel().setText(
        feed.getTitle() + " (Parsing " + numEntries + " entries)");

    final Date lastViewed = new Date(feed.getLastArticle());

    // No more UI setup to do
    if (numEntries == 0) {
      getLabel().setText(
          feed.getTitle() + " (No entries)");
      return;
    }

    // This resets the label's unread counter.  We want to do this in the
    // background.
    IncrementalCommand labelSetup = new IncrementalCommand() {
      int newEntries = 0;
      Iterator i = entries.iterator();

      public boolean execute() {
        EntryWrapper entry = (EntryWrapper) i.next();

        if ((new Date(entry.getPublishedDate())).after(lastViewed)) {
          newEntries++;
        }

        if (i.hasNext()) {
          return true;
          
        } else {
          // No more entries
          if (newEntries > 0) {
            getLabel().addStyleName("unseen");
            getLabel().setText(feed.getTitle() + " (" + newEntries + ")");
          } else {
            getLabel().setText(feed.getTitle());
          }
          
          long loadTime = System.currentTimeMillis();
          setStatus("Last loaded at " + (new Date(loadTime)).toLocaleString());
          return false;
        }
      }
    };
    
    DeferredCommand.addCommand(labelSetup);
    if (isAttached()) {
      refresh();
    }
  }

  protected void enter() {
    if (isAttached()) {
      return;
    }
    loadFeed();
    super.enter();
    refresh();
    History.newItem(feed.getUrl());
  }

  protected String getShortTitle() {
    return "Feed";
  }

  private void refresh() {
    if (!dirty) {
      return;
    }
    dirty = false;

    clear();
    
    DeferredCommand.addCommand(new IncrementalCommand() {
      final Iterator i = entries.iterator();

      // DOM updates can be kind of slow, so we'll show the user updates as they
      // happen
      public boolean execute() {
        EntryWrapper entry = (EntryWrapper) i.next();
        EntryPanel panel = new EntryPanel(entry, FeedPanel.this);
        add(panel.getLabel());
        return i.hasNext();
      }
    });

    // Can do this in the background.
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        getLabel().removeStyleName("unseen");
        getLabel().setText(feed.getTitle());
        feed.setLastArticle(System.currentTimeMillis());
      }
    });
  }
}
