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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import java.util.Iterator;
import java.util.List;

/**
 * Shows details about an entry.
 */
public class DetailPanel extends SliderPanel {

  public DetailPanel(final EntryWrapper entry, SliderPanel parent) {
    super("Details", parent);

    add(new PanelLabel(entry.getTitle()));
    add(new PanelLabel(entry.getPublishedDate()));
    add(new PanelLabel("Open article", new Command() {
      public void execute() {
        Window.open(entry.getLink(), "_blank", null);
      }
    }));

    // Sometimes entries have server-provided categories.  Display them as well.
    List categories = entry.getCategories();
    if (categories.size() > 0) {
      StringBuffer sb = new StringBuffer();
      for (Iterator i = categories.iterator(); i.hasNext();) {
        sb.append(i.next().toString());
        sb.append(" ");
      }
      add(new PanelLabel(sb.toString()));
    }
  }

  protected String getShortTitle() {
    return "Details";
  }
}
