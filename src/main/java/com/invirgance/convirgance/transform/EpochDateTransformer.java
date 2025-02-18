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
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A transformer that converts epoch timestamps in JSON objects to their UTC date string equivalents,
 * allowing selective field inclusion and exclusion.
 *
 * <p>This transformer scans JSON records and converts epoch timestamps (milliseconds since the Unix epoch) 
 * into their corresponding UTC date strings. If specific fields are included, only those fields will be transformed. 
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
public class EpochDateTransformer implements IdentityTransformer
{
    private final int EPOCH_THIRTEEN = 13;
    
    private final int EPOCH_SIXTEEN = 16;
    private final int EPOCH_SIXTEEN_DIVISOR = 1_000_000;
    
    private final int EPOCH_NINETEEN = 19;
    private final int EPOCH_NINETEEN_DIVISOR = 1_000_000_000;
    
    private Set<String> included;
    private Set<String> excluded;
    
    /**
     * Creates a transformer that converts all date fields by default.
     */
    public EpochDateTransformer()
    {
        this(null, null);
    }
    
    /**
     * Creates a new DateEpochTransformer for converting epoch values to their Date (string) equivalents.
     * 
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude during conversion. Null for none.
     */
    public EpochDateTransformer(String[] included, String[] excluded)
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
     * Transforms JSON fields containing epoch longs into Date (Strings).
     * 
     * @param record The JSON object being transformed.
     * @return The transformed JSON object.
     */
    @Override
    public JSONObject transform(JSONObject record) 
    {
        String epoch;
        Object value;
        
        if(included != null)
        {
            for(String field : included)
            {
                value = record.get(field);
                
                if(value instanceof Long)
                {
                    epoch = convertEpoch((Long)value);
                    record.put(field, epoch);
                }
            }
            return record;
        }

        for(String field : record.keySet())
        {
            if(excluded != null && excluded.contains(field)) continue;

            value = record.get(field);
            
            if(value instanceof Long)
            {
                epoch = convertEpoch((Long)value);
                record.put(field, epoch);
            }
        }
        
        return record;
    }
    
    /**
     * Converts a Long value into its epoch UTC string equivalent
     * 
     * @param epoch Epoch time.
     * @return Epoch time to UTC String
     */
    public String convertEpoch(Long epoch) 
    {
        int digits = String.valueOf(epoch).length();

        switch(digits) 
        {
            case EPOCH_THIRTEEN: return convertEpochString( Instant.ofEpochMilli(epoch));
            case EPOCH_SIXTEEN: return convertEpochString(Instant.ofEpochSecond(epoch / EPOCH_SIXTEEN_DIVISOR, epoch % EPOCH_SIXTEEN_DIVISOR));
            case EPOCH_NINETEEN: return convertEpochString(Instant.ofEpochSecond(epoch / EPOCH_NINETEEN_DIVISOR, epoch % EPOCH_NINETEEN_DIVISOR));
            default: return convertEpochString(Instant.ofEpochSecond(epoch));
        }
    }
    
    private String convertEpochString(Instant epoch)
    {
        return epoch.atZone(ZoneOffset.UTC).toLocalDateTime().toString();
    }
}
