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
public class DateEpochTransformerTest
{
    
    public DateEpochTransformerTest()
    {
    }
    
    @Test
    public void testTransformAll()
    {
        Date date = new Date();
        JSONObject record = new JSONObject();
        DateEpochTransformer transformer = new DateEpochTransformer();
        
        // Setup record
        record.put("Item", "Phone");
        record.put("Created", date);
        record.put("Destroyed", date);
        
        // Apply transformation
        record = transformer.transform(record);
        
        assertEquals("Phone", record.get("Item"));
        assertEquals(date.getTime(), record.get("Created"));
        assertEquals(date.getTime(), record.get("Destroyed"));
    }
    
    @Test
    public void testTransformOne()
    {
        String[] included = {"Created"};
        Date date = new Date();
        
        JSONObject record = new JSONObject();
        DateEpochTransformer transformer = new DateEpochTransformer(included);
        
        // Setup record
        record.put("Item", "Phone");
        record.put("Created", date);
        record.put("Destroyed", date);
        
        // Apply transformation
        record = transformer.transform(record);
        
        assertEquals("Phone", record.get("Item"));     
        assertEquals(date.getTime(), record.get("Created"));
        assertEquals(date, record.get("Destroyed"));
        assertTrue(record.get("Destroyed") instanceof Date);
    }
    
}
