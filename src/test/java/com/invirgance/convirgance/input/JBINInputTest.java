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
package com.invirgance.convirgance.input;

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.output.JBINOutput;
import com.invirgance.convirgance.output.OutputCursor;
import com.invirgance.convirgance.source.ByteArraySource;
import com.invirgance.convirgance.target.ByteArrayTarget;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class JBINInputTest
{
    @Test
    public void testOne() throws Exception
    {
        ByteArrayTarget target = new ByteArrayTarget();
        BSONInput input = new BSONInput();
        JBINOutput output = new JBINOutput();
        
        try(OutputCursor cursor = output.write(target))
        {
            cursor.write(new JSONObject("{\"x\":true}"));
        }
        
        for(JSONObject record : input.read(new ByteArraySource(target.getBytes())))
        {
            assertTrue(record.getBoolean("x"));
        }
    }
    
    @Test
    public void testMultiple() throws Exception
    {
        ByteArrayTarget target = new ByteArrayTarget();
        BSONInput input = new BSONInput();
        JBINOutput output = new JBINOutput();
        
        String[] keys = new String[]{"x", "y", "z"};
        int index = 0;
        
        try(OutputCursor cursor = output.write(target))
        {
            cursor.write(new JSONObject("{\"x\":1}"));
            cursor.write(new JSONObject("{\"y\":2}"));
            cursor.write(new JSONObject("{\"z\":3}"));
        }
        
        for(JSONObject record : input.read(new ByteArraySource(target.getBytes())))
        {
            assertEquals(index+1, record.getInt(keys[index]));
            
            index++;
        }
    }
}
