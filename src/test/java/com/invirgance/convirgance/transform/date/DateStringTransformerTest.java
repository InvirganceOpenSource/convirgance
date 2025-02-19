/*
 * The MIT License
 *
 * Copyright 2025 tadghh.
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
package com.invirgance.convirgance.transform.date;

import com.invirgance.convirgance.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class DateStringTransformerTest
{
    
    public DateStringTransformerTest()
    {
    }
    
    @Test
    public void testTransform()
    {
        JSONObject record;
        DateStringTransformer unfiltered;

        record = new JSONObject();
        record.put("Created", "2025-05-19");
        record.put("Destroyed", "2025-05-19");
            
        unfiltered = new DateStringTransformer();
        record = unfiltered.transform(record);
        
        assertTrue(record.get("Created") instanceof String);
        assertTrue(record.get("Destroyed") instanceof String);

        record = new JSONObject();
        record.put("Item", "Phone");
        record.put("Created", "2025-05-19");
        record.put("Destroyed", "2025-05-19");
            
        unfiltered = new DateStringTransformer(new String[]{"Created"}, new String[]{"Destroyed","Item"});
        unfiltered.transform(record);
        
        assertTrue(record.get("Created") instanceof String);     
        assertTrue(record.get("Destroyed") instanceof String);
    }

    @Test  
    public void testISO8601Formats() 
    {
        JSONObject record = new JSONObject();
        DateStringTransformer transformer = new DateStringTransformer();
        String expected;
        String test;
        
        // Expected value on the left [0]
        String[][] formats = {
            { "2025-05-19T09:00:00.123Z", "2025-05-19T14:30:00.123+05:30" },
            { "2025-05-19T14:30:00.123456Z", "2025-05-19T14:30:00.123456Z" },
            { "2025-05-19T14:30:00Z", "2025-05-19T14:30:00+00:00" },
            { "2025-05-19T14:30:00.123Z", "2025-05-19T14:30:00,123Z" },
            { "2011-12-03T10:15:30Z", "2011-12-03T10:15:30Z" },
            { "2025-05-19T14:30:00Z", "2025-05-19T14:30:00" },
            { "2025-05-19T14:30:00Z", "2025-05-19T14:30" },
            { "2025-05-20T00:00:00Z", "2025-W21-2" },
            { "2012-11-17T00:00:00Z", "2012-11-17" },
            { "2025-01-27T00:00:00Z", "2025-W05-1" },
            { "2025-05-19T00:00:00Z", "2025-W212" },
            { "2025-05-19T00:00:00Z", "2025-W21" },
            { "2012-12-02T00:00:00Z", "2012-337" },
            { "2025-05-19T00:00:00Z", "2025W212" },
            { "2012-11-17T00:00:00Z", "20121117" },
            { "2012-04-21T00:00:00Z", "2012-112" },
            { "2012-12-02T00:00:00Z", "2012337" },
            { "2025-05-19T00:00:00Z", "2025W21" },
            { "2012-04-21T00:00:00Z", "2012112" },
            { "2012-11-01T00:00:00Z", "2012-11" },
            { "2012-11-01T00:00:00Z", "201211" },
            { "2012-01-01T00:00:00Z", "2012" },
            { "2025-05-19T00:00:00Z", "20250519" },
            { "2025-05-19T14:30:00Z", "20250519T143000" },
            { "2025-05-19T14:30:00Z", "20250519T143000Z" },
            { "2025-05-19T14:30:00.123Z", "20250519T143000.123" },
            { "2025-05-19T14:30:00.123456Z", "20250519T143000.123456" },
            { "2025-05-19T14:30:00.123Z", "20250519T14:30:00,123Z" },
            { "2025-05-19T09:00:00.123Z", "20250519T143000.123+0530" }
        };

        
        for(String[] date : formats)
        {
            expected = date[0];
            test = date[1];
            
            record.put("timestamp", test);
            record = transformer.transform(record);
            
            assertEquals(expected, record.get("timestamp"));
        }
    }
    
}
