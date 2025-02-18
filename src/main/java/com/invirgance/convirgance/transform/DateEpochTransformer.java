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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A transformer that converts date strings in JSON objects to their epoch
 * equivalents. Allows selective field inclusion and exclusion.
 *
 * <p>
 * This transformer scans JSON records and converts date strings into their
 * corresponding epoch timestamps (milliseconds since Unix epoch). If specific
 * fields are included, only those will be transformed. If specific fields are
 * excluded, all others will be transformed.</p>
 *
 * <p>Example usage:</p>
 * 
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
    private Set<String> included;
    private Set<String> excluded;
    private ISODateParser test = new ISODateParser();

    /**
     * Creates a DateEpochTransformer that try's to convert all String fields of a JSONObject to a epoch value.
     */
    public DateEpochTransformer()
    {
        this(null, null);
    }

    /**
     * Creates a new DateEpochTransformer for converting date values to their
     * epoch equivalents.
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
     * @throws NullPointerException If the included set has not been
     * initialized.
     */
    public String[] getIncluded()
    {
        return included.toArray(String[]::new);
    }

    /**
     * Gets the array of field names that are excluded from conversion.
     *
     * @return Array of excluded field names.
     * @throws NullPointerException If the excluded set has not been
     * initialized.
     */
    public String[] getExcluded()
    {
        return excluded.toArray(String[]::new);
    }

    /**
     * Sets the field names to include in type conversion.
     *
     * @param included Array of field names to include in conversion. Set to
     * null to include all fields.
     */
    public void setIncluded(String[] included)
    {
        if(included == null) this.included = null;
        else this.included = new HashSet<>(Arrays.asList(included));
    }

    /**
     * Sets the field names to exclude from type conversion.
     *
     * @param excluded Array of field names to exclude from conversion. Set to
     * null to exclude no fields.
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
        Object value;
        
        if(included != null)
        {
            for(String field : included)
            {
                value = record.get(field);
                
                if(value instanceof String)
                {
                    epoch = test.toEpoch((String) value);

                    if(epoch != -1L) record.put(field, epoch);
                }
            }

            return record;
        }

        for(String field : record.keySet())
        {
            if(excluded != null && excluded.contains(field)) continue;
            
            value = record.get(field);
                
            if(value instanceof String)
            {
                epoch = test.toEpoch((String) value);

                if(epoch != -1L) record.put(field, epoch);
            }
        }

        return record;
    }

    private class ISODateParser
    {
        private TemporalAccessor parsed;

        public ISODateParser()
        {
        }

        public long toEpoch(String value)
        {
            try
            {
                parsed = FORMATTER.parseBest(value, ZonedDateTime::from, LocalDateTime::from, LocalDate::from, Instant::from, YearMonth::from, Year::from);
                
                if(parsed instanceof Year) return ((Year) parsed).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
                if(parsed instanceof ZonedDateTime) return ((ZonedDateTime) parsed).toInstant().toEpochMilli();
                if(parsed instanceof LocalDateTime) return ((LocalDateTime) parsed).toInstant(ZoneOffset.UTC).toEpochMilli();
                if(parsed instanceof LocalDate) return ((LocalDate) parsed).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
                if(parsed instanceof Instant) return ((Instant) parsed).toEpochMilli();
                if(parsed instanceof YearMonth) return ((YearMonth) parsed).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
            }
            catch (DateTimeParseException e)
            {
                // The value is either not a valid date or simply not a date all together
            }
            
            return -1L;
        }

        private final DateTimeFormatter BASIC_WEEK_FORMATTER = new DateTimeFormatterBuilder()
                .appendPattern("[yyyy'W'wwd][yyyy'-W'wwd][yyyy'W'ww][yyyy'-W'ww]")
                .toFormatter()
                .withZone(ZoneOffset.UTC);
        
        private final DateTimeFormatter BASIC_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMdd")
                .optionalStart()
                .appendLiteral('T')
                .appendPattern("HHmmss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .optionalStart()
                .appendOffset("+HHMM", "Z")
                .optionalEnd()
                .optionalEnd()
                .toFormatter()
                .withZone(ZoneOffset.UTC);
        
        // As in Year Month, Year Day or just Year
        private final DateTimeFormatter BASIC_YEAR_MONTH_DAY_FORMATTER = new DateTimeFormatterBuilder()
                .appendPattern("[yyyyMM][yyyy-MM][yyyyDDD][yyyy]")
                .toFormatter()
                .withZone(ZoneOffset.UTC);

        // Year Month Day and possible
        private final DateTimeFormatter YEAR_MONTH_DAY_TIME = new DateTimeFormatterBuilder()
                .appendPattern("[yyyyMMdd][yyyyMMdd'T'HHmmss]")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .toFormatter()
                .withZone(ZoneOffset.UTC);

        // Europe
        private final DateTimeFormatter ISO_DATE_TIME_COMMA = new DateTimeFormatterBuilder()
                .appendPattern("[yyyy-MM-dd'T'HH:mm:ss][yyyyMMdd'T'HH:mm:ss]")
                .appendLiteral(',')
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, false)
                .appendOffset("+HH:mm", "Z")
                .toFormatter()
                .withZone(ZoneOffset.UTC);

        private final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
                .appendOptional(ISO_DATE_TIME_COMMA)
                .appendOptional(BASIC_DATE_TIME_FORMATTER)
                .appendOptional(YEAR_MONTH_DAY_TIME)
                .appendOptional(BASIC_WEEK_FORMATTER)
                .appendOptional(DateTimeFormatter.ISO_DATE_TIME)
                .appendOptional(DateTimeFormatter.ISO_INSTANT)
                .appendOptional(DateTimeFormatter.ISO_ZONED_DATE_TIME)
                .appendOptional(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendOptional(DateTimeFormatter.ISO_WEEK_DATE)
                .appendOptional(DateTimeFormatter.ISO_ORDINAL_DATE)
                .appendOptional(DateTimeFormatter.BASIC_ISO_DATE)
                .appendOptional(BASIC_YEAR_MONTH_DAY_FORMATTER)
                .toFormatter()
                .withZone(ZoneOffset.UTC);
    }

}
