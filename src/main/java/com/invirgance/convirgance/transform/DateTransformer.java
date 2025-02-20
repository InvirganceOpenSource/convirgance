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
package com.invirgance.convirgance.transform;

import com.invirgance.convirgance.json.JSONObject;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * A transformer class that handles date format conversions for JSON fields.
 * This class extends DateTime and provides functionality to convert between different
 * date representations including Date objects, Strings, and epoch timestamps (Numbers).
 * 
 * The transformer can be configured to:
 * - Convert Date objects to Strings or Numbers (epoch timestamps)
 * - Convert Strings or Numbers (epoch timestamps) to Date objects
 * - Selectively apply transformations to specific fields
 * - Exclude specific fields from transformation
 * - Only supports conversion from String (Dates) formatted as RFC 1123
 * 
 * @author tadghh
 */
public class DateTransformer implements IdentityTransformer 
{
    private Function<Object, Object> transformAction;
    private Set<String> included;
    private Set<String> excluded;

    /**
     * Creates a new Date transformer with no field restrictions.
     */
    public DateTransformer() 
    {
        this(null, null);
    }

    /**
     * Creates a new Date transformer with specified field inclusions and exclusions.
     *
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude from conversion. Null for none.
     */
    public DateTransformer(String[] included, String[] excluded) 
    {
        this.included = included != null ? new HashSet<>(Arrays.asList(included)) : null;
        this.excluded = excluded != null ? new HashSet<>(Arrays.asList(excluded)) : null;
    }

    /**
     * Gets the array of field names that are included in conversion.
     *
     * @return Array of included field names.
     * @throws NullPointerException If the included set has not been initialized.
     */
    public String[] getIncluded() 
    {
        return included.toArray(String[]::new);
    }

    /**
     * Gets the array of field names that are excluded from conversion.
     *
     * @return Array of excluded field names.
     * @throws NullPointerException If the excluded set has not been initialized.
     */
    public String[] getExcluded() 
    {
        return excluded.toArray(String[]::new);
    }

    /**
     * Sets the field names to include in type conversion.
     *
     * @param included Array of field names to include in conversion. Set to null to include all fields.
     */
    public void setIncluded(String[] included) 
    {
        if(included == null) this.included = null;
        else this.included = new HashSet<>(Arrays.asList(included));
    }

    /**
     * Sets the field names to exclude from type conversion.
     *
     * @param excluded Array of field names to exclude from conversion. Set to null to exclude no fields.
     */
    public void setExcluded(String[] excluded) 
    {
        if(excluded == null) this.excluded = null;
        else this.excluded = new HashSet<>(Arrays.asList(excluded));
    }
   
    /**
     * Enables conversion from Date to String during transformation.
     * Setting this will override any previous value.
     * 
     * @param enabled If the transform action should be set to this.
     */
    public void setConvertToString(boolean enabled) 
    {
        if(enabled)
        {
            transformAction = value -> value instanceof Date ? ((Date) value).toString() : null;
        }
        else
        {
            transformAction = null;
        }
    }
    
    /**
     * Enables conversion from Date to Number during transformation.
     * Setting this will override any previous value.
     * 
     * @param enabled If the transform action should be set to this.
     */
    public void setConvertToNumber(boolean enabled) 
    {
        if(enabled)
        {
            transformAction = value -> value instanceof Date ? ((Date) value).getTime() : null;
        }
        else
        {
            transformAction = null;
        }
    }
    
    /**
     * Enables conversion from String to Date during transformation.
     * Setting this will override any previous value.
     * 
     * @param enabled If the transform action should be set to this.
     */
    public void setConvertFromString(boolean enabled) 
    {
        if(enabled)
        {
            transformAction = value -> value instanceof String ? new Date((String) value) : null;
        }
        else
        {
            transformAction = null;
        }
    }
    
    /**
     * Enables conversion from Number to Date during transformation. 
     * Setting this will override any previous value.
     * 
     * @param enabled If the transform action should be set to this.
     */
    public void setConvertFromNumber(boolean enabled) 
    {
        if(enabled)
        {
            transformAction = value -> value instanceof Long ? new Date((Long) value) : null;
        }
        else
        {
            transformAction = null;
        }
    }  
    
    private boolean shouldProcessField(String field) 
    {
        if(excluded != null && excluded.contains(field)) return false;
        if(included != null && !included.contains(field)) return false;
        
        return true;
    }
    
    @Override
    public JSONObject transform(JSONObject record) 
    {     
        if(included != null) 
        {
            for(String field : included) 
            {
                if(shouldProcessField(field)) 
                {                
                    Object transformed = transformAction.apply(record.get(field));
                    
                    if(transformed != null) record.put(field, transformed);    
                }
            }
            
            return record;
        }

        for(String field : record.keySet())
        {
            if(shouldProcessField(field))
            {
                Object transformed = transformAction.apply(record.get(field));
                
                if(transformed != null) record.put(field, transformed);
            }
        }
        
        return record;
    }
}
