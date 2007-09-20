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

import com.google.gwt.ajaxfeed.client.impl.Loader;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.URL;
import com.google.gwt.sample.feedreader.client.resources.Resources;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.styleinjector.client.StyleInjector;

/**
 * The main entry point for the application.
 */
public class GwtFeedReader implements EntryPoint {
  private Configuration configuration;
  private ManifestPanel manifest;

  public void onModuleLoad() {
    // The first thing that we want to do is to get the Ajax Feed API
    // to download and initialize. All of the other initialization should
    // be deferred until the iframe kicks off
    Loader.init(getApiFeedKey(), new Loader.LoaderCallback() {
      public void onError(Throwable t) {
        Window.alert("Unable to initialize AJAX Feed API.\n" + t.getMessage());
        t.printStackTrace();
      }

      public void onLoad() {
        onAjaxFeedLoad();
      }
    });

    // Make sure the Loader's modifications to the DOM have taken effect before
    // we do anything else.
    DeferredCommand.addPause();

    DeferredCommand.addCommand(new Command() {
      public void execute() {
        initialize();
      }
    });
  }

  private native String getApiFeedKey() /*-{
   return $wnd.AjaxFeedApiKey || null;
   }-*/;

  /**
   * Initialize the global state of the application.
   */
  private void initialize() {
    // This function should only work once.
    if (configuration != null) {
      return;
    }

    configuration = new Configuration();

    // Create the root UI element
    manifest = new ManifestPanel(configuration);

    // Use a WindowCloseListener to save the configuration
    Window.addWindowCloseListener(new WindowCloseListener() {

      public void onWindowClosed() {
        configuration.save();
      }

      public String onWindowClosing() {
        // TODO have a configuration value to warn before closing.
        return null;
      }
    });

    // Start a timer to automatically save the configuration in case of sudden
    // exit.
    (new Timer() {
      public void run() {
        configuration.save();
      }
    }).scheduleRepeating(1000 * 60 * 5);

    // Add a HistoryListener to control the application.
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

    // The stylesheet are factored into two pieces: a common template
    // that defines sizes and structural elements.
    StyleInjector.injectStylesheet(Resources.INSTANCE.layoutCss().getText());
    // And further CSS rules that provide look-and-feel and localized resources
    StyleInjector.injectStylesheet(
        Resources.INSTANCE.appearanceCss().getText(), Resources.INSTANCE);

    UnsunkLabel logo = new UnsunkLabel();
    logo.addStyleName("logo");
    RootPanel.get().add(logo, 0, 0);
  }

  /**
   * Executed after the Ajax Feed API has been initialized.
   */
  private void onAjaxFeedLoad() {
    // Call the initialization function for completeness; It's highly unlikely
    // that the feed api would have initialized before the DeferredCommand has
    // executed.
    initialize();

    // Set the initial state of the application based on the initial history
    // token
    String token = History.getToken();
    if (token == null || token.length() == 0) {
      // If there's no token, just show the main UI form
      manifest.enter();
    } else {
      processHistoryToken(token);
    }
  }

  /**
   * Change the application's state based on a new history token.
   */
  private void processHistoryToken(String token) {
    if (token.length() == 0) {
      return;
    }

    token = URL.decodeComponent(token);

    if ("about".equals(token)) {
      ConfigurationPanel cp = new ConfigurationPanel(configuration, manifest);
      cp.showAbout();
      return;
    }

    if ("manifest".equals(token)) {
      manifest.enter();
      return;
    }

    if ("configuration".equals(token)) {
      ConfigurationPanel cp = new ConfigurationPanel(configuration, manifest);
      cp.enter();
      return;
    }

    if (token.startsWith("search||")) {
      ConfigurationPanel cp = new ConfigurationPanel(configuration, manifest);
      cp.showSearch(token.substring(8));
      return;
    }

    String feedUrl;
    String entryUrl;

    if (token.indexOf("||") != -1) {
      String[] halves = token.split("\\|\\|");
      feedUrl = halves[0];
      entryUrl = halves[1];
    } else {
      feedUrl = token;
      entryUrl = null;
    }

    // See if the token represents a new feed URL. If a new URL was added
    // to the configuration, tell the manifest panel it should redraw itself
    // the next time it's shown.
    if (configuration.importFeeds(feedUrl)) {
      manifest.setDirty();
    }

    // Find the URL and display it
    Configuration.Feed feed = configuration.findFeed(feedUrl);
    if (feed != null) {
      manifest.showFeed(feed, entryUrl);
    }
  }
}
