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
package com.invirgance.convirgance.target;

import com.invirgance.convirgance.ConvirganceException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides write access to an underlying file resource. The file will be created
 * on write if it doesn't exist. If the file already exists it will be overwritten
 * every time an OutputStream is written to.
 * 
 * @author jbanes
 */
public class FileTarget implements Target
{
    private File file;

    /**
     * Creates a FileTarget based on the provided path
     * 
     * @param path a path to the underlying file resource
     */
    public FileTarget(String path)
    {
        this(new File(path));
    }

    /**
     * Creates a FileTarget based on the provided File
     * 
     * @param file the underlying file resource to write to
     */
    public FileTarget(File file)
    {
        this.file = file;
    }

    /**
     * Returns the underlying file resource to write to
     * 
     * @return the target file
     */
    public File getFile()
    {
        return file;
    }
    
    /**
     * Returns an OutputStream to write to the underlying file resource. If the
     * file does not exist it will be created. If the file already exists it
     * will be overwritten with each call to this method.
     * 
     * @return the output stream
     * @throws ConvirganceException if the file is inaccessible
     */
    @Override
    public OutputStream getOutputStream()
    {
        try
        {
            return new FileOutputStream(file);
        }
        catch(IOException e) { throw new ConvirganceException(e); }
    }
    
}
