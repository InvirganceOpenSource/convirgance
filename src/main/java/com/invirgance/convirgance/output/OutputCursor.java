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
package com.invirgance.convirgance.output;

import com.invirgance.convirgance.json.JSONObject;
import java.util.Iterator;

/**
 * Handles reliably writing out JSON records to an output destination. Supports writing
 * both individual records and collections, with implementations managing the specific 
 * details of the output destination (e.g. files, databases, network streams).
 * 
 * @author jbanes
 */
public interface OutputCursor extends AutoCloseable
{
    
    /**
     * Used to write a record to the stream.
     * @param record The {@link JSONObject} to write.
     */
    public void write(JSONObject record);
    
    /**
     * Used to write a 'collection' of {@link JSONObject} to the stream.
     * @param iterable The iterable of JSONObjects to write.
     */
    default public void write(Iterable<JSONObject> iterable)
    {
        write(iterable.iterator());
    }
    
    /**
     * Writes each {@link JSONObject} in the iterator to the source.
     * @param iterator The iterator of JSONObjects to write.
     */
    default public void write(Iterator<JSONObject> iterator)
    {
        while(iterator.hasNext()) write(iterator.next());
    }
}
