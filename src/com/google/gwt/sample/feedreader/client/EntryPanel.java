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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Displays a single entry in a feed.
 */
public class EntryPanel extends SliderPanel {
  final EntryWrapper entry;
  final PanelLabel panelLabel;
  boolean contentsSet = false;
  
  public EntryPanel(final EntryWrapper entry, SliderPanel parent) {
    super(entry.getTitle(), parent);
    addStyleName("EntryPanel");
    this.entry = entry;
    
    Label title = new Label(entry.getTitle());
    title.addStyleName("title");
    
    Label snippit = new Label(entry.getContentSnippet());
    snippit.addStyleName("snippit");
    
    VerticalPanel vp = new VerticalPanel();
    vp.add(title);
    vp.add(snippit);
    
    this.panelLabel = new PanelLabel(vp, new Command() {
      public void execute() {
        enter();
      }
    });
    
    setEditCommand("Details", "Show entry details", new Command() {
      public void execute() {
        (new DetailPanel(entry, EntryPanel.this)).enter();
      }
    });
  }

  public void enter() {
    if (isAttached()) {
      return;
    }
    
    if (!contentsSet) {
      add(new PanelLabel(entry.getContent(), null, true));
      add(new PanelLabel("Open article", new Command() {
        public void execute() {
         Window.open(entry.getLink(), "_blank", null);
        }
      }));
     contentsSet = true;
    }
    super.enter();
    History.newItem("");
  }
  
  public PanelLabel getLabel() {
    return panelLabel;
  }
  
  protected String getShortTitle() {
    return "Entry";
  }
}
