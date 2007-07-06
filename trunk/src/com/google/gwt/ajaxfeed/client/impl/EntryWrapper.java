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
package com.google.gwt.ajaxfeed.client.impl;

import com.google.gwt.jsio.client.JSList;
import com.google.gwt.jsio.client.JSWrapper;

/**
 * @gwt.beanProperties
 */
public interface EntryWrapper extends JSWrapper {
  /**
   * @gwt.typeArgs <java.lang.String>
   */
  public JSList getCategories();
  public String getContent();
  public String getContentSnippet();
  public String getLink();
  public String getPublishedDate();
  public String getTitle();
}
