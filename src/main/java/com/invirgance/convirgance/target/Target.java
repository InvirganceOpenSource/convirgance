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
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstracts away access to an on-demand OutputStream over a given resource. Most 
 * implementations are able to provide repeated access to the underlying resource.
 * However, not all resources can be reused and multiple calls to 
 * {@link #getOutputStream()} will fail if {@link #isReusable()} returns false.
 * 
 * @author jbanes
 */
public interface Target
{  
    /**
     * Returns an {@link OutputStream} over the underlying resource
     * 
     * @return an OutputStream
     */
    public OutputStream getOutputStream();
    
    /**
     * Returns true if the target is reusable. The default implementation returns
     * true, so this must be overridden if your implementation is not reusable.
     * 
     * @return true
     */
    default public boolean isReusable()
    {
        return true;
    }
    
    /**
     * Returns true if the OutputStream has already been accessed and another attempt
     * to access {@link #getOutputStream()} will fail. The default implementation returns
     * false, so this must be overridden if your implementation is not reusable.
     * 
     * @return false
     */
    default public boolean isUsed()
    {
        return false;
    }
    
    /**
     * Convenience method for writing string data to a file in UTF-8 format. This 
     * is useful for quickly serializing in-memory data to a file, typically 
     * generated information that is of use to a user. 
     * 
     * @param data the string to write
     */
    default public void writeString(String data)
    {
        writeString(data, "UTF-8");
    }
    
    /**
     * Convenience method for writing string data to a file. This is useful for quickly
     * serializing in-memory data to a file, typically generated information that
     * is of use to a user. 
     * 
     * @param data the string to write
     * @param encoding the desired character encoding
     */
    default public void writeString(String data, String encoding)
    {
        try(OutputStream out = getOutputStream())
        {
            out.write(data.getBytes(encoding));
        }
        catch(IOException e)
        {
            throw new ConvirganceException(e);
        }
    }
    
    /**
     * Convenience method for writing data to a file. This is useful for quickly
     * serializing in-memory data to a file, typically generated information that
     * is of use to a user. 
     * 
     * @param data a byte array of the data to write
     */
    default public void write(byte[] data)
    {
        try(OutputStream out = getOutputStream())
        {
            out.write(data);
        }
        catch(IOException e)
        {
            throw new ConvirganceException(e);
        }
    }
}
