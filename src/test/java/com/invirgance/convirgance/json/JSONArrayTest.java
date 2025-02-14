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
package com.invirgance.convirgance.json;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jbanes
 */
public class JSONArrayTest
{
    
    @Test
    public void testUsage() throws Exception
    {
        JSONArray array = new JSONArray();
        Object[] values = new Object[] {"Hello world!", 1337, 3.14159, true, null};
        int count = 0;
        
        array.addAll(Arrays.asList(values));
        
        assertEquals(values.length, array.size());
        
        // Check that the array has all the expected values
        for(Object value : array)
        {
            assertEquals(values[count], value);
            
            count++;
        }
        
        assertEquals(values.length, count);
    }
    
    @Test
    public void testContains() throws Exception
    {
        JSONArray array = new JSONArray();
        Object[] values = new Object[] {"Hello world!", 1337, 3.14159, true, null};
        
        array.addAll(Arrays.asList(values));
        
        for(Object value : values)
        {
            assertTrue(array.contains(value));
        }
        
        array.containsAll(Arrays.asList(values));
    }
    
    @Test
    public void testToArray() throws Exception
    {
        JSONArray array = new JSONArray();
        Object[] values = new Object[] {"Hello world!", 1337, 3.14159, true, null};
        int count = values.length;
        
        array.addAll(Arrays.asList(values));
        
        values = array.toArray();
        
        assertEquals(count, values.length);
        
        for(int i=0; i<values.length; i++)
        {
            assertEquals(array.get(i), values[i]);
        }
        
        values = array.toArray(values);
        
        assertEquals(count, values.length);
        
        for(int i=0; i<values.length; i++)
        {
            assertEquals(array.get(i), values[i]);
        }
        
        array.clear();
        
        assertEquals(0, array.size());
        assertEquals(count, values.length);
    }
    
    @Test
    public void testToString() throws Exception
    {
        JSONArray array = new JSONArray();
        
        array.add("Hello world!");
        array.add(1337);
        array.add(3.14159);
        array.add(true);
        array.add(null);
        
        assertEquals("[\"Hello world!\",1337,3.14159,true,null]", array.toString());
        assertEquals("[\n    \"Hello world!\",\n    1337,\n    3.14159,\n    true,\n    null\n]", array.toString(4));
    }
    
    @Test
    public void testFromString() throws Exception
    {
        JSONArray record = new JSONArray("[\"Hello world!\",1337,3.14159,true,null,1,9223372036854775807]");
        
        assertEquals(7, record.size());
        assertEquals("Hello world!", record.get(0));
        assertEquals(1337, record.get(1));
        assertEquals(3.14159, record.get(2));
        assertTrue(record.getBoolean(3));
        assertNull(record.get(4));
        assertEquals(1, record.getInt(5));
        assertEquals(9223372036854775807L, record.getLong(6));
    }
    
    @Test
    public void testEquals() throws Exception
    {
        JSONArray left  = new JSONArray("[\"Hello world!\",1337,3.14159,true,null]");
        JSONArray right = new JSONArray("[\"Hello world!\",1337,3.14159,true,null]");
        
        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
        
        assertEquals(new JSONArray(), new JSONArray());
        assertEquals(new JSONArray().hashCode(), new JSONArray().hashCode());
    }
}
