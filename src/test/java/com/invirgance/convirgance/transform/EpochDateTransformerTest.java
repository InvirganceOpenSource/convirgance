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
package com.invirgance.convirgance.transform;

import com.invirgance.convirgance.json.JSONObject;
import java.util.ArrayList;
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

        record.put("Created", 1747612800000L);    
        record.put("Destroyed", 1747612800000L);

        unfiltered = new EpochDateTransformer();
        unfiltered.transform(record);

        assertTrue(record.get("Created") instanceof String);
        assertTrue(record.get("Destroyed") instanceof String);
        assertTrue(record.get("Item") instanceof String);
        
        assertEquals("2025-05-19T00:00", record.get("Created"));
        assertEquals("2025-05-19T00:00", record.get("Destroyed"));

        
        record = new JSONObject();
        record.put("Item", "Pen");
        record.put("Created", 1747612800000L);
        record.put("Destroyed", 1747612800000L);

        unfiltered = new EpochDateTransformer(new String[]{"Created"}, new String[]{"Destroyed","Item"});
        unfiltered.transform(record);

        assertTrue(record.get("Created") instanceof String);
        assertTrue(record.get("Item") instanceof String);
        assertTrue(record.get("Destroyed") instanceof Long);
        assertEquals("2025-05-19T00:00", record.get("Created"));
    }
    
    @Test  
    public void testISO8601FormatsToString() 
    {
        JSONObject record;
        DateEpochTransformer transformer = new DateEpochTransformer();
        EpochDateTransformer other = new EpochDateTransformer();
        ArrayList<JSONObject> test = new ArrayList<>();
        
        String[] formats =
        {
            "2025-05-19T14:30:00.123+05:30",
            "2025-05-19T14:30:00.123456Z",
            "2025-05-19T14:30:00+00:00",
            "2025-05-19T14:30:00,123Z",
            "2011-12-03T10:15:30Z",
            "2025-05-19T14:30:00",
            "2025-05-19T14:30",
            "2025-W21-2",
            "2012-11-17",
            "2025-W05-1",
            "2025-W212",
            "2025-W21",
            "2012-337",
            "2025W212",
            "20121117",
            "2012-112",
            "2012337",
            "2025W21",
            "2012112",
            "2012-11",
            "201211",
            "2012",
            "20250519",
            "20250519T143000",
            "20250519T143000Z",
            "20250519T143000.123",
            "20250519T143000.123456",
            "20250519T14:30:00,123Z",
            "20250519T143000.123+0530",
        };

        for(String date : formats) 
        {
            record = new JSONObject();
            
            record.put("timestamp", date);
            record = transformer.transform(record);
            
            test.add(record);
        }
        
        for(JSONObject item: test)
        {
            assertTrue(item.get("timestamp") instanceof Long);
            
            other.transform(item);
            
            assertTrue(item.get("timestamp") instanceof String);         
        }
    }
    
}
