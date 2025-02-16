/*
 * The MIT License
 *
 * Copyright 2025 Invirgance LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.invirgance.convirgance.transform;

import com.invirgance.convirgance.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class CoerceStringsTransformerTest
{

    /**
     * Test of isBooleans method, of class CoerceStringsTransformer.
     */
    @Test
    public void testIsBooleans()
    {
        boolean expected = true;
        boolean result;
        
        CoerceStringsTransformer instance = new CoerceStringsTransformer();
        
        result = instance.isBooleans();
        assertEquals(expected, result);
    }

    /**
     * Test of isDoubles method, of class CoerceStringsTransformer.
     */
    @Test
    public void testIsDoubles()
    {
        boolean expected = true;
        boolean result;
        
        CoerceStringsTransformer instance = new CoerceStringsTransformer();

        result = instance.isDoubles();
        assertEquals(expected, result);
    }

    /**
     * Test of isIntegers method, of class CoerceStringsTransformer.
     */
    @Test
    public void testIsIntegers()
    {
        boolean expected = true;
        boolean result;
        
        CoerceStringsTransformer instance = new CoerceStringsTransformer();

        result = instance.isIntegers();
        assertEquals(expected, result);
    }

    /**
     * Test of setBooleans method, of class CoerceStringsTransformer.
     */
    @Test
    public void testSetBooleans()
    {
        boolean booleans = false;
        
        CoerceStringsTransformer instance = new CoerceStringsTransformer();
        
        instance.setBooleans(booleans);
        assertFalse(instance.isBooleans());
    }

    /**
     * Test of setDoubles method, of class CoerceStringsTransformer.
     */
    @Test
    public void testSetDoubles()
    {
        boolean doubles = false;
        
        CoerceStringsTransformer instance = new CoerceStringsTransformer();
        
        instance.setDoubles(doubles);
        assertFalse(instance.isDoubles());
    }

    /**
     * Test of setIntegers method, of class CoerceStringsTransformer.
     */
    @Test
    public void testSetIntegers()
    {
        boolean integers = false;
        
        CoerceStringsTransformer instance = new CoerceStringsTransformer();
        
        instance.setIntegers(integers);
        assertFalse(instance.isIntegers());
    }

    /**
     * Test of setIncluded, getIncluded methods, of class CoerceStringsTransformer.
     */
    @Test
    public void testIncluded()
    {
        String[] included = new String[]{"NAME"};
        String[] result;
        
        CoerceStringsTransformer instance = new CoerceStringsTransformer();
        instance.setIncluded(included);      
        
        result = instance.getIncluded();
        assertArrayEquals(included, result);
    }

    /**
     * Test of setExcluded, getExcluded methods, of class CoerceStringsTransformer.
     */
    @Test
    public void testExcluded()
    {
        String[] excluded = new String[]{"ZIP"};
        String[] result;
        
        CoerceStringsTransformer instance = new CoerceStringsTransformer();
        instance.setExcluded(excluded);
        
        result = instance.getExcluded();      
        assertArrayEquals(excluded, result);
    }

    /**
     * Test of coerce method, of class CoerceStringsTransformer.
     */
    @Test
    public void testCoerce()
    {
        String value = "true";
        Object expected = true;
        Object result;
        
        CoerceStringsTransformer instance = new CoerceStringsTransformer();
        
        result = instance.coerce(value);
        assertEquals(expected, result); 
    }

    /**
     * Test of transform method, of class CoerceStringsTransformer.
     */
    @Test
    public void testTransform()
    {
        JSONObject record;
        JSONObject result;
        CoerceStringsTransformer allEnabled;
        CoerceStringsTransformer selective;
        CoerceStringsTransformer filtered;
        String[] included;
        String[] excluded;

        // Test various string conversions
        record = new JSONObject();
        record.put("boolTrue", "true");
        record.put("boolFalse", "false");
        record.put("integer", "123");
        record.put("longNumber", "9999999999");
        record.put("double", "123.45");
        record.put("text", "hello");
        record.put("nullValue", null);
        record.put("nonString", 42);
        record.put("negativeInt", "-123");
        record.put("negativeDouble", "-123.45");
        record.put("zipCode", "12345");

        // Test with all conversions enabled
        allEnabled = new CoerceStringsTransformer();
        result = allEnabled.transform(record);

        assertEquals(true, result.get("boolTrue"));
        assertEquals(false, result.get("boolFalse"));
        assertEquals(123, result.get("integer"));
        assertEquals(9999999999L, result.get("longNumber"));
        assertEquals(123.45, result.get("double"));
        assertEquals("hello", result.get("text"));
        assertNull(result.get("nullValue"));
        assertEquals(42, result.get("nonString"));
        assertEquals(-123, result.get("negativeInt"));
        assertEquals(-123.45, result.get("negativeDouble"));
        assertEquals(12345, result.get("zipCode"));

        // Test with selective type conversion
        selective = new CoerceStringsTransformer(false, true, false);
        record = new JSONObject();
        record.put("boolTrue", "true");
        record.put("double", "123.45");
        record.put("integer", "123");

        result = selective.transform(record);
        assertEquals("true", result.get("boolTrue")); // boolean conversion disabled
        assertEquals(123.45, result.get("double")); // double conversion enabled
        assertEquals("123", result.get("integer")); // integer conversion disabled

        // Test with field filtering
        included = new String[]
        {
            "double", "integer"
        };
        excluded = new String[]
        {
            "integer"
        };
        filtered = new CoerceStringsTransformer(true, true, true, included, excluded);

        record = new JSONObject();
        record.put("boolTrue", "true");
        record.put("double", "123.45");
        record.put("integer", "123");

        result = filtered.transform(record);
        assertEquals("true", result.get("boolTrue")); // not in included list
        assertEquals(123.45, result.get("double")); // in included list
        assertEquals("123", result.get("integer")); // in excluded list

        // Test edge cases
        record = new JSONObject();
        record.put("emptyString", "");
        record.put("invalidNumber", "123abc");
        record.put("invalidDouble", "123.456.789");

        result = allEnabled.transform(record);
        assertEquals("", result.get("emptyString"));
        assertEquals("123abc", result.get("invalidNumber"));
        assertEquals("123.456.789", result.get("invalidDouble"));
    }
    
}
