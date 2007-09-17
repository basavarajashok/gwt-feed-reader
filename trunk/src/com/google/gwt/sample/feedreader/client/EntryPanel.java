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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Displays a single entry in a feed.
 */
public class EntryPanel extends WallToWallPanel {
  final EntryWrapper entry;
  final PanelLabel panelLabel;
  boolean contentsSet = false;

  public EntryPanel(final EntryWrapper entry, WallToWallPanel parent) {
    super(entry.getTitle(), parent);
    addStyleName("EntryPanel");
    this.entry = entry;

    UnsunkLabel title = new UnsunkLabel(entry.getTitle());
    title.addStyleName("title");

    UnsunkLabel snippit = new UnsunkLabel(entry.getContentSnippet());
    snippit.addStyleName("snippit");

    FlowPanel vp = new FlowPanel();
    vp.add(title);
    vp.add(snippit);

    this.panelLabel = new PanelLabel(vp, new Command() {
      public void execute() {
        enter();
      }
    });
  }

  public void enter() {
    if (isAttached()) {
      return;
    }

    if (!contentsSet) {
      PanelLabel contents = new PanelLabel(entry.getContent(), null, true);
      retargetLinks(contents.getElement());
      contents.addStyleName("articleContents");
      add(contents);
      add(new PanelLabel("Open article", new Command() {
        public void execute() {
          Window.open(entry.getLink(), "_blank", "");
        }
      }));
      contentsSet = true;
    }
    super.enter();
    // Add an item so that we can back into the FeedPanel
    History.newItem("");
  }

  public void exit() {
    // Dump the DOM elements on exit to avoid memory issues.
    clear();
    contentsSet = false;

    super.exit();
  }

  public PanelLabel getLabel() {
    return panelLabel;
  }

  protected String getShortTitle() {
    return "Entry";
  }

  /**
   * Force all links to open in a new window so that application state isn't
   * lost.
   * 
   * @param elt
   */
  protected void retargetLinks(Element elt) {
    int numChildren = DOM.getChildCount(elt);
    for (int i = 0; i < numChildren; i++) {
      Element child = DOM.getChild(elt, i);
      String href = DOM.getElementProperty(child, "href");
      if (href != null) {
        DOM.setElementAttribute(child, "onclick", "javascript:window.open('"
            + href + "', '_blank');");
        DOM.removeElementAttribute(child, "href");
        DOM.setElementAttribute(child, "class", "externalLink");
      }
      retargetLinks(child);
    }
  }
}
