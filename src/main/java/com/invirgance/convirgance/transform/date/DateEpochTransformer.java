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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Transforms the Date values of a JSONObject into long-formatted epoch timestamps.
 * 
 * <p>
 * This transformer scans JSON records and converts Date values into long numbers
 * representing the number of milliseconds since January 1st, 1970 midnight UTC
 * time. This format is compatible with {@link java.util.Date#getTime()} in both
 * Java and Javascript.</p>
 *
 * <p>Example usage:</p>
 * 
 * <pre>
 * Iterable&lt;JSONObject&gt; stream = ...;
 * 
 * // Convert specific fields
 * String[] included = {"created_at", "updated_at"};
 * DateEpochTransformer transformer = new DateEpochTransformer(included);
 * 
 * // Apply the transformation
 * stream = transformer.transform(stream);
 * </pre>
 * 
 * @author jbanes
 * @see java.util.Date#getTime()
 */
public class DateEpochTransformer implements IdentityTransformer
{
    private List<String> columns;
    
    /**
     * Creates a new DateEpochTransformer for converting any Date values found
     * into their epoch equivalents.
     */
    public DateEpochTransformer() 
    {
        super();
    }

    /**
     * Creates a new DateEpochTransformer for converting Date values to their
     * epoch equivalents.
     *
     * @param columns field names to include in the conversion
     */
    public DateEpochTransformer(String[] columns) 
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
    public void setColumns(String[] columns)
    {
        this.columns = Arrays.asList(columns);
    }
    
    @Override
    public JSONObject transform(JSONObject record) throws ConvirganceException
    {
        Iterable<String> iterable = columns != null ? columns : record.keySet();
        Object value;
        
        for(String key : iterable)
        {
            value = record.get(key);

            if(value != null && value instanceof Date)
            {
                record.put(key, ((Date)value).getTime());
            }
        }
        
        return record;
    }
}