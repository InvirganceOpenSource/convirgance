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

import java.time.Instant;

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
public class EpochDateTransformer extends DateTime 
{
    private final int EPOCH_THIRTEEN = 13;
    private final int EPOCH_SIXTEEN = 16;
    private final int EPOCH_SIXTEEN_DIVISOR = 1_000_000;
    private final int EPOCH_NINETEEN = 19;
    private final int EPOCH_NINETEEN_DIVISOR = 1_000_000_000;

    /**
     * Creates a transformer that converts all date fields by default.
     */
    public EpochDateTransformer() 
    {
        super();
    }

    /**
     * Creates a new EpochDateTransformer for converting epoch values to their Date (string) equivalents.
     * 
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude during conversion. Null for none.
     */
    public EpochDateTransformer(String[] included, String[] excluded) 
    {
        super(included, excluded);
    }

    /**
     * Transforms a Long value representing an epoch timestamp to its date string representation.
     *
     * @param value The value to transform
     * @return The date string, or the original value if not transformable
     */
    @Override
    protected Object transformAction(Object value) 
    {
        if(value instanceof Long) return convertEpoch((Long) value);
        
        return null;
    }

    private String convertEpoch(Long epoch) 
    {
        int digits = String.valueOf(epoch).length();
        
        switch(digits) 
        {
            case EPOCH_THIRTEEN: return convertEpochString(Instant.ofEpochMilli(epoch));
            case EPOCH_SIXTEEN: return convertEpochString(Instant.ofEpochSecond(epoch / EPOCH_SIXTEEN_DIVISOR, epoch % EPOCH_SIXTEEN_DIVISOR));
            case EPOCH_NINETEEN: return convertEpochString(Instant.ofEpochSecond(epoch / EPOCH_NINETEEN_DIVISOR, epoch % EPOCH_NINETEEN_DIVISOR));
            default: return convertEpochString(Instant.ofEpochSecond(epoch));
        }
    }
    
    private String convertEpochString(Instant epoch) 
    {
        String timestamp = epoch.toString();
  
        if(timestamp.endsWith("Z")) return timestamp.substring(0, timestamp.length() - 1);
          
        return timestamp.substring(0, timestamp.lastIndexOf("Z"));
    }
}