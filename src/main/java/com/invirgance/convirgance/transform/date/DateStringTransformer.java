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
 * Transforms Date values of a JSONObject into their String representation.
 *
 * <p>
 * If specific fields are included, only those will be transformed. If specific fields are excluded, all others
 * will be transformed.
 * </p>
 *
 * <p>Example usage:</p>
 * 
 * <pre>
 * // Convert specific fields
 * String[] included = {"created_at", "updated_at"};
 * DateStringTransformer transformer = new DateStringTransformer(included, null);
 *
 * // Convert all fields except "excluded_field"
 * String[] excluded = {"excluded_field"};
 * DateStringTransformer transformer = new DateStringTransformer(null, excluded);
 * </pre>
 *
 * @author tadghh
 */
public class DateStringTransformer extends DateTime 
{
    
    /**
     * Creates a new DateStringTransformer for transforming JSONObject Date values into Strings.
     */
    public DateStringTransformer() 
    {
        super();
    }

    /**
     * Creates a new DateStringTransformer for transforming JSONObject Date values into Strings.
     *
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude during conversion. Null for none.
     */
    public DateStringTransformer(String[] included, String[] excluded) 
    {
        super(included, excluded);
    }

    /**
     * Transforms the Date object to a String in the ISO 8601 Standard.
     *
     * @param value The value to transform
     * @return The Date's string in the ISO 8601 Standard, or null.
     */
    @Override
    protected Object transformAction(Object value) 
    {
        return value instanceof Date ? ((Date) value).toInstant().toString() : null;
    }
}