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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * A bundle for the images used in the application.
 */
public interface Images extends ImageBundle {
  public static final Images INSTANCE = (Images) GWT.create(Images.class);
  
  /**
   * @gwt.resource iPhoneArrow.png
   */
  public AbstractImagePrototype enter();

  /**
   * @gwt.resource gwtLogo.png
   */
  public AbstractImagePrototype logo();
  
  /**
   * @gwt.resource newwindow.png
   */
  public AbstractImagePrototype popout();
}
