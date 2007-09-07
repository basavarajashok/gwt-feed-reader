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
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the base class for all of the major UI panels. It is designed to fill
 * the full width of the client area and to have at most a single instance
 * attached to the DOM. The panel has a concept of having a parent or previous
 * panel that can be accessed by a "back" button, as well as a distinct "edit"
 * or alternate command that can be accessed by a second button in the title
 * bar. The contents of the panel should consist only of PanelLabel widgets.
 */
public abstract class SliderPanel extends Composite implements HasHTML {

  private static SliderPanel activePanel;

  protected final ClickListener parentClickListener = new ClickListener() {
    public void onClick(Widget w) {
      exit();
    }
  };

  private final SliderPanel parent;
  private final FlowPanel contents = new FlowPanel();
  private final HorizontalPanel header = new HorizontalPanel();
  private final PanelLabel panelLabel;
  private final UnsunkLabel statusLabel = new UnsunkLabel();
  private final Hyperlink permalink = new Hyperlink();

  private Command editCommand;
  private HasText hasText;
  private HasHTML hasHtml;

  public SliderPanel(String title, SliderPanel parent) {
    this.parent = parent;
    panelLabel = new PanelLabel(title, new Command() {
      public void execute() {
        enter();
      }
    });

    header.setVerticalAlignment(HorizontalPanel.ALIGN_TOP);
    header.addStyleName("header");
    // Dynamic style injection not yet working
    DOM.setStyleAttribute(header.getElement(), "background", "url('"
        + Resources.INSTANCE.toolbar() + "') #6d84a2 repeat-x");

    if (parent != null) {
      Label l = new Label(parent.getShortTitle());
      l.addClickListener(parentClickListener);
      l.addStyleName("button");
      l.addStyleName("backButton");
      // Remove once dynamic style injection working
      DOM.setStyleAttribute(l.getElement(), "-webkit-border-image", "url('"
          + Resources.INSTANCE.backButton() + "') 0 8 0 14");
      header.add(l);
    }

    UnsunkLabel titleLabel = new UnsunkLabel(title);
    titleLabel.addStyleName("titleLabel");
    header.add(titleLabel);
    header.setCellWidth(titleLabel, "100%");
    header.setCellHorizontalAlignment(titleLabel, HorizontalPanel.ALIGN_CENTER);

    FlowPanel vp = new FlowPanel();
    vp.add(header);

    contents.addStyleName("contents");
    vp.add(contents);

    initWidget(vp);
    addStyleName("SliderPanel");
  }

  public void add(PanelLabel label) {
    if ((hasText != null) || (hasHtml != null)) {
      hasText = hasHtml = null;
      contents.clear();
    }

    contents.add(label);
  }

  public void clear() {
    contents.clear();
  }

  public String getHTML() {
    return hasHtml == null ? null : hasHtml.getHTML();
  }

  public PanelLabel getLabel() {
    return panelLabel;
  }

  public String getText() {
    return hasText == null ? null : hasText.getText();
  }

  public void remove(PanelLabel label) {
    contents.remove(label);
  }

  /**
   * Set the command to be executed by a right-justified button in the title
   * bar.
   * 
   * @param label the label for the button
   * @param title the title or alt-text for the button
   * @param command the Command to execute when the button is pressed.
   */
  public void setEditCommand(String label, String title, Command command) {
    editCommand = command;
    Label l = new Label(label);
    l.addStyleName("button");
    l.addStyleName("goButton");
    // Remove once dynamic style injection working
    DOM.setStyleAttribute(l.getElement(), "-webkit-border-image", "url('"
        + Resources.INSTANCE.button() + "') 0 5 0 5");
    l.setTitle(title);
    l.addClickListener(new ClickListener() {
      public void onClick(Widget w) {
        editCommand.execute();
      }
    });
    header.add(l);
    header.setCellHorizontalAlignment(l, HorizontalPanel.ALIGN_RIGHT);
  }

  public void setHTML(String html) {
    HTML h;
    hasText = hasHtml = h = new HTML(html);

    contents.clear();
    contents.add(h);
  }

  /**
   * Add a permalink to the bottom of the panel.
   * 
   * @param token the history token to associate with the link
   */
  public void setPermalink(String token) {
    if (token == null) {
      permalink.setVisible(false);
    } else {
      permalink.setTargetHistoryToken(token);
      permalink.setVisible(true);
    }
  }

  /**
   * Set a status message at the bottom of the panel.
   * 
   * @param status the new status message
   */
  public void setStatus(String status) {
    statusLabel.setText(status);
  }

  public void setText(String text) {
    UnsunkLabel l;
    hasText = l = new UnsunkLabel(text);
    hasHtml = null;

    contents.clear();
    contents.add(l);
  }

  /**
   * Display the panel, removing any currently-displayed panel from the screen.
   * If the panel is already displayed, calling this method again will produce
   * no result.
   */
  protected void enter() {
    if (isAttached()) {
      return;
    }

    if (activePanel != null) {
      RootPanel.get().remove(activePanel);
    }

    activePanel = SliderPanel.this;
    RootPanel.get().add(SliderPanel.this, 0, 0);

    DeferredCommand.addPause();
    DeferredCommand.addCommand(new Command() {
      public native void execute() /*-{
       $wnd.scrollTo(0, 1);
       }-*/;
    });
  }

  /**
   * Return to the parent panel.
   */
  protected void exit() {
    if (parent == null) {
      throw new RuntimeException("SliderPanel has no parent");
    }

    parent.enter();
  }

  /**
   * A short title to be used as the label of the back button.
   */
  protected abstract String getShortTitle();
}
