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
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class DateISOStringTransformerTest
{
    @Test
    public void testTransformAll()
    {
        Date date = new Date(1740087929886L);
        String expected = "2025-02-20T21:45:29.886Z";

        JSONObject record = new JSONObject();
        DateISOStringTransformer transformer = new DateISOStringTransformer();

        // Set up record
        record.put("Item", "Phone");
        record.put("Created", date);
        record.put("Destroyed", date);
        
        // Apply transformation
        record = transformer.transform(record);
        
        assertEquals("Phone", record.get("Item"));   
        assertEquals(expected, record.get("Created"));
        assertEquals(expected, record.get("Destroyed"));
    }

    @Test
    public void testTransformOne()
    {
        Date date = new Date(1740087929886L);
        String expected = "2025-02-20T21:45:29.886Z";

        JSONObject record = new JSONObject();
        DateISOStringTransformer transformer = new DateISOStringTransformer("Created");

        // Set up record
        record.put("Item", "Phone");
        record.put("Created", date);
        record.put("Destroyed", date);
        
        // Apply transformation
        transformer.transform(record);
        
        assertEquals("Phone", record.get("Item"));    
        assertEquals(expected, record.get("Created"));
        assertEquals(date, record.get("Destroyed"));
    }
}
