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
package com.invirgance.convirgance.input;

import com.invirgance.convirgance.source.Source;
import com.invirgance.convirgance.json.JSONObject;

/**
 * Interface for reading file formats as an Iterable stream of data
 * 
 * @author jbanes
 * @param <T> the record type being read. This will be {@link JSONObject} in most cases.
 */
public interface Input<T>
{
    /**
     * Reads data from the specified source and returns an {@link InputCursor} 
     * for iterating over the data.
     * 
     * @param source an input stream to read from.
     * @return an {@link InputCursor} to iterate over the stream
     */
    public InputCursor<T> read(Source source);
 
}
