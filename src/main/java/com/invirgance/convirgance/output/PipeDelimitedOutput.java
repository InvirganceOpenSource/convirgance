/*
 * The MIT License
 *
 * Copyright 2025 INVIRGANCE LLC.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.invirgance.convirgance.output;

import com.invirgance.convirgance.ConvirganceException;

/**
 * Provides support for writing pipe-delimited files as a stream of data.  
 * Convenience object for working with pipe-delimited files. Extends 
 * {@link DelimitedOutput} and sets the delimiter character to the '|' character.
 * 
 * @author timur
 */
public class PipeDelimitedOutput extends DelimitedOutput
{
    /**
     * Creates a new PipeDelimitedOutput defaulting to UTF-8 text encoding
     */
    public PipeDelimitedOutput()
    {
        super(null, '|');
    }
    
    /**
     * Creates a PipeDelimitedOuput with specified columns
     * 
     * @param columns an array of column names
     */
    public PipeDelimitedOutput(String[] columns)
    {
        super(columns, '|');
    }
    
    /**
     * Throws an exception when called to prevent the delimiter being
     * changed from '|'.
     * 
     * @param delimiter the new delimiter to set
     * @throws ConvirganceException thrown always to prevent the delimiter from being changed
     */
    @Override
    public void setDelimiter(char delimiter)
    {
        throw new ConvirganceException("Cannot set delimiter for PipeDelimitedOutput class");
    }   
    
}
