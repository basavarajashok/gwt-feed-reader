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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Go.
 */
public class AjaxFeed implements EntryPoint {
  private final Configuration configuration = new Configuration();
  
  ManifestPanel manifest = new ManifestPanel(configuration);

  public void onModuleLoad() {
    Image logo = Images.INSTANCE.logo().createImage();
    logo.addStyleName("logo");
    RootPanel.get().add(logo, 0, 0);
    
    Window.addWindowCloseListener(new WindowCloseListener() {

      public void onWindowClosed() {
        configuration.save();
      }

      public String onWindowClosing() {
        // TODO have a configuration value to warn before closing.
        return null;
      }
    });

    History.addHistoryListener(new HistoryListener() {
      /**
       * Prevent repeated loads of the same token.
       */
      String lastToken = "";

      public void onHistoryChanged(String token) {
        if (lastToken.equals(token)) {
          return;
        }

        if (token != null) {
          lastToken = token;
          processHistoryToken(token);
        }
      }
    });

    String token = History.getToken();
    if (token == null || token.length() == 0) {
      manifest.enter();
    } else {
      processHistoryToken(token);
    }
    (new Timer() {
      public void run() {
        configuration.save();
      }
    }).scheduleRepeating(1000 * 60 * 5);
  }

  private void processHistoryToken(String token) {
    if (token.length() == 0) {
      return;
    }
    
    token = URL.decodeComponent(token);

    if ("manifest".equals(token)) {
      manifest.enter();
      return;
    }
    
    if ("configuration".equals(token)) {
      manifest.showConfiguration();
      return;
    }

    if (configuration.importFeeds(token)) {
      manifest.setDirty();
    }

    Configuration.Feed feed = configuration.findFeed(token);
    if (feed != null) {
      manifest.showFeed(feed);
    }
  }
}
