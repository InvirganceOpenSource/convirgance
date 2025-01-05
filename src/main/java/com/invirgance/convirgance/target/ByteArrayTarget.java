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
 * A temporary, single-use byte array buffer for data processing.
 * Once the output stream is retrieved, it cannot be accessed again, 
 * ensuring content is not overwritten and remains unchanged.
 * 
 * @author jbanes
 */
public class ByteArrayTarget implements Target
{
    private ByteArrayOutputStream out;
    
    /**
     * Gets the current output stream contents as a byte array.
     * @return The output stream as a byte array. 
     */
    public byte[] getBytes()
    {
        return out.toByteArray();
    }
    
    /**
     * Returns the output stream to write to.
     * @return The stream.
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
     * Streams of this type are not reusable.
     * @return false.
     */
    @Override
    public boolean isReusable()
    {
        return false;
    }

    @Override
    public boolean isUsed()
    {
        return (out != null);
    }
    
}
