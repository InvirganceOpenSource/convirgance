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
package com.invirgance.convirgance.transform.filter;

import java.util.Comparator;

/**
 *
 * @author jbanes
 */
public class CoerciveComparator implements Comparator
{
    //TODO: Case sensisitivity
    //TODO: Allow Nulls last
    //TODO: Coerce strings to numbers if appropriate
    
    private int compareFloat(float left, float right)
    {
        if(left == right) return 0;
        if(left < right) return -1;
        
        return 1;
    }
    
    private int compareDouble(double left, double right)
    {
        if(left == right) return 0;
        if(left < right) return -1;
        
        return 1;
    }
    
    private int compareLong(long left, long right)
    {
        if(left == right) return 0;
        if(left < right) return -1;
        
        return 1;
    }
    
    private int compareNumbers(Number left, Number right)
    {   
        // TODO: Need BigInteger and BigDecimal
        
        if(left instanceof Double || right instanceof Double) return compareDouble(left.doubleValue(), right.doubleValue());
        if(left instanceof Long || right instanceof Long) return compareLong(left.longValue(), right.longValue());
        if(left instanceof Float || right instanceof Float) return compareFloat(left.floatValue(), right.floatValue());
        
        return left.intValue() - right.intValue();
    }
    
    @Override
    public int compare(Object left, Object right)
    {
        if(left == null && right == null) return 0;
        if(left == null) return -1;
        if(right == null) return 1;
        if(left == right) return 0;
        
        if(left instanceof Number && right instanceof Number)
        {
            return compareNumbers((Number)left, (Number)right);
        }
        
        if(left.getClass().equals(right.getClass()) && left instanceof Comparable)
        {
            return ((Comparable)left).compareTo(right);
        }
        
        if(left.equals(right)) return 0;
        
        return left.hashCode() - right.hashCode();
    }
    
}
