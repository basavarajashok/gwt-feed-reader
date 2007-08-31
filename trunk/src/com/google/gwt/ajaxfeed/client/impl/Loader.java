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
package com.google.gwt.ajaxfeed.client.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This class must be invoked before any other class in the Google AJAX Feed API
 * is used. It is responsible for downloading and initializing the API. Because
 * the AJAX Feed API is external to the GWT application, the API may not be
 * immediately available, so a callback is used to notify the GWT code that the
 * API is ready for use.
 */
public final class Loader {
  static Command command;
  private static final Frame SANDBOX = new Frame();

  /**
   * Indicates whether or not the external JavaScript for the Google AJAX Feed
   * API has been installed into the JSVM.
   * 
   * @return <code>true</code> iff the API is ready for use.
   */
  public static native boolean apiReady() /*-{
   return Boolean($wnd.google_feed && $wnd.google_feed.feeds && $wnd.google_feed.feeds.Feed || false);
   }-*/;

  /**
   * Initialize the Google AJAX Feed API.
   * 
   * @param apiKey The developer's API key
   * @param onLoadCallback A Command that will be executed when the API is
   *          initialized.
   */
  public static void init(final String apiKey, Command onLoadCallback) {
    command = onLoadCallback;
    registerSandboxCallback();

    SANDBOX.setWidth("0");
    SANDBOX.setHeight("0");
    RootPanel.get().add(SANDBOX, 0, 0);
    // Allow the iframe's document to be set up first
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        setupDocument(getDocument(SANDBOX), apiKey);
      }
    });
  }

  /**
   * This is called from the sandbox iframe with the top-level google object.
   */
  static native void ajaxFeedLoad(JavaScriptObject google) /*-{
   $wnd.google_feed = google;
   @com.google.gwt.ajaxfeed.client.impl.Loader::startTimer()();
   }-*/;

  static void startTimer() {
    Timer t = new Timer() {
      public void run() {
        if (apiReady()) {
          cancel();
          closeDocument(getDocument(SANDBOX));
          command.execute();
        } else {
          System.out.println("Not ready");
        }
      }
    };
    t.scheduleRepeating(10);
  }

  static void throwRuntimeException(String msg) {
    throw new RuntimeException(msg);
  }

  private static native void closeDocument(Element doc) /*-{
    doc.close();
  }-*/;

  private static native Element getDocument(Frame frame) /*-{
    var elt = frame.@com.google.gwt.user.client.ui.UIObject::getElement()();
    
    // FF || IE
    var doc = elt.contentDocument || elt.contentWindow;
    // Opera sometimes returns the window
    if (doc.document) {
    doc = doc.document;
    }
    
    if (!doc) {
      @com.google.gwt.ajaxfeed.client.impl.Loader::throwRuntimeException(Ljava/lang/String;)("Unable to obtain sandbox");
    }
    
    return doc;
  }-*/;
  
  private static native void registerSandboxCallback() /*-{
   $wnd.AjaxFeedLoad = @com.google.gwt.ajaxfeed.client.impl.Loader::ajaxFeedLoad(Lcom/google/gwt/core/client/JavaScriptObject;);
   }-*/;
  
  private static native void setupDocument(Element doc, String apiKey) /*-{
   doc.open();
   doc.write('<html><head>');
   doc.write('<script type="text/javascript" src="http://www.google.com/jsapi');
   if (apiKey) {
     doc.write('?key=' + apiKey);
   }
   
   doc.write('"></script>');
   doc.write('<script type="text/javascript">');
   doc.write('google.load("feeds", "1");');
   doc.write('window.parent.AjaxFeedLoad(google);');
   doc.write('</script>');
   doc.write('</head><body></body></html>');
   }-*/;

  private Loader() {
  }
}
