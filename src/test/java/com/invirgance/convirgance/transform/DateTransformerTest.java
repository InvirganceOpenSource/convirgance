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

import com.invirgance.convirgance.json.JSONObject;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class DateTransformerTest
{
    
    public DateTransformerTest()
    {
    }
    
    @Test
    public void testInclusionAndExclusion()
    {
        JSONObject record;
        DateTransformer transformer;
        Long epoch = 1740087929886L;
        Date date = new Date(epoch);
        System.out.println(date.toString());
        // Verify non Date fields remain unchanged
        record = new JSONObject();
        record.put("Item", "Phone");
        record.put("Created", date);
        record.put("Destroyed", date);
            
        transformer = new DateTransformer();
        transformer.setConvertToString(true);
        record = transformer.transform(record);
        
        assertTrue(record.get("Created") instanceof String);
        assertTrue(record.get("Destroyed") instanceof String);
        
        // Verify that excluded values remain unchanged
        record = new JSONObject();
        record.put("Item", "Phone");
        record.put("Created", date);
        record.put("Destroyed", date);
            
        transformer = new DateTransformer(new String[]{"Created"}, new String[]{"Destroyed","Item"});
        transformer.setConvertToString(true);
        transformer.transform(record);
        
        assertTrue(record.get("Item") instanceof String);     
        assertTrue(record.get("Created") instanceof String);
        assertTrue(record.get("Destroyed") instanceof Date);
    }

    @Test  
    public void testTransformSpecifiedFormats() 
    {
        JSONObject record = new JSONObject();
        DateTransformer transformer = new DateTransformer();
        Long epoch = 1740087929886L;
        Date date = new Date(epoch);
        String expected = date.toString();

        // Converting from Date to String
        transformer.setConvertToString(true);
        record.put("timestamp", date);
        record = transformer.transform(record);
        
        assertEquals(expected, record.get("timestamp"));

        // Convert from Date to Long (epoch)
        transformer.setConvertToNumber(true);
        record.put("timestamp", date);
        record = transformer.transform(record);
        
        assertEquals(epoch, record.get("timestamp"));

        // Convert from String to Date
        transformer.setConvertFromString(true);
        record.put("timestamp", date.toString());
        record = transformer.transform(record);
        
        assertEquals(expected, record.get("timestamp").toString());
        assertTrue(record.get("timestamp") instanceof Date);

        // Convert from Number to Date
        transformer.setConvertFromNumber(true);
        record.put("timestamp", epoch);
        record = transformer.transform(record);
        
        assertEquals(expected, record.get("timestamp").toString());
        assertTrue(record.get("timestamp") instanceof Date);
    }
    
}
