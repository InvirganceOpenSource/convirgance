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
package com.invirgance.convirgance.transform.date;

import java.util.Date;

/**
 * Transforms the String values of a JSONObject into a Date object.
 * Allows selective field inclusion and exclusion.
 * 
 * Note:
 *  - Only supports transforming from Strings (Dates) formatted as RFC 1123
 * 
 * @author tadghh
 */
public class StringDateTransformer extends DateTime 
{
    /**
     * Creates a transformer that creates Date objects from Strings.
     */
    public StringDateTransformer() 
    {
        super();
    }

    /**
     * Creates a new StringDateTransformer for transforming String values of JSONObjects to Dates.
     * 
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude during conversion. Null for none.
     */
    public StringDateTransformer(String[] included, String[] excluded) 
    {
        super(included, excluded);
    }

    /**
     * Transforms a String into a Date.
     *
     * @param value The value to transform
     * @return The Date or null.
     */
    @Override
    protected Object transformAction(Object value) 
    {
        if(value instanceof String) return new Date((String) value);
        
        return null;
    }
}
