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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 *  A widget that represents a single label.  A label might represent a feed,
 *  and entry in a feed, or some other named action.
 */
public class PanelLabel extends HorizontalPanel implements HasText {
  final Command primary;
  final Widget widget;
  
  public PanelLabel(String text) {
    this(text, null, false);
  }
  
  public PanelLabel(String text, Command primary) {
    this(text, primary, false);
  }
  
  public PanelLabel(String text, Command primary, boolean html) {
      this(html ? new HTML(text) : new Label(text), primary);
  }
  
  public PanelLabel(Widget widget, Command primary) {
    this.primary = primary;
    this.widget = widget;

    add(widget);
    
    if (primary != null) {
      Image enter = Images.INSTANCE.enter().createImage();
      add(enter);
      setCellWidth(enter, enter.getWidth() + "px");
      setCellHorizontalAlignment(enter, HorizontalPanel.ALIGN_RIGHT);
      setCellVerticalAlignment(enter, HorizontalPanel.ALIGN_MIDDLE);
      sinkEvents(Event.ONCLICK);
    }

    addStyleName("PanelLabel");
  }
  
  public String getText() {
    if (widget instanceof HasText) {
      return ((HasText)widget).getText();
    } else {
      return widget.toString();
    }
  }
  
  public void onBrowserEvent(Event e) {
    switch (DOM.eventGetType(e)) {
      case Event.ONCLICK:
        primary.execute();
    }
  }
  
  public void setText(String text) {
    if (widget instanceof HasText) {
      ((HasText)widget).setText(text);
    }
  }
}
