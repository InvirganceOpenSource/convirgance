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
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class EpochDateTransformerTest
{
    
    public EpochDateTransformerTest()
    {
    }

    @Test
    public void testTransform()
    {
        // 1747612800000L == 2025-05-19
        
        JSONObject record;
        EpochDateTransformer unfiltered;
        
        record = new JSONObject();
        record.put("Item", "Pen");
        record.put("Created", 1747612800L);    
        record.put("Destroyed", 1747612800000L);

        unfiltered = new EpochDateTransformer();
        unfiltered.transform(record);

        assertTrue(record.get("Created") instanceof String);
        assertTrue(record.get("Destroyed") instanceof String);
        assertTrue(record.get("Item") instanceof String);
        
        assertEquals("2025-05-19T00:00:00", record.get("Created"));
        assertEquals("2025-05-19T00:00:00", record.get("Destroyed"));
        
        record = new JSONObject();
        record.put("Item", "Pen");
        record.put("Created", 1747612800000L);
        record.put("Destroyed", 1747612800000L);

        unfiltered = new EpochDateTransformer(new String[]{"Created"}, new String[]{"Destroyed","Item"});
        unfiltered.transform(record);

        assertTrue(record.get("Created") instanceof String);
        assertTrue(record.get("Item") instanceof String);
        assertTrue(record.get("Destroyed") instanceof Long);
        assertEquals("2025-05-19T00:00:00", record.get("Created"));
    }
    
    @Test  
    public void testISO8601FormatsToString() 
    {
        JSONObject record = new JSONObject();
        EpochDateTransformer transformer = new EpochDateTransformer();
        
        Long test;
        String expected;
        
        Object[][] formats = {
            { "2025-05-19T09:00:00.123", Instant.parse("2025-05-19T09:00:00.123Z").toEpochMilli() },
            { "2025-05-19T14:30:00.123", Instant.parse("2025-05-19T14:30:00.123Z").toEpochMilli() },
            { "2025-05-19T14:30:00.123", Instant.parse("2025-05-19T14:30:00.123Z").toEpochMilli() },
            { "2011-12-03T10:15:30", Instant.parse("2011-12-03T10:15:30Z").toEpochMilli() },
            { "2025-05-19T14:30:00", Instant.parse("2025-05-19T14:30:00Z").toEpochMilli() },
            { "2025-05-19T14:30:15", Instant.parse("2025-05-19T14:30:15Z").toEpochMilli() },
            { "2025-05-20T00:00:00", 1747699200L },
        };
        
        for(Object[] date: formats)
        {
            test = (Long) date[1];
            expected = (String) date[0];
            
            record.put("timestamp", test);
            record = transformer.transform(record);     
            
            assertEquals(expected, record.get("timestamp") );
        }
    }
    
}
