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

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import java.util.*;

/**
 * A transformer that converts string values in JSON objects to their appropriate data types.
 * Supports automatic conversion to Boolean, Double/Decimal, and Integer types based on string patterns.
 * Field selection can be customized through inclusion and exclusion lists.
 * 
 * <p>For example, string values like "true", "123.45", and "42" can be automatically
 * converted to their corresponding Boolean, Double, and Integer types.</p>
 * 
 * @author jbanes
 */
public class CoerceStringsTransformer implements IdentityTransformer
{
    private boolean booleans;
    private boolean doubles;
    private boolean integers;
    
    private Set<String> included;
    private Set<String> excluded;

    /**
     * Creates a new transformer with all type conversions enabled by default.
     * Automatically converts strings to booleans, doubles, and integers where possible.
     */
    public CoerceStringsTransformer()
    {
        this(true, true, true, null, null);
    }

    /**
     * Creates a new transformer with selective type conversion support.
     *
     * @param booleans Set to true to enable conversion of "true"/"false" strings to Boolean values.
     * @param doubles Set to true to enable conversion of decimal strings to Double values.
     * @param integers Set to true to enable conversion of whole number strings to Integer values.
     */
    public CoerceStringsTransformer(boolean booleans, boolean doubles, boolean integers)
    {
        this(booleans, doubles, integers, null, null);
    }

    /**
     * Creates a transformer with selective type conversion and field filtering.
     *
     * <p>Example usage:</p>
     * <pre>
     * // Create transformer that only converts numeric fields except ZIP codes
     * boolean[] flags = {false, true, true}; // booleans, doubles, integers
     * String[] included = {"price", "quantity", "zipCode"};
     * String[] excluded = {"zipCode"}; // prevent ZIP code conversion
     * CoerceStringsTransformer transformer = new CoerceStringsTransformer(flags[0], flags[1], flags[2], included, excluded);
     * 
     * // Input:  {"price": "19.99", "quantity": "5", "zipCode": "12345"}
     * // Output: {"price": 19.99, "quantity": 5, "zipCode": "12345"}
     * </pre>
     *
     * @param booleans Set to true to enable conversion of "true"/"false" strings to Boolean values.
     * @param doubles Set to true to enable conversion of decimal strings to Double values.
     * @param integers Set to true to enable conversion of whole number strings to Integer values.
     * @param included Array of field names to process. Set to null to process all fields.
     * @param excluded Array of field names to skip. Set to null to exclude no fields.
     */
    public CoerceStringsTransformer(boolean booleans, boolean doubles, boolean integers, String[] included, String[] excluded)
    {
        this.booleans = booleans;
        this.doubles = doubles;
        this.integers = integers;
        this.included = included != null ? new HashSet<>(Arrays.asList(included)) : null;
        this.excluded = excluded != null ? new HashSet<>(Arrays.asList(excluded)) : null;
    }

    /**
     * Checks if boolean string conversion is enabled.
     * 
     * @return True if boolean conversion is enabled, false otherwise.
     */
    public boolean isBooleans()
    {
        return booleans;
    }

    /**
     * Checks if double/decimal string conversion is enabled.
     * 
     * @return True if double conversion is enabled, false otherwise.
     */
    public boolean isDoubles()
    {
        return doubles;
    }

    /**
     * Checks if integer string conversion is enabled.
     * 
     * @return True if integer conversion is enabled, false otherwise.
     */
    public boolean isIntegers()
    {
        return integers;
    }

    /**
     * Enables or disables boolean string conversion.
     * 
     * @param booleans Set to true to enable boolean conversion, false to disable it.
     */
    public void setBooleans(boolean booleans)
    {
        this.booleans = booleans;
    }

    /**
     * Enables or disables double/decimal string conversion.
     * 
     * @param doubles Set to true to enable double conversion, false to disable it.
     */
    public void setDoubles(boolean doubles)
    {
        this.doubles = doubles;
    }

    /**
     * Enables or disables integer string conversion.
     * 
     * @param integers Set to true to enable integer conversion, false to disable it.
     */
    public void setIntegers(boolean integers)
    {
        this.integers = integers;
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
     * Attempts to convert a string value to its appropriate data type.
     * 
     * <p>Conversion rules:</p>
     * <ul>
     * <li>Strings "true" or "false" (case insensitive) are converted to Boolean if boolean conversion is enabled.</li>
     * <li>Strings containing a decimal point are converted to Double if double conversion is enabled.</li>
     * <li>Strings containing only digits (with optional leading minus) are converted to Integer or Long if integer conversion is enabled.</li>
     * </ul>
     *
     * @param value The string value to convert.
     * @return The converted value, or the original string if conversion is not possible.
     * @throws NumberFormatException If numeric conversion fails for a string that appears to be a number.
     */
    public Object coerce(String value)
    {
        char c;
        Long number;
        
        if(value.length() < 1) return value;
        
        c = Character.toLowerCase(value.charAt(0));
        
        if(booleans && (c == 't' || c == 'f'))
        {
            if(value.equalsIgnoreCase("true")) return true;
            if(value.equalsIgnoreCase("false")) return false;
            
            return value;
        }
        
        if(doubles && value.contains(".") && (c == '-' || Character.isDigit(c)))
        {
            try { return Double.valueOf(value); } catch(NumberFormatException e) { }
            
            
            return value;
        }
        
        if(integers && (c == '-' || Character.isDigit(c)))
        {
            try 
            { 
                number = Long.valueOf(value);
                
                if(number == number.intValue()) return number.intValue();
                
                return number;
            } 
            catch(NumberFormatException e) { }
            
            return value;
        }
        
        return value;
    }

    /**
     * Transforms a JSON object by converting string values to appropriate data types.
     * Only processes fields based on inclusion/exclusion settings and enabled type conversions.
     *
     * <p>The transformation modifies the input object directly, converting string values
     * to their corresponding data types where possible.</p>
     *
     * @param record The JSON object to transform.
     * @return The modified JSON object (same instance as input).
     * @throws ConvirganceException If an error occurs during type conversion.
     */
    @Override
    public JSONObject transform(JSONObject record) throws ConvirganceException
    {
        for(Map.Entry<String,Object> entry : record.entrySet())
        {
            if(entry.getValue() == null) continue;
            if(!(entry.getValue() instanceof String)) continue;
            if(included != null && !included.contains(entry.getKey())) continue;
            if(excluded != null && excluded.contains(entry.getKey())) continue;
            
            entry.setValue(coerce((String)entry.getValue()));
        }
        
        return record;
    }
}
