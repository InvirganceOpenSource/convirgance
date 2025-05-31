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
import com.invirgance.convirgance.transform.ValueGenerator;
import com.invirgance.convirgance.wiring.annotation.Wiring;

/**
 * Used to check if a JSONObjects key has a value greater than the provided 
 * minimum value. Supports {@link ValueGenerator} as a value.
 * 
 * @author jbanes
 */
@Wiring
public class GreaterThanFilter extends ComparatorFilter
{
    
    /**
     * Creates a new GreaterThanFilter.
     */
    public GreaterThanFilter()
    {
        super();
    }

    /**
     * Creates a new Comparator to check the expected key against the minimum value.
     * @param key The key to check.
     * @param value The minimum value.
     */
    public GreaterThanFilter(String key, Object value)
    {
        super(key, value);
    }
    
    /**
     * Tests the record to see if the expected key has a value greater than the comparison value.
     * @param record The record to test.
     * @return True if the record's key has a value greater than the comparison value.
     */
    @Override
    public boolean test(JSONObject record)
    {
        Object value = getValue();
        
        if(value instanceof ValueGenerator) value = ((ValueGenerator)value).generate(record);
        
        return getComparator().compare(record.get(getKey()), value) > 0;
    }
    
}
