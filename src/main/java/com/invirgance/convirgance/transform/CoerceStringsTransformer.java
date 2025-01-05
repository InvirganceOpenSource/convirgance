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
 * Coerces string values into other types like Integer, Double, and Boolean.
 * For example, 'true' and '1234' will be coerced into Boolean and Integer, 
 * respectively. The types to be coerced and the columns can be customized as
 * needed.
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
     * Creates a new Transformer with support enabled for parsing booleans, doubles, and integers to their string equivalent. 
     */
    public CoerceStringsTransformer()
    {
        this(true, true, true, null, null);
    }

    /**
     * Creates a new Transformer that parses string values into specified types.
     *
     * @param booleans If true, parse boolean strings ("true"/"false")
     * @param doubles If true, parse decimal numbers
     * @param integers If true, parse whole numbers
     */
    public CoerceStringsTransformer(boolean booleans, boolean doubles, boolean integers)
    {
        this(booleans, doubles, integers, null, null);
    }

    /**
     * Creates a Transformer with specified type parsing and field filters.
     *
     * @param booleans If true, parse boolean strings
     * @param doubles If true, parse decimal numbers
     * @param integers If true, parse whole numbers
     * @param included Only parse fields matching these names (null for all)
     * @param excluded Skip parsing for fields matching these names (e.g. "ZIP")
     */
    public CoerceStringsTransformer(boolean booleans, boolean doubles, boolean integers, String[] included, String[] excluded)
    {
        this.booleans = booleans;
        this.doubles = doubles;
        this.integers = integers;
        //TODO: set included and excluded, add tests
        //this.included = included != null ? new HashSet<>(Arrays.asList(included)) : null;
        //this.excluded = excluded != null ? new HashSet<>(Arrays.asList(excluded)) : null;
    }

    /**
     * If Boolean string coercion is enabled.
     * @return True if enabled.
     */
    public boolean isBooleans()
    {
        return booleans;
    }

    /**
     * If Double/Decimal/Scientific string coercion is enabled.
     * @return True if enabled.
     */
    public boolean isDoubles()
    {
        return doubles;
    }

    /**
     * If integer string coercion is enabled.
     * @return True if enabled.
     */
    public boolean isIntegers()
    {
        return integers;
    }

    /**
     * Used to enable coercion of strings to Booleans.
     * @param booleans If coercion should be done.
     */
    public void setBooleans(boolean booleans)
    {
        this.booleans = booleans;
    }

    /**
     * Used to enable coercion of strings to Double/Decimal/Scientific.
     * @param doubles If coercion should be done.
     */
    public void setDoubles(boolean doubles)
    {
        this.doubles = doubles;
    }

    /**
     * Used to enable coercion of strings to Integers.
     * @param integers If coercion should be done.
     */
    public void setIntegers(boolean integers)
    {
        this.integers = integers;
    }

    /**
     * The array of field names to include when parsing.
     * @return The included field names.
     * @throws NullPointerException If included has not been initialized.
     */
    public String[] getIncluded()
    {
        return included.toArray(String[]::new);
    }

    /**
     * The array of field names to exclude when parsing.
     * @return The excluded field names.
     * @throws NullPointerException If excluded has not been initialized.
     */
    public String[] getExcluded()
    {
        return excluded.toArray(String[]::new);
    }
    
    /**
     * Set field names to include when evaluating types during parsing.
     * @param included Array of field/header names to include.
     */
    public void setIncluded(String[] included)
    {
        if(included == null) this.included = null;
        else this.included = new HashSet<>(Arrays.asList(included));
    }
    
    /**
     * Set field names to exclude when evaluating types.
     * @param excluded Array of fields/headers to exclude.
     */
    public void setExcluded(String[] excluded)
    {
        if(excluded == null) this.excluded = null;
        else this.excluded = new HashSet<>(Arrays.asList(excluded));
    }
    
    /**
     * Attempts to coerce a string into its 'real' datatype.
     * @param value The string to coerce.
     * @return The value of the string, as the coerced datatype.
     * @throws NumberFormatException If parsing the string to a double or integer fails.
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
     * Transforms the given JSONObject by coercing the values of specified keys,
     * including those in the 'included' set and excluding those in the
     * 'excluded' set.
     *
     * @param record The record to modify.
     * @return The modified JSONObject with values transformed as per the inclusion and exclusion criteria.
     * @throws ConvirganceException If an error occurs during coercion.
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
