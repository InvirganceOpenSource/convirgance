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
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class ISOStringDateTransformerTest
{
    @Test
    public void testTransformMany()
    {
        Long epoch = 1740087929L;
        Date date = new Date(epoch * 1000);
        String test = "2025-02-20T21:45:29Z";
        
        JSONObject record = new JSONObject();
        ISOStringDateTransformer transformer = new ISOStringDateTransformer("Created", "Destroyed");

        record.put("Item", "Phone");
        record.put("Created", test);
        record.put("Destroyed", test);
        
        record = transformer.transform(record);

        assertEquals("Phone", record.get("Item"));     
        assertEquals(date, record.get("Created"));
        assertEquals(date, record.get("Destroyed"));
    }
    
    @Test
    public void testTransformOne()
    {
        Long epoch = 1740087929L;
        Date date = new Date(epoch * 1000);
        String test = "2025-02-20T21:45:29Z";
        
        JSONObject record = new JSONObject();
        ISOStringDateTransformer transformer = new ISOStringDateTransformer("Created");

        record.put("Item", "Phone");
        record.put("Created", test);
        record.put("Destroyed", test);
        
        record = transformer.transform(record);
        
        assertEquals("Phone", record.get("Item"));     
        assertEquals(date, record.get("Created"));
        assertEquals(test, record.get("Destroyed"));
    }
}
