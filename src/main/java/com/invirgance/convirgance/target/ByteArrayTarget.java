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
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * A Target that writes the data to a memory buffer. While this can be very
 * useful for test cases and quick wins, it is not recommended for production
 * code. Buffering large amounts of data in memory will significantly slow
 * down your application and put pressure on the Java Garbage Collector.
 * 
 * @author jbanes
 */
public class ByteArrayTarget implements Target
{
    private ByteArrayOutputStream out;
    
    /**
     * Get the written data as a byte array
     * 
     * @return the written data as a byte array. 
     */
    public byte[] getBytes()
    {
        return out.toByteArray();
    }
    
    /**
     * Returns the output stream to write to. This memory buffer can only be 
     * written to once. You will want to create a new instance of this Target
     * if you want to write another data set.
     * 
     * @return an output stream that writes to a memory buffer
     * @throws ConvirganceException If the target stream has already been used.
     */
    @Override
    public OutputStream getOutputStream()
    {
        if(out != null) throw new ConvirganceException("Target output stream has already been used");
        
        out = new ByteArrayOutputStream();
        
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
     * @return true if the underlying memory buffer has already been written to, false otherwise
     */
    @Override
    public boolean isUsed()
    {
        return (out != null);
    }
    
}
