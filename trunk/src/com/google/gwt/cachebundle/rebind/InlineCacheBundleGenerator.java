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
package com.google.gwt.cachebundle.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.util.Util;

import java.io.IOException;
import java.net.URL;

/**
 * This is a refinement that will use data urls for browsers that support them.
 * Only files whose size are smaller than MAX_INLINE_SIZE will be inlined.
 * Larger files will use the standard CacheBundle behavior.
 */
public class InlineCacheBundleGenerator extends CacheBundleGenerator {
  /**
   * The largest file size that will be inlined. Note that this value is taken
   * before any encodings are applied.
   */
  private static final int MAX_INLINE_SIZE = 8 * 1024;

  /**
   * Add a CacheBundle-referenced file to the module's output. The extension of
   * the resource will be preserved to ensure mime-types are correct.
   * 
   * @return The name of the resource in the module's output.
   */
  protected String addToOutput(TreeLogger logger, GeneratorContext context,
      URL resource) throws UnableToCompleteException {
    byte[] bytes = Util.readURLAsBytes(resource);

    if (bytes.length < MAX_INLINE_SIZE) {
      // This is bad, but I am lazy and don't want to write _another_ encoder
      sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
      try {
        logger.log(TreeLogger.DEBUG, "Inlining " + resource.getFile(), null);
        return "data:" + resource.openConnection().getContentType()
            + ";base64," + enc.encode(bytes).replaceAll("\\s+", "");
      } catch (IOException e) {
        logger.log(TreeLogger.ERROR, "Unable to open resource "
            + resource.toExternalForm(), e);
        throw new UnableToCompleteException();
      }
    } else {
      return super.addToOutput(logger, context, resource);
    }
  }

  protected String generateSimpleSourceName(String sourceType) {
    return "__" + sourceType.replaceAll("\\.", "__") + "InlineImpl";
  }
}
