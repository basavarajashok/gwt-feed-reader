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
import com.google.gwt.resources.client.ResourceBundle;
import com.google.gwt.resources.client.ResourcePrototype;

/**
 * A CacheBundle that allows random extra files to be given the royal cache
 * treatment.
 * 
 */
public interface Resources extends ResourceBundle {
  public static final Resources INSTANCE =
      (Resources) GWT.create(Resources.class);

  /**
   * @gwt.resource iPhoneBackButton.png
   */
  public ResourcePrototype backButton();

  /**
   * @gwt.resource pinstripes.png
   */
  public ResourcePrototype background();

  /**
   * @gwt.resource iPhoneButton.png
   */
  public ResourcePrototype button();

  /**
   * @gwt.resource GwtFeedReader.css
   */
  public ResourcePrototype css();

  /**
   * @gwt.resource iPhoneArrow.png
   */
  public ResourcePrototype enter();

  /**
   * @gwt.resource gwtLogo.png
   */
  public ResourcePrototype logo();
  
  /**
   * @gwt.resource spinner.gif
   */
  public ResourcePrototype spinner();
  
  /**
   * @gwt.resource iPhoneToolbar.png
   */
  public ResourcePrototype toolbar();
}
