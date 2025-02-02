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
import com.invirgance.convirgance.target.Target;
import java.util.Iterator;

/**
 * A cursor for manually writing data to a given {@link Output} format. Obtaining
 * a cursor from an Output is ideal when you are pulling or generating records
 * from disparate sources and need precise control over writing to the the
 * output stream. If you don't need manual control, consider calling 
 * {@link Output#write(Target, Iterable)} instead.
 * 
 * @author jbanes
 * @see Output#write(Target)
 */
public interface OutputCursor extends AutoCloseable
{
    /**
     * Write a single record to the output stream
     * 
     * @param record the {@link JSONObject} to write
     */
    public void write(JSONObject record);
    
    /**
     * Write a stream of data to the output stream. This call does not close
     * the cursor and can be called repeatedly as needed.
     * 
     * @param iterable a stream a data to write
     */
    default public void write(Iterable<JSONObject> iterable)
    {
        write(iterable.iterator());
    }
    
    /**
     * Write a stream of data to the output stream. This call does not close
     * the cursor and can be called repeatedly as needed.
     * 
     * @param iterator a stream a data to write
     */
    default public void write(Iterator<JSONObject> iterator)
    {
        while(iterator.hasNext()) write(iterator.next());
    }
}
