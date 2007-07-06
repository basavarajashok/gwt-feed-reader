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
package com.google.gwt.sample.ajaxfeed.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
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
 * TODO.
 * 
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
  private final Label statusLabel = new Label();
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

    header.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
    header.addStyleName("header");
    if (parent != null) {
      header.add(new Button(parent.getShortTitle(), parentClickListener));
    }

    header.add(new Label(title));

    FlowPanel vp = new FlowPanel();
    vp.add(header);

    contents.addStyleName("contents");
    vp.add(contents);
    
    statusLabel.addStyleName("statusLabel");
    vp.add(statusLabel);
    
    permalink.addStyleName("permalink");
    permalink.setVisible(false);
    vp.add(permalink);

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

  public void setEditCommand(String label, String title, Command command) {
    editCommand = command;
    Button b = new Button(label, new ClickListener() {
      public void onClick(Widget w) {
        editCommand.execute();
      }
    });
    b.setTitle(title);
    header.add(b);
    header.setCellHorizontalAlignment(b, HorizontalPanel.ALIGN_RIGHT);
  }

  public void setHTML(String html) {
    HTML h;
    hasText = hasHtml = h = new HTML(html);

    contents.clear();
    contents.add(h);
  }
  
  public void setPermalink(String token) {
    if (token == null) {
      permalink.setVisible(false);
    } else {
      permalink.setText("Permalink");
      permalink.setTargetHistoryToken(token);
      permalink.setVisible(true);
    }
  }
  
  public void setStatus(String status) {
    statusLabel.setText(status);
  }

  public void setText(String text) {
    Label l;
    hasText = l = new Label(text);
    hasHtml = null;

    contents.clear();
    contents.add(l);
  }

  protected void enter() {
    if (activePanel != null) {
      RootPanel.get().remove(activePanel);
    }
    
    DeferredCommand.addPause();
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        activePanel = SliderPanel.this;
        RootPanel.get().add(SliderPanel.this, 0, 0);
      }
    });
  }

  protected void exit() {
    if (parent == null) {
      throw new RuntimeException("SliderPanel has no parent");
    }
    
    parent.enter();
  }

  protected abstract String getShortTitle();
}
