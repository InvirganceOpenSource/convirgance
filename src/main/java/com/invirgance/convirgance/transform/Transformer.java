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
package com.invirgance.convirgance.transform;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import java.util.Iterator;

/**
 * This interface provides a way to apply on the fly data transformations to JSONObjects. 
 * Use to clean, filter, or enrich data lazily during iteration.
 * @author jbanes
 */
public interface Transformer
{
    /**
     * Lazily transforms a collection of JSON objects.
     *
     * @param iterable The source data.
     * @return A lazily-transformed iterable.
     * @throws ConvirganceException If a transformation error occurs.
     */
    public default Iterable<JSONObject> transform(final Iterable<JSONObject> iterable) throws ConvirganceException
    {
        return new Iterable<JSONObject>() {
            
            @Override
            public Iterator<JSONObject> iterator()
            {
                return transform(iterable.iterator());
            }
        };
    }
    
    /**
     * Returns an iterator that applies transformations to each element.
     *
     * @param iterator The source data iterator.
     * @return A transformed iterator.
     */
    public Iterator<JSONObject> transform(Iterator<JSONObject> iterator);
}
