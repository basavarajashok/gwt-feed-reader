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
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
public abstract class WallToWallPanel extends Composite implements HasHTML {

  private static WallToWallPanel activePanel;

  protected final ClickListener parentClickListener = new ClickListener() {
    public void onClick(Widget w) {
      exit();
    }
  };

  private final WallToWallPanel parent;
  private final FlowPanel contents = new FlowPanel();
  private final HorizontalPanel header = new HorizontalPanel();
  private final PanelLabel panelLabel;
  private final UnsunkLabel titleLabel = new UnsunkLabel("");

  private Command editCommand;
  private HasText hasText;
  private HasHTML hasHtml;
  private PanelLabel lastSelectedLabel;

  public WallToWallPanel(String title, WallToWallPanel parent) {
    this.parent = parent;
    panelLabel = new PanelLabel("", new Command() {
      public void execute() {
        enter();
      }
    }) {
      public void setText(String title) {
        titleLabel.setText(title);
        super.setText(title);
      }
    };
    panelLabel.setText(title);

    header.setVerticalAlignment(HorizontalPanel.ALIGN_TOP);
    header.addStyleName("header");

    if (parent != null) {
      Label l = new Label(parent.getShortTitle());
      l.addClickListener(parentClickListener);
      l.addStyleName("button");
      l.addStyleName("backButton");
      header.add(l);
    }

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
      // Save the label to scroll to when backing into the parent panel.
      if (activePanel == parent) {
        parent.setLastSelectedLabel(getLabel());
      }

      RootPanel.get().remove(activePanel);
    }

    activePanel = WallToWallPanel.this;
    RootPanel.get().add(WallToWallPanel.this, 0, 0);

    DeferredCommand.addPause();
    DeferredCommand.addCommand(new ScrollToCommand(lastSelectedLabel));
  }

  /**
   * Return to the parent panel.
   */
  protected void exit() {
    if (parent == null) {
      throw new RuntimeException("SliderPanel has no parent");
    }

    // When backing out, we don't want to go to our last label when the panel
    // is re-entered.
    setLastSelectedLabel(null);

    parent.enter();
  }

  /**
   * A short title to be used as the label of the back button.
   */
  protected abstract String getShortTitle();

  /**
   * Remember the last PanelLabel that was selected on the current panel. This
   * is used to scroll the viewport down to the last selected panel when the
   * WallToWallPanel is backed into.
   */
  private void setLastSelectedLabel(PanelLabel label) {
    lastSelectedLabel = label;
  }
}
