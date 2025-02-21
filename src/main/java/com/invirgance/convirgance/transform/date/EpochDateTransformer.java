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

/**
 * Transforms Epoch millisecond timestamp values of a JSONObject into Date objects.
 * 
 * <p>This transformer scans JSONObjects and transforms Epoch millisecond timestamps 
 * into their corresponding Date. If specific fields are included, only those fields will be transformed. 
 * If specific fields are excluded, all other fields will be transformed.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // Convert specific fields
 * String[] included = {"created_at", "updated_at"};
 * EpochDateTransformer transformer = new EpochDateTransformer(included, null);
 *
 * // Convert all fields except "excluded_field"
 * String[] excluded = {"excluded_field"};
 * EpochDateTransformer transformer = new EpochDateTransformer(null, excluded);
 * </pre>
 *
 * @author tadghh
 */
public class EpochDateTransformer extends DateTime 
{

    /**
     * Creates a new EpochDateTransformer for transforming the epoch values of a JSONObject to Dates.
     */
    public EpochDateTransformer() 
    {
        super();
    }

    /**
     * Creates a new EpochDateTransformer for transforming the epoch values of a JSONObject to Dates.
     * 
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude during conversion. Null for none.
     */
    public EpochDateTransformer(String[] included, String[] excluded) 
    {
        super(included, excluded);
    }

    /**
     * Takes an Epoch millisecond timestamp transforming it into a Date.
     *
     * @param value The value to transform
     * @return The Date or null.
     */
    @Override
    protected Object transformAction(Object value) 
    {
        return (value instanceof Long) ? parser.toDate((Long) value) : null;
    }

}