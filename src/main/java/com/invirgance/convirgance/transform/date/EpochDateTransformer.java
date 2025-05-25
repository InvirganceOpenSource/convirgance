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
import com.invirgance.convirgance.wiring.annotation.Wiring;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Transforms long numbers representing a millisecond timestamp into Date objects. 
 * 
 * <p>This transformer transforms Epoch millisecond timestamps into their 
 * corresponding Date following the format of {@link java.util.Date#getTime()} in
 * both Java and JavaScript.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * Iterable&lt;JSONObject&gt; stream = ...;
 * 
 * // Convert specified fields
 * EpochDateTransformer transformer = new EpochDateTransformer("created_at", "updated_at");
 * 
 * // Apply the transformation
 * stream = transformer.transform(stream);
 * </pre>
 *
 * @author jbanes
 * @see java.util.Date#getTime()
 */
@Wiring
public class EpochDateTransformer implements IdentityTransformer
{
    private List<String> columns;
    
    /**
     * Prepares a new EpochDateTransformer for transforming epoch values 
     * to Dates. Note that {@link #setColumns(java.lang.String...)} must be called
     * or the transformation will throw an exception when invoked.
     */
    public EpochDateTransformer() 
    {
        super();
    }

    /**
     * Creates a new EpochDateTransformer for transforming the epoch values of a JSONObject to Dates.
     * 
     * @param columns field names to include in the conversion
     */
    public EpochDateTransformer(String... columns) 
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
        if(columns == null) throw new ConvirganceException("The list of columns to transform must be set!");

        for(String key : columns)
        {
            if(record.isNull(key)) continue;
            
            record.put(key, new Date(record.getLong(key)));
        }
        
        return record;
    }

}