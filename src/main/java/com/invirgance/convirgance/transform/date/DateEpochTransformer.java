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
 * Transforms the Date values of a JSONObject to their epoch timestamp.
 * Allows selective field inclusion and exclusion.
 * 
 * <p>
 * This transformer scans JSON records and converts Date values into their
 * corresponding epoch timestamps (milliseconds since Unix epoch). If specific
 * fields are included, only those will be transformed. If specific fields are
 * excluded, all others will be transformed.</p>
 *
 * <p>Example usage:</p>
 * 
 * <pre>
 * // Convert specific fields
 * String[] included = {"created_at", "updated_at"};
 * DateEpochTransformer transformer = new DateEpochTransformer(included, null);
 *
 * // Convert all fields except "excluded_field"
 * String[] excluded = {"excluded_field"};
 * DateEpochTransformer transformer = new DateEpochTransformer(null, excluded);
 * </pre>
 *
 * @author tadghh
 */
public class DateEpochTransformer extends DateTime 
{
    
    /**
     * Creates a new DateEpochTransformer for converting Date values to their
     * epoch equivalents.
     */
    public DateEpochTransformer() 
    {
        super();
    }

    /**
     * Creates a new DateEpochTransformer for converting Date values to their
     * epoch equivalents.
     *
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude during conversion. Null for none.
     */
    public DateEpochTransformer(String[] included, String[] excluded) 
    {
        super(included, excluded);
    }

    /**
     * Transforms a Date to its epoch timestamp.
     *
     * @param value The value to transform
     * @return The epoch timestamp for the Date. Or null.
     */
    @Override
    protected Object transformAction(Object value) 
    {
        if(value instanceof Date) return ((Date) value).getTime();

        return null;
    }
}