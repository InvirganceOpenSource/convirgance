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

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class EpochDateTransformerTest
{
    @Test
    public void testTransformMany()
    {
        Long epoch = new Date().getTime();
        Date expected = new Date(epoch);
        
        JSONObject record = new JSONObject();
        EpochDateTransformer transformer = new EpochDateTransformer("Created", "Destroyed");
       
        // Set up record
        record.put("Item", "Phone");
        record.put("Created", epoch);
        record.put("Destroyed", epoch);
        
        // Apply transformation
        record = transformer.transform(record);
        
        assertEquals("Phone", record.get("Item"));
        assertEquals(expected, record.get("Created"));
        assertEquals(expected, record.get("Destroyed"));
    }
    
    @Test
    public void testTransformOne()
    {
        Long epoch = new Date().getTime();
        Date expected = new Date(epoch);
        
        JSONObject record = new JSONObject();
        EpochDateTransformer transformer = new EpochDateTransformer("Created");
       
        // Set up record
        record.put("Item", "Phone");
        record.put("Created", epoch);
        record.put("Destroyed", epoch);
        
        // Apply transformation
        record = transformer.transform(record);
        
        assertEquals("Phone", record.get("Item"));     
        assertEquals(expected, record.get("Created"));
        assertEquals(epoch, record.get("Destroyed"));
    }
    
    @Test
    public void testException()
    {
        JSONObject record = new JSONObject();
        EpochDateTransformer transformer = new EpochDateTransformer();
        
        try
        {
            record = transformer.transform(record);
            
            fail("Expected a ConvirganceException");
        }
        catch(ConvirganceException e)
        {
            assertEquals("The list of columns to transform must be set!", e.getMessage());
        }
    }
}
