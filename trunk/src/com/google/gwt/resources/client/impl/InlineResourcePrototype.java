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
package com.google.gwt.resources.client.impl;

import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.resources.client.ResourceRequest;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * An implementation of ResourcePrototype that can be used when the browser
 * supports data: URLs.
 */
public abstract class InlineResourcePrototype implements ResourcePrototype {
  /**
   * A no-op version of ResourceRequest.
   */
  private static class NullResourceRequest implements ResourceRequest {
    public void cancel() {
    }

    public boolean isPending() {
      return false;
    }
  }

  /**
   * The NullResourceRequest is stateless, so we'll use a singleton.
   */
  private static final NullResourceRequest NULL_REQUEST =
      new NullResourceRequest();
  
  public ResourceRequest fetchContents(ResourceCallback callback) {
    callback.onSuccess(this, getContents());
    return NULL_REQUEST;
  }

  public abstract String getName();

  public abstract String getUrl();

  public void setElementUrlProperty(Element elt, String propertyName) {
    DOM.setElementProperty(elt, propertyName, getUrl());
  }
  
  protected abstract String getContents();
}
