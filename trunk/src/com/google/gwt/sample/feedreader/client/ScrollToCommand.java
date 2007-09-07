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
import com.google.gwt.user.client.ui.Widget;

/**
 * This Command is used to ensure that a Widget will be on-screen, or at least
 * the title-bar scrolled offscreen.  It is used by {@link WallToWallPanel}.
 */
class ScrollToCommand implements Command {
  final int yPos;

  public ScrollToCommand(Widget w) {
    if (w == null) {
      yPos = 1;
    } else {
      yPos = DOM.getAbsoluteTop(w.getElement());
    }
  }

  public native void execute() /*-{
   $wnd.scrollTo(0, this.@com.google.gwt.sample.feedreader.client.ScrollToCommand::yPos);
   }-*/;
}