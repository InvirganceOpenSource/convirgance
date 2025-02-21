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
public class StringDateTransformerTest
{
    
    public StringDateTransformerTest()
    {
    }

    /**
     * Test of transformAction method, of class StringDateTransformer.
     * Strings representing dates should be transformed into Date objects
     */
    @Test
    public void testTransformAction()
    {
        String[] included = {"Created"};
        String[] excluded = {"Destroyed","Item"};
     
        Long epoch = 1740087929L;
        Date date = new Date(epoch * 1000);
        String test = "2025-02-20T21:45:29Z";
        String expected = date.toString();
        
        JSONObject record = new JSONObject();
        StringDateTransformer transformer = new StringDateTransformer();

        // Test basic value transformation
//record.put("Item", "Phone"); The user would know not the try and convert their Item fields into dates

        record.put("Created", test);
        record.put("Destroyed", test);
        
        record = transformer.transform(record);
        assertEquals(expected, record.get("Created").toString());
        assertEquals(expected, record.get("Destroyed").toString());
        
        // Verify that excluded values remain unchanged
        record = new JSONObject();
        record.put("Item", "Phone");
        record.put("Created", test);
        record.put("Destroyed", test);
            
        transformer = new StringDateTransformer(included, excluded);
        transformer.transform(record);
        
        assertTrue(record.get("Item") instanceof String);     
        assertEquals(expected, record.get("Created").toString());
        assertTrue(record.get("Created") instanceof Date);
        assertTrue(record.get("Destroyed") instanceof String);
    }
    
}
