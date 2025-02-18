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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    public void testTransform()
    {
        JSONObject record;
        DateEpochTransformer unfiltered;

        record = new JSONObject();
        record.put("Item", "Pen");
        record.put("Created", "2025-05-19");
        record.put("Destoryed", "2025-05-19");
            
        unfiltered = new DateEpochTransformer();
        record = unfiltered.transform(record);
        
        assertTrue(record.get("Created") instanceof Long);
        assertTrue(record.get("Destoryed") instanceof Long);
        assertFalse(record.get("Item") instanceof Long);

        record = new JSONObject();
        record.put("Item", "Pen");
        record.put("Created", "2025-05-19");
        record.put("Destoryed", "2025-05-19");
            
        unfiltered = new DateEpochTransformer(new String[]{"Created"}, new String[]{"Destoryed","Item"});
        record = unfiltered.transform(record);
        
        assertTrue(record.get("Created") instanceof Long);
        assertFalse(record.get("Item") instanceof Long);
        assertFalse(record.get("Destoryed") instanceof Long);
    }
    
    @Test  
    public void testISO8601Formats() 
    {
        JSONObject record = new JSONObject();
        DateEpochTransformer transformer = new DateEpochTransformer();

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

        for(String dateStr : formats) 
        {
            record.put("timestamp", dateStr);
            record = transformer.transform(record);   
            assertTrue(record.get("timestamp") instanceof Long);
        }
    }
}
