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

import com.invirgance.convirgance.ConvirganceException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides access to an underlying file resource
 * 
 * @author jbanes
 */
public class FileSource implements Source
{
    private final File file;

    /**
     * Creates a new FileSource based on the provided path.
     * 
     * @param path a path to the underlying file resource
     */
    public FileSource(String path)
    {
        this(new File(path));
    }
    
    /**
     * Creates a new FileSource based on the provided File.
     * 
     * @param file the underlying file resource
     */
    public FileSource(File file)
    {
        this.file = file;
    }

    /**
     * Returns the current file
     * 
     * @return the file resource managed by this source
     */
    public File getFile()
    {
        return file;
    }
    
    /**
     * Returns an InputStream to access the underlying file resource
     * @return an input stream for the file resource
     * @throws ConvirganceException if the file does not exist or the file is inaccessible
     */
    @Override
    public InputStream getInputStream()
    {
        try
        {
            return new FileInputStream(file);
        }
        catch(IOException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
}
