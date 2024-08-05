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

    public CoerceStringsTransformer()
    {
        this(true, true, true, null, null);
    }

    public CoerceStringsTransformer(boolean booleans, boolean doubles, boolean integers)
    {
        this(booleans, doubles, integers, null, null);
    }

    public CoerceStringsTransformer(boolean booleans, boolean doubles, boolean integers, String[] included, String[] excluded)
    {
        this.booleans = booleans;
        this.doubles = doubles;
        this.integers = integers;
        
        
    }
    
    public boolean isBooleans()
    {
        return booleans;
    }

    public boolean isDoubles()
    {
        return doubles;
    }

    public boolean isIntegers()
    {
        return integers;
    }

    public void setBooleans(boolean booleans)
    {
        this.booleans = booleans;
    }

    public void setDoubles(boolean doubles)
    {
        this.doubles = doubles;
    }

    public void setIntegers(boolean integers)
    {
        this.integers = integers;
    }

    public String[] getIncluded()
    {
        return included.toArray(String[]::new);
    }

    public String[] getExcluded()
    {
        return excluded.toArray(String[]::new);
    }

    public void setIncluded(String[] included)
    {
        if(included == null) this.included = null;
        else this.included = new HashSet<>(Arrays.asList(included));
    }

    public void setExcluded(String[] excluded)
    {
        if(excluded == null) this.excluded = null;
        else this.excluded = new HashSet<>(Arrays.asList(excluded));
    }
    
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
