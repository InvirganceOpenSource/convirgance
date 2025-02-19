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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
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
    protected final ISODateParser parser;

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
        this.parser = new ISODateParser();
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
    
    /**
    * A utility class for parsing and normalizing various ISO 8601 date-time formats.
    * Supports a wide range of formats including:
    * <ul>
    *   <li>Basic and extended ISO calendar dates (e.g., "20250519", "2025-05-19")</li>
    *   <li>Date-times with optional fractional seconds (e.g., "2025-05-19T14:30:00.123")</li>
    *   <li>Week dates (e.g., "2025-W21-2", "2025W212")</li>
    *   <li>Ordinal dates (e.g., "2012-337")</li>
    *   <li>Partial dates (Year-Month, Year only)</li>
    *   <li>Dates with timezone offsets</li>
    * </ul>
    */
    protected class ISODateParser
    {
        private TemporalAccessor parsed;

        public ISODateParser()
        {
        }
        
        /**
         * Normalizes a Date object to a UTC date-time string in a standardized format.
         * 
         * @param value The Date object to normalize
         * @return A string in the format "yyyy-MM-dd'T'HH:mm:ss.SSSX" representing the date in UTC
         * 
         */
        public String normalize(Date value) 
        {
            return value.toInstant().atZone(ZoneOffset.UTC).format(NORMALIZE_FORMATTER);
        }

        /**
         * Normalizes a string date representation to a UTC date-time string.
         * Handles various ISO 8601 formats and converts them to a standard representation.
         * Partial dates (e.g., year only or year-month) are completed by using the first day/time.
         * 
         * @param value A date in the format of a string.
         * @return The normalized string date.
         */
        public String normalize(String value) 
        {
            parsed = FORMATTER.parseBest(value, ZonedDateTime::from, LocalDateTime::from, LocalDate::from, Instant::from, YearMonth::from, Year::from);

            if(parsed instanceof Year) return ((Year) parsed).atDay(1).atStartOfDay(ZoneOffset.UTC).format(NORMALIZE_FORMATTER);
            if(parsed instanceof ZonedDateTime) return ((ZonedDateTime) parsed).withZoneSameInstant(ZoneOffset.UTC).format(NORMALIZE_FORMATTER);
            if(parsed instanceof LocalDateTime) return ((LocalDateTime) parsed).atZone(ZoneOffset.UTC).format(NORMALIZE_FORMATTER);
            if(parsed instanceof LocalDate) return ((LocalDate) parsed).atStartOfDay(ZoneOffset.UTC).format(NORMALIZE_FORMATTER);
            if(parsed instanceof YearMonth) return ((YearMonth) parsed).atDay(1).atStartOfDay(ZoneOffset.UTC).format(NORMALIZE_FORMATTER);

            return ((Instant) parsed).atZone(ZoneOffset.UTC).format(NORMALIZE_FORMATTER);
        }
        
        /**
         * Converts a string date representation to Unix epoch milliseconds.
         * For dates without time components, the start of day (00:00:00) in UTC is used.
         * Partial dates use the first day of the period (e.g., first day of month for year-month format).
         * 
         * @param value A date in the format of a string.
         * @return Unix epoch of the date.
         */
        public long toEpoch(String value)
        {
            parsed = FORMATTER.parseBest(value, ZonedDateTime::from, LocalDateTime::from, LocalDate::from, Instant::from, YearMonth::from, Year::from);

            if(parsed instanceof ZonedDateTime) return ((ZonedDateTime) parsed).toInstant().toEpochMilli();
            if(parsed instanceof LocalDateTime) return ((LocalDateTime) parsed).toInstant(ZoneOffset.UTC).toEpochMilli();
            if(parsed instanceof LocalDate) return ((LocalDate) parsed).toEpochSecond(LocalTime.MIN, ZoneOffset.UTC);
            if(parsed instanceof YearMonth) return ((YearMonth) parsed).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
            if(parsed instanceof Year) return ((Year) parsed).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

            return ((Instant) parsed).toEpochMilli();
        }
        
        /**
         * Converts any supported ISO 8601 format string to a normalized UTC instant string.
         * The output format follows the ISO 8601 instant format (RFC 3339).
         * 
         * @param value A string representing a date in any supported ISO 8601 format
         * @return An ISO 8601 instant string in UTC (e.g., "2025-05-19T00:00:00Z") 
         */
        public String toString(String value)
        {
            parsed = FORMATTER.parseBest(value, ZonedDateTime::from, LocalDateTime::from, LocalDate::from, Instant::from, YearMonth::from, Year::from);

            if(parsed instanceof ZonedDateTime) return ((ZonedDateTime) parsed).toInstant().toString();
            if(parsed instanceof LocalDateTime) return ((LocalDateTime) parsed).toInstant(ZoneOffset.UTC).toString();
            if(parsed instanceof LocalDate) return ((LocalDate) parsed).atStartOfDay(ZoneOffset.UTC).toInstant().toString();
            if(parsed instanceof YearMonth) return ((YearMonth) parsed).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant().toString();
            if(parsed instanceof Year) return ((Year) parsed).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant().toString();

            return ((Instant) parsed).toString();
        }

        /**
         * Converts a Date object to an ISO 8601 instant string in UTC.
         * 
         * @param value The Date object to convert
         * @return An ISO 8601 instant string in UTC (e.g., "2025-05-19T00:00:00Z")
         */        
        public String toString(Date value)
        {
            return value.toInstant().toString();
        }

        // For normalizing DateTime strings to some generic format    
        private final DateTimeFormatter NORMALIZE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneOffset.UTC);

        private final DateTimeFormatter BASIC_WEEK_FORMATTER = new DateTimeFormatterBuilder()
                .appendPattern("YYYY")
                .optionalStart()
                .appendLiteral('-')
                .optionalEnd()
                .appendLiteral('W')
                .appendPattern("ww")
                .optionalStart()
                .optionalStart()
                .appendLiteral('-')
                .optionalEnd()
                .appendPattern("e")
                .optionalEnd()
                .parseDefaulting(ChronoField.DAY_OF_WEEK, 1)
                .toFormatter();

        private final DateTimeFormatter BASIC_WEEK_DAY_FORMATTER = new DateTimeFormatterBuilder()
                .appendPattern("YYYY'W'wwe").toFormatter();

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

        // Year Month Day and possible time
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

        // Handles formatting of most if not all UTC date, date+time formats
        private final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ISO_INSTANT)
                .appendOptional(DateTimeFormatter.ISO_ZONED_DATE_TIME)
                .appendOptional(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .appendOptional(BASIC_WEEK_DAY_FORMATTER)
                .appendOptional(ISO_DATE_TIME_COMMA)
                .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME)        
                .appendOptional(DateTimeFormatter.ISO_WEEK_DATE)
                .appendOptional(DateTimeFormatter.ISO_ORDINAL_DATE)           
                .appendOptional(BASIC_WEEK_FORMATTER)       
                .appendOptional(BASIC_DATE_TIME_FORMATTER)
                .appendOptional(YEAR_MONTH_DAY_TIME)          
                .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendOptional(DateTimeFormatter.BASIC_ISO_DATE)
                .appendOptional(BASIC_YEAR_MONTH_DAY_FORMATTER)
                .toFormatter()
                .withZone(ZoneOffset.UTC);
    }

}