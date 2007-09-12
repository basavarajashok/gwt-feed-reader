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
package com.google.gwt.sample.feedreader.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.libideas.client.DataResource;
import com.google.gwt.libideas.client.ImmutableResourceBundle;
import com.google.gwt.libideas.client.TextResource;

/**
 * A ResourceBundle that holds the references to the CSS resources we wish to
 * use with {@link com.google.gwt.widgetideas.client.StyleInjector}.
 */
public interface Resources extends ImmutableResourceBundle {
  public static final Resources INSTANCE =
      (Resources) GWT.create(Resources.class);

  /**
   * @gwt.resource iPhoneBackButton.png
   */
  public DataResource backButton();

  /**
   * @gwt.resource pinstripes.png
   */
  public DataResource background();

  /**
   * @gwt.resource iPhoneButton.png
   */
  public DataResource button();

  /**
   * @gwt.resource GwtFeedReader.css
   */
  public TextResource css();

  /**
   * @gwt.resource iPhoneArrow.png
   */
  public DataResource enter();

  /**
   * @gwt.resource gwtLogo.png
   */
  public DataResource logo();

  /**
   * @gwt.resource spinner.gif
   */
  public DataResource spinner();

  /**
   * @gwt.resource iPhoneToolbar.png
   */
  public DataResource toolbar();
}
