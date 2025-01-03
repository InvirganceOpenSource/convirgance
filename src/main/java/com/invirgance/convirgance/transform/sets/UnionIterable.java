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
package com.invirgance.convirgance.transform.sets;

import com.invirgance.convirgance.json.JSONObject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Combines multiple JSONObject sources (iterables) into a single iterable stream. Useful
 * for cases where cross-comparison, filtering, or transformation across
 * multiple data sources is required.
 *
 * @author jbanes
 */
public class UnionIterable implements Iterable<JSONObject>
{
    private List<Iterable<JSONObject>> streams;

    /**
     * Creates the UnionIterable with the provided streams.
     * @param streams The streams to merge into one.
     */
    public UnionIterable(Iterable<JSONObject>... streams)
    {
        this.streams = Arrays.asList(streams);
    }

    /**
     * Returns an iterator that iterates through all provided sources sequentially.
     * 
     * @return An iterator that contains the supplied streams.
     */
    @Override
    public Iterator<JSONObject> iterator()
    {
        final Iterable<JSONObject>[] streams = this.streams.toArray(Iterable[]::new);
        
        return new Iterator<JSONObject>() {
            
            private Iterator<JSONObject> iterator;
            private int index = 0;
            
            @Override
            public boolean hasNext()
            {
                while(index < streams.length && (iterator == null || !iterator.hasNext())) 
                {
                    iterator = streams[index++].iterator();
                }
                
                return (iterator != null && iterator.hasNext());
            }

            @Override
            public JSONObject next()
            {
                while(index < streams.length && (iterator == null || !iterator.hasNext())) 
                {
                    iterator = streams[index++].iterator();
                }
                
                return iterator.next();
            }
        };
    }
    
}
