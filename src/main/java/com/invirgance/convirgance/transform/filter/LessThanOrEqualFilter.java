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
import com.invirgance.convirgance.wiring.annotation.Wiring;

/**
 * Filters JSONObjects where the value of the specified key is less than or equal to the provided comparison value.
 * @author jbanes
 */
@Wiring
public class LessThanOrEqualFilter extends ComparatorFilter
{
    /**
     * Creates a new LessThanOrEqualFilter.
     */
    public LessThanOrEqualFilter()
    {
        super();
    }

    /**
     * Creates a new LessThanOrEqualFilter for evaluating whether the value of a
     * specified key is less than or equal to the comparison value.
     *
     * @param key The key to evaluate.
     * @param value The comparison value.
     */
    public LessThanOrEqualFilter(String key, Object value)
    {
        super(key, value);
    }
    
    /**
     * Tests if the value of the specified key in the record is less than or equal to the comparison value.
     * @param record The record to evaluate.
     * @return True if the value of the record's specified key is less than or equal to the comparison value.
     */    
    @Override
    public boolean test(JSONObject record)
    {
        return getComparator().compare(record.get(getKey()), getValue()) <= 0;
    }
}
