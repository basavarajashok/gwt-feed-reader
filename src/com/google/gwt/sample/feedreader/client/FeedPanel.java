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
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.IncrementalCommand;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Display all of the entries in a Feed so that the user may select one to view.
 */
public class FeedPanel extends WallToWallPanel {
  private static final FeedResultApi resultApi =
      (FeedResultApi) GWT.create(FeedResultApi.class);
  private static final FeedApi feedApi = (FeedApi) GWT.create(FeedApi.class);
  private static final JsonFeedApi jsonFeedApi =
      (JsonFeedApi) GWT.create(JsonFeedApi.class);

  // private final PanelLabel panelLabel;
  private final Configuration.Feed feed;
  private final Map /* <String, EntryPanel> */entryPanels = new HashMap();
  private boolean loadStarted = false;
  private boolean loadFinished = false;
  private boolean drawn = false;
  private List entries;
  private String requestedEntry;

  public FeedPanel(final Configuration.Feed feed, ManifestPanel parent) {
    super(feed.getTitle(), parent);
    this.feed = feed;

    getLabel().setTitle(feed.getTitle());

    addStyleName("FeedPanel");
    setText("Loading");
  }

  public void clear() {
    entryPanels.clear();
    super.clear();
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
    loadStarted = true;
    loadFinished = false;

    getLabel().setBusy(true);

    JavaScriptObject feedJso = feedApi.construct(feed.getUrl());
    feedApi.setNumEntries(feedJso, 20);
    feedApi.load(feedJso, new FeedCallback() {
      public void onLoad(JavaScriptObject feedResult) {
        // Fix up any missing fields
        resultApi.bind(feedResult);

        ErrorWrapper errorResponse = resultApi.getError(feedResult);
        if (errorResponse != null) {
          getLabel().setText(feed.getTitle() + " (Error)");
          setText("Unable to load feed (" + errorResponse.getMessage() + ")");
          return;
        }

        JavaScriptObject jsonFeed = resultApi.getFeed(feedResult);
        entries = jsonFeedApi.getEntries(jsonFeed);

        String title = jsonFeedApi.getTitle(jsonFeed);
        feed.setTitle(title);
        getLabel().setText(title);

        getLabel().setBusy(false);

        final Date lastViewed = new Date(feed.getLastArticle());

        // Count the number of new entries while the next feed downloads
        DeferredCommand.addCommand(new IncrementalCommand() {
          final Iterator i = entries.iterator();
          int newEntries = 0;

          public boolean execute() {
            EntryWrapper entry = (EntryWrapper) i.next();

            try {
              if ((new Date(entry.getPublishedDate())).after(lastViewed)) {
                newEntries++;
              }
            } catch (IllegalArgumentException e) {
              // Ignore date formats that we can't parse.
            }

            if (i.hasNext()) {
              return true;

            } else {
              // Show the number of new entries
              if (newEntries > 0) {
                getLabel().addStyleName("unseen");
                getLabel().setText("(" + newEntries + ") " + feed.getTitle());
              } else {
                getLabel().setText(feed.getTitle());
              }

              getLabel().setBusy(false);

              return false;
            }
          }
        });

        loadStarted = false;
        loadFinished = true;
        drawn = false;
        redraw();
      }
    });
  }

  protected void enter() {
    if (isAttached()) {
      return;
    }

    if (!loadStarted && !loadFinished) {
      loadFeed();
    }

    super.enter();
    redraw();
    feed.setLastArticle(System.currentTimeMillis());
    getLabel().setText(feed.getTitle());
    History.newItem(feed.getUrl());
  }

  protected String getShortTitle() {
    return "Feed";
  }

  /**
   * Display the feed entry with the given link URL. If the FeedPanel has not
   * been loaded, this method will display the FeedPanel. If there is no entry
   * with the specified link URL, the FeedPanel will be displayed.
   * 
   * @param entryUrl the link URL of the entry that should be displayed
   */
  void showEntry(String entryUrl) {
    if (loadFinished) {
      EntryPanel panel = (EntryPanel) entryPanels.get(entryUrl);
      if (panel != null) {
        panel.enter();
      } else {
        enter();
      }
    } else {
      // Couldn't find the named article, just show the panel.
      requestedEntry = entryUrl;
      enter();
    }
  }

  private void redraw() {
    if (drawn || !isAttached()) {
      return;
    }

    drawn = true;

    if (!loadFinished) {
      add(new PanelLabel("Loading"));
      return;
    }

    clear();

    DeferredCommand.addCommand(new IncrementalCommand() {
      final Iterator i = entries.iterator();
      PanelLabel lastLabel;

      public boolean execute() {
        EntryWrapper entry = (EntryWrapper) i.next();
        EntryPanel panel = new EntryPanel(entry, feed, FeedPanel.this);
        entryPanels.put(entry.getLink(), panel);
        lastLabel = panel.getLabel();
        add(lastLabel);

        if (i.hasNext()) {
          return true;
        } else {
          // We want to format the last element a little differently
          lastLabel.addStyleName("last");

          if (requestedEntry != null) {
            showEntry(requestedEntry);
            requestedEntry = null;
          }

          return false;
        }
      }
    });
  }
}
