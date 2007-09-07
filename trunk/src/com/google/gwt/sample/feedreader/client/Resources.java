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

import com.google.gwt.cachebundle.client.CacheBundle;
import com.google.gwt.core.client.GWT;

/**
 * A CacheBundle that allows random extra files to be given the royal cache
 * treatment.
 * 
 */
public interface Resources extends CacheBundle {
  public static final Resources INSTANCE =
      (Resources) GWT.create(Resources.class);

  /**
   * @gwt.resource iPhoneBackButton.png
   */
  public String backButton();

  /**
   * @gwt.resource pinstripes.png
   */
  public String background();

  /**
   * @gwt.resource iPhoneButton.png
   */
  public String button();

  /**
   * @gwt.resource GwtFeedReader.css
   */
  public String css();

  /**
   * @gwt.resource iPhoneArrow.png
   */
  public String enter();

  /**
   * @gwt.resource gwtLogo.png
   */
  public String logo();
  
  /**
   * @gwt.resource spinner.gif
   */
  public String spinner();
  
  /**
   * @gwt.resource iPhoneToolbar.png
   */
  public String toolbar();
}
