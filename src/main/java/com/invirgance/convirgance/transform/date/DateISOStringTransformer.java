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

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.transform.IdentityTransformer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
public class DateISOStringTransformer implements IdentityTransformer
{
    private List<String> columns;
    private SimpleDateFormat formatter;
    
    /**
     * Creates a new DateStringTransformer for transforming JSONObject Date values into Strings.
     */
    public DateISOStringTransformer() 
    {
        super();
    }

    /**
     * Creates a new DateStringTransformer for transforming JSONObject Date values into Strings.
     *
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude during conversion. Null for none.
     */
    public DateISOStringTransformer(String[] columns) 
    {
        this.columns = Arrays.asList(columns);
    }

    private SimpleDateFormat getFormatter()
    {
        if(this.formatter != null) return this.formatter;
        
        this.formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        
        this.formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        return this.formatter;
    }
    
    @Override
    public JSONObject transform(JSONObject record) throws ConvirganceException
    {
        Iterable<String> iterable = columns != null ? columns : record.keySet();
        SimpleDateFormat formatter = getFormatter();
        Object value;
        
        for(String key : iterable)
        {
            value = record.get(key);

            if(value != null && value instanceof Date)
            {
                record.put(key, formatter.format(value));
            }
        }
        
        return record;
    }
}