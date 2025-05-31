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
 * Used when filtering data that should be equal to some criteria.
 * @author jbanes
 */
@Wiring
public class EqualsFilter extends ComparatorFilter
{
    /**
     * Creates a new EqualsFilter.
     */
    public EqualsFilter()
    {
        super();
    }

    /**
     * Creates a new EqualsFilter with the key to evaluate and its expected/comparison value.
     * @param key The key to evaluate for compared Objects.
     * @param value The comparison value.
     */
    public EqualsFilter(String key, Object value)
    {
        super(key, value);
    }
    
    /**
     * Evaluates the provided JSONObject, checking the key and its value to this Filters comparison value.
     * @param record The JSONObject to evaluate.
     * @return True if the record has the expected key and value.
     */
    @Override
    public boolean test(JSONObject record)
    {
        Object value = record.get(getKey());
        Object target = getValue();
        
        if(target == null) return (value == null);
        
        if(target instanceof ValueGenerator)
        {
            return getComparator().compare(value, ((ValueGenerator)target).generate(record)) == 0;
        }
        
        return getComparator().compare(value, getValue()) == 0;
    }
}
