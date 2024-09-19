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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class CoerciveComparatorTest
{
    @Test
    public void testIntegers()
    {
        CoerciveComparator comparator = new CoerciveComparator();
        
        // Test basic integers
        assertEquals(0, comparator.compare(0, 0));
        assertEquals(-1, comparator.compare(0, 1));
        assertEquals(1, comparator.compare(1, 0));
        assertEquals(-5, comparator.compare(0, 5));
        assertEquals(5, comparator.compare(5, 0));
        assertEquals(0, comparator.compare(5, 5));
        
        // Test integers compared to longs on the right
        assertEquals(0, comparator.compare(0, 0L));
        assertEquals(-1, comparator.compare(0, 1L));
        assertEquals(1, comparator.compare(1, 0L));
        
        // Test integers compared to longs on the left
        assertEquals(0, comparator.compare(0L, 0));
        assertEquals(-1, comparator.compare(0L, 1));
        assertEquals(1, comparator.compare(1L, 0));
        
        // Test longs compared to longs
        assertEquals(0, comparator.compare(0L, 0L));
        assertEquals(-1, comparator.compare(0L, 1L));
        assertEquals(1, comparator.compare(1L, 0L));
        
        // Test integers compared to doubles on the right
        assertEquals(0, comparator.compare(0, 0.0));
        assertEquals(-1, comparator.compare(0, 1.0));
        assertEquals(1, comparator.compare(1, 0.0));
        
        // Test integers compared to doubles on the left
        assertEquals(0, comparator.compare(0.0, 0));
        assertEquals(-1, comparator.compare(0.0, 1));
        assertEquals(1, comparator.compare(1.0, 0));
        
        // Test integers compared to floats on the right
        assertEquals(0, comparator.compare(0, 0.0f));
        assertEquals(-1, comparator.compare(0, 1.0f));
        assertEquals(1, comparator.compare(1, 0.0f));
        
        // Test integers compared to floats on the left
        assertEquals(0, comparator.compare(0.0f, 0));
        assertEquals(-1, comparator.compare(0.0f, 1));
        assertEquals(1, comparator.compare(1.0f, 0));
    }
    
    @Test
    public void testStrings()
    {
        CoerciveComparator comparator = new CoerciveComparator();
        
        // Capitals come before lower case in ASCII & UTF-8
        assertEquals(0, comparator.compare("Hello", "Hello"));
        assertEquals(-32, comparator.compare("Hello", "hello"));
        assertEquals(32, comparator.compare("hello", "Hello"));
    }
    
    @Test
    public void testNulls()
    {
        CoerciveComparator comparator = new CoerciveComparator();
        
        // Capitals come before lower case in ASCII & UTF-8
        assertEquals(0, comparator.compare(null, null));
        assertEquals(-1, comparator.compare(null, "Hello"));
        assertEquals(1, comparator.compare("Hello", null));
    }
}
