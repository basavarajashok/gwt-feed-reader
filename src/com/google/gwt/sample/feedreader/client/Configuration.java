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

import com.google.gwt.core.client.GWT;
import com.google.gwt.jsio.client.JSList;
import com.google.gwt.jsio.client.JSONWrapperException;
import com.google.gwt.jsio.client.JSWrapper;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates the long-term state of the application.
 */
public class Configuration {
  /**
   * A JSON object that encapsulates the metadata for a feed.
   * @gwt.beanProperties
   */
  public abstract static class Feed implements JSWrapper {
    public abstract long getLastArticle();

    public abstract String getTitle();

    public abstract String getUrl();

    public abstract void setLastArticle(long time);

    public abstract void setTitle(String title);

    public abstract void setUrl(String url);

    public String toString() {
      return "{" + "\"url\":\"" + getUrl() + "\",\"title\":\"" + getTitle()
          + "\",\"lastArticle\":" + getLastArticle() + "}";
    }
  }

  /**
   * The top-level JSON object that encapsulates the configuration of the
   * application.
   * @gwt.beanProperties
   */
  abstract static class Container implements JSWrapper {
    /**
     * @gwt.typeArgs <com.google.gwt.sample.feedreader.client.Configuration.Feed>
     * @return A live List of Feed objects
     */
    public abstract JSList getFeeds();

    public String toString() {
      StringBuffer toReturn = new StringBuffer("{\"feeds\":[");

      Iterator i = getFeeds().iterator();
      if (i.hasNext()) {
        toReturn.append(i.next().toString());
      }
      while (i.hasNext()) {
        toReturn.append(",").append(i.next().toString());
      }

      toReturn.append("]}");
      return toReturn.toString();
    }
  }

  /**
   * Default feeds to use when others are not available.
   */
  private static final String[] FEEDS = {
      "http://arstechnica.com/index.ars/rss", "http://slashdot.org/index.rss",
      "http://googlewebtoolkit.blogspot.com/atom.xml",
      "http://thinkgeek.com/thinkgeek.rss", "http://ajaxian.com/index.xml",};

  private static final String COOKIE_NAME = "AjaxFeed";

  /**
   * The one instance of the JSON configuration container.
   */
  private final Container container = (Container) GWT.create(Container.class);

  public Configuration() {
    String cookieData = Cookies.getCookie(COOKIE_NAME);
    if (cookieData != null) {
      try {
        container.setJSONData(cookieData);
      } catch (JSONWrapperException e) {
        // Probably could not parse the json data, revert to builtins
        initialize();
      }
    } else {
      initialize();
    }
  }

  /**
   * Retrieve the metadata associated with a particular feed URL.
   */
  public Feed findFeed(String url) {
    for (Iterator i = getFeeds().iterator(); i.hasNext();) {
      Feed feed = (Feed) i.next();
      if (feed.getUrl().equals(url)) {
        return feed;
      }
    }

    return null;
  }

  /**
   * Returns the live list of Feed metadata objects.  Modifications to this
   * object will be reflected throughout the application.
   */
  public List getFeeds() {
    return container.getFeeds();
  }

  /**
   * Add a pipe-delimited set of feed URLs to the configuration.
   * @return <code>true</code> iff at least one new feed was added.
   */
  public boolean importFeeds(String token) {
    Set toAdd = new HashSet(Arrays.asList(token.split("\\|")));
    List feeds = getFeeds();
    Feed toReturn = null;

    out : for (Iterator i = toAdd.iterator(); i.hasNext();) {
      String url = (String) i.next();
      for (Iterator j = feeds.iterator(); j.hasNext();) {
        Feed feed = (Feed) j.next();
        if (feed.getUrl().equals(url)) {
          i.remove();
          if (toReturn == null) {
            toReturn = feed;
          }
          continue out;
        }
      }
    }

    if (toAdd.size() == 0) {
      return false;
    }

    boolean ok = Window.confirm("Do you wish to import " + toAdd.size()
        + " feeds?");

    if (ok) {
      for (Iterator i = toAdd.iterator(); i.hasNext();) {
        String url = (String) i.next();
        Feed f = (Feed) GWT.create(Feed.class);

        f.setUrl(url);
        f.setTitle(url);
        f.setLastArticle(0);

        if (toReturn == null) {
          toReturn = f;
        }
        feeds.add(f);
      }
    }

    save();

    return true;
  }

  /**
   * Delete the application's configuration.
   */
  public void reset() {
    Cookies.removeCookie(COOKIE_NAME);
    getFeeds().clear();
    initialize();
  }

  /**
   * Save the configuration into the application's cookie.
   */
  public void save() {
    Cookies.setCookie(COOKIE_NAME, container.toString(), new Date(
        System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365), null, null,
        false);

    System.out.println(Cookies.getCookie(COOKIE_NAME));
  }

  /**
   * This method would not be strictly necessary with some server-side support.
   */
  private void initialize() {
    if (!Window.confirm("You have no feeds configured."
        + " Would you like a default selection?")) {
      return;
    }

    JSList feeds = container.getFeeds();

    for (int i = 0; i < FEEDS.length; i++) {
      Feed f = (Feed) GWT.create(Feed.class);

      f.setUrl(FEEDS[i]);
      f.setTitle(FEEDS[i]);
      f.setLastArticle(0);

      feeds.add(f);
    }

    save();
  }
}
