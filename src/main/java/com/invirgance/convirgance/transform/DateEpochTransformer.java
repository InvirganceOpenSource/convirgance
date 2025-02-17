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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A transformer that converts date strings in JSON objects to their epoch equivalents.
 * Allows selective field inclusion and exclusion.
 *
 * <p>This transformer scans JSON records and converts date strings into their
 * corresponding epoch timestamps (milliseconds since Unix epoch). If specific fields
 * are included, only those will be transformed. If specific fields are excluded,
 * all others will be transformed.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // Convert specific fields
 * String[] included = {"created_at", "updated_at"};
 * DateEpochTransformer transformer = new DateEpochTransformer(included, null);
 *
 * // Convert all fields except "excluded_field"
 * String[] excluded = {"excluded_field"};
 * DateEpochTransformer transformer = new DateEpochTransformer(null, excluded);
 * </pre>
 *
 * @author tadghh
 */
public class DateEpochTransformer implements IdentityTransformer
{
    private static final DateTimeFormatter DATE_FORMATTER = createDateFormatter();
    
    private Set<String> included;
    private Set<String> excluded;
    
    /**
     * Creates a transformer that converts all date fields by default.
     */
    public DateEpochTransformer()
    {
        this(null, null);
    }
    
    /**
     * Creates a new DateEpochTransformer for converting date values to their epoch equivalents.
     * 
     * @param included Field names to include in the conversion. Null for all.
     * @param excluded Field names to exclude during conversion. Null for none.
     */
    public DateEpochTransformer(String[] included, String[] excluded)
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
     * Transforms JSON fields containing date strings into epoch timestamps.
     * 
     * @param record The JSON object being transformed.
     * @return The transformed JSON object.
     */
    @Override
    public JSONObject transform(JSONObject record) 
    {
        Long epoch;
        
        if(included != null)
        {
            for(String field : included)
            {              
                if(record.getString(field) != null)
                {
                     epoch = toEpoch(record.getString(field));
                    
                     if(epoch != -1L) record.put(field, epoch);
                }
            }
            
            return record;
        }

        for(String entry : record.keySet())
        {
            if(excluded != null && excluded.contains(entry)) continue;
            
            if(record.getString(entry) != null)
            {
                 epoch = toEpoch(record.getString(entry));

                 if(epoch != -1L) record.put(entry, epoch);
            }
        }

        return record;
    }
    
    private long toEpoch(String value)
    {
        try
        {
            return DATE_FORMATTER.parse(value, Instant::from).toEpochMilli();
        }
        catch (DateTimeParseException ignored)
        {
            // The value failed to parse, its either incorrect (not our problem) or the included fields were not set (we are checking every string value)
            return -1L;
        }
    }
    
    private static DateTimeFormatter createDateFormatter()
    {
        return new DateTimeFormatterBuilder()
            .appendPattern("[yyyy-MM-dd]['T'][HH:mm:ss][HH:mm][.SSS][.SSSSSS][XXX][XX][X][Z]")
            .appendPattern("[yyyy-MM-dd][dd/MM/yyyy][MM/dd/yyyy]")
            .appendPattern("[yyyy'W'ww]['W'ww-e][yyyy-DDD]")
            .appendPattern("[yyyyMMdd][yyyyMMdd'T'HHmmss'Z']")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
            .parseDefaulting(ChronoField.MICRO_OF_SECOND, 0)
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .toFormatter()
            .withZone(ZoneOffset.UTC);
    }
}
