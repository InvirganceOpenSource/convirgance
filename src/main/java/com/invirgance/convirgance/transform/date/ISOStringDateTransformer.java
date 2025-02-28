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
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Transforms ISO 8601 formatted strings into Date objects.
 *
 * <p>Example usage:</p>
 * <pre>
 * Iterable&lt;JSONObject&gt; stream = ...;
 * 
 * // Convert specified fields
 * StringDateTransformer transformer = new StringDateTransformer("created_at", "updated_at");
 *
 * // Apply the transformation
 * stream = transformer.transform(stream);
 * </pre>
 * 
 * @author jbanes
 */
public class ISOStringDateTransformer implements IdentityTransformer
{
    private List<String> columns;
    private SimpleDateFormat formatter;
    
    /**
     * Prepares a new transformer to parse ISO 8601 strings into Date objects. You
     * must call {@link #setColumns(java.lang.String...)} before attempting a
     * transform or an exception will be thrown.
     */
    public ISOStringDateTransformer() 
    {
        super();
    }

    /**
     * Creates a new transformer that parses ISO 8601 strings into Date objects
     * 
     * @param columns field names to include in the conversion
     */
    public ISOStringDateTransformer(String... columns) 
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
    
    @Override
    public JSONObject transform(JSONObject record) throws ConvirganceException
    {
        Instant instant;
        String value;

        if(columns == null) throw new ConvirganceException("The list of columns to transform must be set!");

        for(String key : columns)
        {
            value = record.getString(key);

            if(value == null) continue;
            
            instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value));

            record.put(key, Date.from(instant));
        }
        
        return record;
    }
}
