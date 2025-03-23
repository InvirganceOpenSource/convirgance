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
 * Transforms Date values in a JSONObject into ISO 8601 string format. (e.g. "2025-02-20T21:45:29.886Z")
 *
 * <p>
 * If specific fields are specified, only those fields will be transformed. If 
 * specific fields are excluded, all others will be transformed.
 * </p>
 *
 * <p>Example usage:</p>
 * 
 * <pre>
 * Iterable&lt;JSONObject&gt; stream = ...;
 * 
 * // Convert specific fields
 * DateStringTransformer transformer = new DateStringTransformer("created_at", "updated_at");
 * 
 * // Apply the transformation
 * stream = transformer.transform(stream);
 * </pre>
 *
 * @author jbanes
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
     * @param columns field names to include in the conversion
     */
    public DateISOStringTransformer(String... columns) 
    {
        this.columns = Arrays.asList(columns);
    }
    
    /**
     * Get the list of columns that will be transformed. Null is returned if no
     * column list has been set. In this case, all columns will be checked.
     * 
     * @return list of columns to be converted or null if all columns will be checked
     */
    public String[] getColumns()
    {
        return columns.toArray(String[]::new);
    }

    /**
     * Set the list of columns to transform. Null may be passed if all columns 
     * should be checked and transformed if they are of type {@link java.util.Date}.
     * 
     * @param columns list of columns to be converted
     */
    public void setColumns(String... columns)
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
                if(value instanceof java.sql.Date) record.put(key, value.toString());
                else record.put(key, formatter.format(value));
            }
        }
        
        return record;
    }
}