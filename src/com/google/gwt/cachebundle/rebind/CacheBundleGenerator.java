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

import com.google.gwt.cachebundle.client.CacheBundle;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.util.Util;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Copies selected files into module output with strong names and generates the
 * CacheBundle mappings.
 */
public class CacheBundleGenerator extends Generator {
  private static final String METADATA_TAG = "gwt.resource";

  public String generate(TreeLogger logger, GeneratorContext context,
      String typeName) throws UnableToCompleteException {

    // The TypeOracle knows about all types in the type system
    final TypeOracle typeOracle = context.getTypeOracle();

    // Get a reference to the type that the generator should implement
    final JClassType sourceType = typeOracle.findType(typeName);

    // Ensure that the requested type exists
    if (sourceType == null) {
      logger.log(TreeLogger.ERROR, "Could not find requested typeName", null);
      throw new UnableToCompleteException();
    }

    // Pick a name for the generated class to not conflict. Enclosing class
    // names must be preserved.
    final String generatedSimpleSourceName =
        generateSimpleSourceName(sourceType.getName());

    // Begin writing the generated source.
    final ClassSourceFileComposerFactory f =
        new ClassSourceFileComposerFactory(sourceType.getPackage().getName(),
            generatedSimpleSourceName);

    f.addImport(CacheBundle.class.getName());

    // Determine the interface to implement
    if (sourceType.isInterface() != null) {
      f.addImplementedInterface(sourceType.getQualifiedSourceName());

    } else {
      // The incoming type wasn't a plain interface, we don't support
      // abstract base classes
      logger.log(TreeLogger.ERROR, "Requested JClassType is not an interface.",
          null);
      throw new UnableToCompleteException();
    }

    // All source gets written through this Writer
    final PrintWriter out =
        context.tryCreate(logger, sourceType.getPackage().getName(),
            generatedSimpleSourceName);

    // If an implementation already exists, we don't need to do any work
    if (out != null) {

      // We really use a SourceWriter since it's convenient
      final SourceWriter sw = f.createSourceWriter(context, out);

      JMethod[] methods = sourceType.getMethods();

      for (int i = 0; i < methods.length; i++) {
        JMethod m = methods[i];

        sw.print(m.getReadableDeclaration(false, true, true, true, true));
        sw.println("{");
        sw.indent();

        URL resourceUrl = getResourceUrlFromMetaData(logger, m);
        String resourceName = addToOutput(logger, context, resourceUrl);

        sw.println("return \"" + resourceName + "\";");

        sw.outdent();
        sw.println("}");
      }

      // Write the generated code to disk
      sw.commit(logger);
    }

    // Return the name of the concrete class
    return f.getCreatedClassName();
  }

  /**
   * Add a CacheBundle-referenced file to the module's output. The extension of
   * the resource will be preserved to ensure mime-types are correct.
   * 
   * @return The name of the resource in the module's output.
   */
  protected String addToOutput(TreeLogger logger, GeneratorContext context,
      URL resource) throws UnableToCompleteException {
    byte[] bytes = Util.readURLAsBytes(resource);

    String strongName = Util.computeStrongName(bytes);

    String fileName = resource.getPath();
    String extension = "";
    int lastIdx = fileName.lastIndexOf('.');
    if (lastIdx != -1) {
      extension = "." + fileName.substring(lastIdx + 1);
    }
    String outputName = strongName + ".cache" + extension;
    // Ask the context for an OutputStream into the named resource
    OutputStream out = context.tryCreateResource(logger, outputName);

    // This would be null if the resource has already been created in the output
    // (because two or more files had identical content).
    if (out != null) {
      try {
        InputStream in = resource.openStream();
        Util.copy(logger, in, out);
        context.commitResource(logger, out);
      } catch (IOException e) {
        logger.log(TreeLogger.ERROR, "Unable to open resource "
            + resource.toExternalForm(), e);
        throw new UnableToCompleteException();
      }
    }

    logger.log(TreeLogger.DEBUG, "Copied " + resource.toExternalForm() + " to "
        + outputName, null);
    return outputName;
  }

  /**
   * Given a user-define type name, determine the type name for the generated class.
   */
  protected String generateSimpleSourceName(String sourceType) {
    return "__" + sourceType.replaceAll("\\.", "__") + "Impl";
  }

  // Assume this is only called for valid methods. This is a cut-down version
  // of the logic found in ImageBundleBuilder
  private URL getResourceUrlFromMetaData(TreeLogger logger, JMethod method)
      throws UnableToCompleteException {

    String[][] md = method.getMetaData(METADATA_TAG);

    if (md.length != 1) {
      logger.log(TreeLogger.ERROR, "Method " + method.getName()
          + " does not have a @" + METADATA_TAG + " annotation", null);
      throw new UnableToCompleteException();
    }

    // Metadata is available, so get the resource url from the metadata
    int lastTagIndex = md.length - 1;
    int lastValueIndex = md[lastTagIndex].length - 1;
    String resourceNameFromMetaData = md[lastTagIndex][lastValueIndex];

    // Make sure the name is either absolute or package-relative.
    if (resourceNameFromMetaData.indexOf("/") == -1) {
      String pkgName = method.getEnclosingType().getPackage().getName();
      // This construction handles the default package correctly, too.
      resourceNameFromMetaData =
          pkgName.replace('.', '/') + "/" + resourceNameFromMetaData;
    }

    // Make sure that the resource exists on the classpath. In the future,
    // this code will have to be changed if resources are loaded from the
    // source path or public path.
    URL resourceURL =
        getClass().getClassLoader().getResource(resourceNameFromMetaData);
    if (resourceURL == null) {
      logger.log(TreeLogger.ERROR, "Resource " + resourceNameFromMetaData
          + " not found on classpath. "
          + "Is the name specified as Class.getResource() would expect?", null);
      throw new UnableToCompleteException();
    }

    return resourceURL;
  }
}
