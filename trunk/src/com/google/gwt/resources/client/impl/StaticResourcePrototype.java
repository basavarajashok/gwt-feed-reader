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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.resources.client.ResourceRequest;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * An implementation of ResourceBundle for server-side resources.
 */
public abstract class StaticResourcePrototype implements ResourcePrototype {
  /**
   * Encapsulates the state of the HTTP request to retrieve the resource's
   * content.
   */
  private class StaticResourceRequest implements RequestCallback,
      ResourceRequest {
    private final ResourceCallback callback;
    private Request request;

    public StaticResourceRequest(ResourceCallback callback) {
      this.callback = callback;
    }

    public void cancel() {
      assert request != null;
      request.cancel();
    }

    public boolean isPending() {
      assert request != null;
      return request.isPending();
    }

    public void onError(Request request, Throwable exception) {
      callback.onError(StaticResourcePrototype.this, exception);
    }

    public void onResponseReceived(Request request, Response response) {
      callback.onSuccess(StaticResourcePrototype.this, response.getText());
    }

    public void setRequest(Request req) {
      this.request = req;
    }
  }

  public ResourceRequest fetchContents(ResourceCallback callback) {
    RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, getUrl());
    StaticResourceRequest toReturn = new StaticResourceRequest(callback);

    try {
      Request req = rb.sendRequest("", toReturn);
      toReturn.setRequest(req);
    } catch (RequestException e) {
      callback.onError(this, e);
    }

    return toReturn;
  }
  
  public abstract String getName();

  public abstract String getUrl();

  public void setElementUrlProperty(Element elt, String propertyName) {
    DOM.setElementProperty(elt, propertyName, getUrl());
  }
  
  public String toString() {
    return "Static resource: " + getUrl();
  }
}
