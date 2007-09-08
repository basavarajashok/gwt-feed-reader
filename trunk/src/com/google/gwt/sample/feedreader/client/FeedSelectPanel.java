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

import com.google.gwt.ajaxfeed.client.impl.ErrorWrapper;
import com.google.gwt.ajaxfeed.client.impl.FeedCallback;
import com.google.gwt.ajaxfeed.client.impl.FindResultApi;
import com.google.gwt.ajaxfeed.client.impl.Globals;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.Iterator;
import java.util.List;

/**
 * A panel to allow the user to choose feeds from a search result.
 */
public class FeedSelectPanel extends WallToWallPanel {
  private static final FindResultApi RESULT_API =
      (FindResultApi) GWT.create(FindResultApi.class);

  public FeedSelectPanel(final ConfigurationPanel parent,
      final Configuration configuration, String query) {
    super("Select feed...", parent);

    FeedCallback fc = new FeedCallback() {
      public void onLoad(final JavaScriptObject jso) {
        RESULT_API.bind(jso);

        ErrorWrapper error = RESULT_API.getError(jso);
        if (error != null) {
          Window.alert("Unable to find feeds.\n" + error.getMessage());
          return;
        }

        // Remove the loading message
        clear();

        // Update the UI piecewise
        DeferredCommand.addCommand(new IncrementalCommand() {
          List feeds = configuration.getFeeds();
          Iterator i = RESULT_API.getEntries(jso).iterator();

          public boolean execute() {
            final FindResultApi.Entry entry = (FindResultApi.Entry) i.next();
            if (feeds.contains(entry.getUrl())) {
              return i.hasNext();
            }

            UnsunkLabel title = new UnsunkLabel(entry.getTitle(), true);
            title.addStyleName("title");
            UnsunkLabel snippit =
                new UnsunkLabel(entry.getContentSnippet(), true);
            snippit.addStyleName("snippit");
            UnsunkLabel url = new UnsunkLabel(entry.getUrl());
            url.addStyleName("snippit");

            FlowPanel vp = new FlowPanel();
            vp.add(title);
            vp.add(snippit);
            vp.add(url);

            add(new PanelLabel(vp, new Command() {
              public void execute() {
                History.newItem(entry.getUrl());
              }
            }));

            return i.hasNext();
          }
        });
      }
    };

    Globals.API.findFeeds(query, fc);

    addStyleName("FeedSelectPanel");
    add(new PanelLabel("Waiting for results"));
  }

  protected String getShortTitle() {
    return "Select feeds";
  }
}
