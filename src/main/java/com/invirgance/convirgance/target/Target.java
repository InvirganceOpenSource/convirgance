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

import java.io.OutputStream;

/**
 * Provides a safe way to write out content to a Object implementing Target.
 * 
 * Used when needing to write data for file operations, network transfers.
 * @author jbanes
 */
public interface Target
{  
    /**
     * Gets the output stream to write to.
     * @return The {@link OutputStream}.
     */
    public OutputStream getOutputStream();
    
    /**
     * Returns if the output stream for the target can be reused, default true.
     * @return true.
     */
    default public boolean isReusable()
    {
        return true;
    }
    
    /**
     * Returns if the output stream for the target has been used, default false.
     * @return false.
     */
    default public boolean isUsed()
    {
        return false;
    }
}
