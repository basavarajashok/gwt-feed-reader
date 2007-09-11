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
package com.google.gwt.resources.client;

import com.google.gwt.user.client.Element;

/**
 * Encapsulates information about a particular resource.
 */
public interface ResourcePrototype {
  /**
   * Retrieve the contents of the resource. This is preferable to using
   * {@link #getUrl()} because browser-specific behavior may be implemented.
   * 
   * @param callback a ResourceCallback to invoke when the contents of the
   *          resource are available.
   */
  public ResourceRequest fetchContents(ResourceCallback callback);

  /**
   * Returns the name of the function within the ResourceBundle used to create
   * the ResourcePrototype.
   * 
   * @return the name of the function within the ResourceBundle used to create
   *         the ResourcePrototype
   */
  public String getName();

  /**
   * Retrieves a URL by which the contents of the resource can be obtained. This
   * will be an absolute URL.
   */
  public String getUrl();

  /**
   * Configures an Element's property to reference the contents of a resource.
   * This method is preferable to a combination of {@link #getUrl()} and
   * {@link com.google.gwt.user.client.DOM} because it may allow for better
   * browser-specific behavior.
   * 
   * @param elt the element to modify
   * @param propertyName the name of the property that should reference the
   *          contents of the resource
   */
  public void setElementUrlProperty(Element elt, String propertyName);
}
