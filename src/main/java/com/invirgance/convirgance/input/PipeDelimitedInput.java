/*
 * The MIT License
 *
 * Copyright 2025 Invirgance LLC.
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
package com.invirgance.convirgance.input;

import com.invirgance.convirgance.ConvirganceException;


/**
 * Provides support for reading pipe-delimited files as a stream of data.  
 * Convenience object for working with pipe-delimited files. Extends 
 * {@link DelimitedInput} and sets the delimiter character to the '|' character.
 * 
 * @author timur
 * @see DelimitedInput
 */
public class PipeDelimitedInput extends DelimitedInput
{   
    /**
     * Creates a new PipeDelimitedInput defaulting to UTF-8 text encoding
     */
    public PipeDelimitedInput()
    {
        this(null, "UTF-8");
    }
    
    /**
     * Creates a new PipeDelimitedInput with the provided column headers
     * 
     * @param columns The column headers.
     */
    public PipeDelimitedInput(String... columns)
    {
        this(columns, "UTF-8");
    }
    
    /**
     * Creates a new PipeDelimitedInput with custom text encoding
     * 
     * @param encoding The text encoding of the input content.
     */
    public PipeDelimitedInput(String encoding)
    {
        this(null, encoding);
    }
    
    /**
     * Creates a new PipeDelimitedInput with the provided column headers and custom text encoding
     * 
     * @param columns the column headers
     * @param encoding the text encoding of the input content
     */
    public PipeDelimitedInput(String[] columns, String encoding)
    {
        super(columns, encoding, '|');
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
        throw new ConvirganceException("Cannot set delimiter for PipeDelimitedInput class. Use DelimitedInput instead.");
    }   
}
