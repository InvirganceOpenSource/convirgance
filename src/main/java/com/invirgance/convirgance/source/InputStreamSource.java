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
import java.io.InputStream;

/**
 * A Source implementation that wraps an InputStream and provides one-time access to it.
 * @author jbanes
 */
public class InputStreamSource implements Source
{
    private InputStream in;
    private boolean used;

    /**
     * Creates a InputStreamSource based on the provided InputStream.
     * @param in The InputStream to use.
     */
    public InputStreamSource(InputStream in)
    {
        this.in = in;
    }

    /**
     * Gets the InputStream.
     * @return The stream.
     * @throws ConvirganceException If attempting to reuse the stream.
     */
    @Override
    public InputStream getInputStream()
    {
        if(used) throw new ConvirganceException("Attempted to reuse input stream. Construct with a File if more than one pass is needed.");
        
        this.used = true;
        
        return this.in;
    }
    
    /**
     * Streams from sources of this type are not reusable.
     * @return false.
     */
    @Override
    public boolean isReusable()
    {
        return false;
    }
    
    /**
     * If this InputStream has been used.
     * @return A Boolean.
     */
    @Override
    public boolean isUsed()
    {
        return this.used;
    }
}
