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
import java.io.OutputStream;

/**
 * A Target implementation that wraps an OutputStream and provides one-time access to it
 * @author jbanes
 */
public class OutputStreamTarget implements Target
{
    private OutputStream out;
    private boolean used;

    /**
     * Creates a OutputStreamTarget from a provided OutputStream
     * 
     * @param out the OutputStream to wrap
     */
    public OutputStreamTarget(OutputStream out)
    {
        this.out = out;
    }
    
    /**
     * Gets the underlying OutputStream
     * 
     * @return the stream
     * @throws ConvirganceException if attempting to reuse the stream
     */
    @Override
    public OutputStream getOutputStream()
    {
        if(used) throw new ConvirganceException("Attempted to reuse an output stream");

        used = true;
        
        return out;
    }

    /**
     * Streams from targets of this type are not reusable
     * 
     * @return false
     */
    @Override
    public boolean isReusable()
    {
        return false;
    }

    /**
     * If this OutputStream has been used
     * 
     * @return true if the underlying OutputStream has already been used, false otherwise
     */
    @Override
    public boolean isUsed()
    {
        return used;
    }
}
