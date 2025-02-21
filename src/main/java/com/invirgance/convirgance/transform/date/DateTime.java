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

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.transform.IdentityTransformer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for date-related transformers that handle JSON field conversions.
 * Provides common functionality for field inclusion/exclusion and transformation logic.
 *
 * <p>
 * This class implements the basic structure for date transformers, including:
 * - Field inclusion/exclusion management
 * - Common transformation patterns
 * - Base implementation of the IdentityTransformer interface
 * </p>
 *
 * @author tadghh
 */
public abstract class DateTime implements IdentityTransformer 
{
    private Set<String> included;
    private Set<String> excluded;

    /**
     * Creates a new Date transformer with no field restrictions.
     */
    protected DateTime() 
    {
        this(null, null);
    }

    /**
     * Creates a new Date transformer with specified field inclusions and exclusions.
     *
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude from conversion. Null for none.
     */
    protected DateTime(String[] included, String[] excluded) 
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
     * If both excluded and included are set and a identical field is included in both it will remain excluded.
     * 
     * @param excluded Array of field names to exclude from conversion. Set to null to exclude no fields.
     */
    public void setExcluded(String[] excluded) 
    {
        if(excluded == null) this.excluded = null;
        else this.excluded = new HashSet<>(Arrays.asList(excluded));
    }

    /**
     * Checks if a field should be processed based on inclusion/exclusion rules.
     *
     * @param field The field name to check
     * @return true if the field should be processed, false otherwise
     */
    protected boolean shouldProcessField(String field) 
    {
        if(excluded != null && excluded.contains(field)) return false;
        if(included != null && !included.contains(field)) return false;
        
        return true;
    }

    /**
     * Template method for transforming a JSON field value.
     *
     * @param value The value to transform
     * @return The transformed value
     */
    protected abstract Object transformAction(Object value);

    /**
     * Transforms JSON fields according to the concrete transformer's logic.
     *
     * @param record The JSON object being transformed.
     * @return The transformed JSON object.
     */
    @Override
    public JSONObject transform(JSONObject record) 
    {     
        if(included != null) 
        {
            for(String field : included) 
            {
                if(shouldProcessField(field)) 
                {                
                    Object transformed = transformAction(record.get(field));
                    
                    if(transformed != null) record.put(field, transformed);    
                }
            }
            
            return record;
        }

        for(String field : record.keySet())
        {
            if(shouldProcessField(field))
            {
                Object transformed = transformAction(record.get(field));
                
                if(transformed != null) record.put(field, transformed);
            }
        }
        
        return record;
    }
   

}