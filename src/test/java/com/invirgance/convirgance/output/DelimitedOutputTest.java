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
package com.invirgance.convirgance.output;

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.target.ByteArrayTarget;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jbanes
 */
public class DelimitedOutputTest
{
    
    @Test
    public void testSingleRecord() throws Exception
    {
        String expected = "Column 1|Column 2|Column 3" + System.lineSeparator() +
                          "Value 1|Value 2|Value 3" + System.lineSeparator();
        
        DelimitedOutput output = new DelimitedOutput(new String[]{"Column 1", "Column 2", "Column 3"});
        ByteArrayTarget target = new ByteArrayTarget();
        JSONObject record = new JSONObject();
        
        record.put("Column 1", "Value 1");
        record.put("Column 2", "Value 2");
        record.put("Column 3", "Value 3");
        
        try(OutputCursor cursor = output.write(target))
        {
            cursor.write(record);
        }
        
        assertEquals(expected, new String(target.getBytes(), "UTF-8"));
    }
    
    @Test
    public void testNullEncoding() throws Exception
    {
        String expected = "Column 1|Column 2|Column 3" + System.lineSeparator() +
                          "Value 1|Value 2|Value 3" + System.lineSeparator() +
                          "Value 1||Value 3" + System.lineSeparator() +
                          "Value 1||" + System.lineSeparator() +
                          "||" + System.lineSeparator();
        
        DelimitedOutput output = new DelimitedOutput(new String[]{"Column 1", "Column 2", "Column 3"});
        ByteArrayTarget target = new ByteArrayTarget();
        JSONObject record = new JSONObject();
        
        record.put("Column 1", "Value 1");
        record.put("Column 2", "Value 2");
        record.put("Column 3", "Value 3");
        
        try(OutputCursor cursor = output.write(target))
        {
            record.put("Column 1", "Value 1");
            record.put("Column 2", "Value 2");
            record.put("Column 3", "Value 3");
        
            cursor.write(record);
            
            record.remove("Column 2");
        
            cursor.write(record);
            
            record.remove("Column 3");
        
            cursor.write(record);
            
            record.remove("Column 1");
        
            cursor.write(record);
        }
        
        assertEquals(expected, new String(target.getBytes(), "UTF-8"));
    }
}
