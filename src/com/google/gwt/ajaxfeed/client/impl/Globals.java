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

import com.google.gwt.core.client.GWT;
import com.google.gwt.jsio.client.JSWrapper;

/**
 * Encapsulates "global" functions in the Ajax Feed API.
 * 
 * @gwt.global $wnd.google.feeds
 */
public abstract class Globals implements JSWrapper {
  public static final Globals API = (Globals) GWT.create(Globals.class);

  /**
   * Used to conduct a search for feeds with articles relevant to the provided query.
   * @param query the search query
   * @param callback a callback to be invoked when a response is available
   */
  public abstract void findFeeds(String query, FeedCallback callback);

  /**
   * Used to canonicalize and verify the existence of a feed's URL.
   * @param url the url to verify
   * @param callback a callback to be invoked when a response is available
   */
  public abstract void lookupFeed(String url, FeedCallback callback);
}
