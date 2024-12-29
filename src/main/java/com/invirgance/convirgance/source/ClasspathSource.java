/*
 * Copyright 2024 INVIRGANCE LLC

Permission is hereby granted, free of charge, to any person obtaining a copy 
of this software and associated documentation files (the “Software”), to deal 
in the Software without restriction, including without limitation the rights to 
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
of the Software, and to permit persons to whom the Software is furnished to do 
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
SOFTWARE.
 */
package com.invirgance.convirgance.source;

import java.io.InputStream;

/**
 * A Source implementation that loads resources from the classpath.
 * Resources are loaded relative to a specified class or the current class if none provided.
 * @author jbanes
 */
public class ClasspathSource implements Source
{
    private Class clazz;
    private String path;

    /**
     * Creates a ClasspathSource with the provided path.
     * @param path A path to a file.
     */
    public ClasspathSource(String path)
    {
        this(null, path);
    }

    /**
     * Creates a ClasspathSource with the provided Class, along with the provided file.
     * @param clazz The reference class for loading resources,, if the class is null ClasspathSource will be used.
     * @param path A path to a file.
     */
    public ClasspathSource(Class clazz, String path)
    {
        this.clazz = clazz == null ? getClass() : clazz;
        this.path = path;
    }
    
    @Override
    public InputStream getInputStream()
    {
        return clazz.getResourceAsStream(path);
    }
    
}
