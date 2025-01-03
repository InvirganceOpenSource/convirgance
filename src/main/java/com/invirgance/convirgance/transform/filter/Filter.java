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
package com.invirgance.convirgance.transform.filter;

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.transform.Transformer;
import java.util.Iterator;

/**
 * Defines a filter that evaluates each record in a collection against a
 * condition. Records that pass the condition are returned in the transformed
 * iterator.
 *
 * @author jbanes
 */
public interface Filter extends Transformer
{
    @Override
    public default Iterator<JSONObject> transform(Iterator<JSONObject> iterator)
    {
        return new Iterator<JSONObject>() {
            
            private JSONObject next;
            
            /**
             * Checks if there is a next record that passes the filter
             * condition. Iterates over the source iterator and finds the next
             * record that passes the filter.
             *
             * @return `true` if a valid record is found, `false` otherwise.
             */
            @Override
            public boolean hasNext()
            {
                JSONObject next = this.next;
                
                if(next != null) return true;
                
                while(iterator.hasNext() && this.next == null)
                {
                    next = iterator.next();
                    
                    if(test(next)) this.next = next;
                }
                
                return (this.next != null);
            }

            /**
             * Returns the next valid record that passes the filter condition.
             * Calls `hasNext()` to ensure the record is valid before returning.
             *
             * @return The next valid JSONObject.
             */
            @Override
            public JSONObject next()
            {
                JSONObject next = this.next;
                
                if(next == null) 
                {
                    hasNext();
                    next = this.next;
                }
                
                this.next = null;
                
                return next;
            }
        };
    }
    
    /**
     * Tests if the given record meets the filter condition.
     *
     * @param record The JSONObject to test.
     * @return If the record passed or failed.
     */
    public boolean test(JSONObject record);
}
