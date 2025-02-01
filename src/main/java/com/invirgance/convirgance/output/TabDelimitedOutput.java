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
 * Support for writing tab delimited file format
 * @author timur
 */
public class TabDelimitedOutput extends DelimitedOutput
{
    /**
     * Creates a new TabeDelimitedOutput.
     */
    public TabDelimitedOutput()
    {
        super(null, '\t');
    }
    
    /**
     * Creates a TabDelimitedOuput with specified columns.
     * @param columns A String array of column names.
     */
    public TabDelimitedOutput(String[] columns)
    {
        super(columns, '\t');
    }
    
    /**
     * Overriding the setDelimiter method to throw an exception when usage is attempted.
     * @param delimiter The delimiter being set.
     * @throws ConvirganceException always as usage not allowed
     */
    @Override
    public void setDelimiter(char delimiter)
    {
        throw new ConvirganceException("Cannot set delimiter for TabDelimitedOutput class");
    }   
    
}
