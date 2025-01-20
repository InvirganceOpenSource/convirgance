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

import com.invirgance.convirgance.jbin.BinaryDecoder;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.target.ByteArrayTarget;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jbanes
 */
public class JBINOutputTest
{
    @Test
    public void testEmpty() throws Exception
    {
        ByteArrayTarget target = new ByteArrayTarget();
        JBINOutput output = new JBINOutput(false);
        byte[] data;
        
        try(OutputCursor cursor = output.write(target))
        {
            // Doing nothing at all :)
        }
        
        data = target.getBytes();
        
        assertEquals(9, data.length);
        assertEquals(0xFF, data[0] & 0xFF);
        assertEquals(0xFF, data[1] & 0xFF);
        assertEquals('J',  data[2] & 0xFF);
        assertEquals('B',  data[3] & 0xFF);
        assertEquals('I',  data[4] & 0xFF);
        assertEquals('N',  data[5] & 0xFF);
        assertEquals(0x01, data[6] & 0xFF);
        assertEquals(0x00, data[7] & 0xFF);
        assertEquals(0xFF, data[8] & 0xFF);
    }
    
    @Test
    public void testOne() throws Exception
    {
        ByteArrayTarget target = new ByteArrayTarget();
        JBINOutput output = new JBINOutput(false);
        byte[] data;
        
        DataInputStream in;
        
        try(OutputCursor cursor = output.write(target))
        {
            cursor.write(new JSONObject("{\"x\":true}"));
        }
        
        data = target.getBytes();
        in = new DataInputStream(new ByteArrayInputStream(data));
        
        // Verify header
        assertTrue(data.length > 8);
        assertEquals(0xFF, in.read() & 0xFF);
        assertEquals(0xFF, in.read() & 0xFF);
        assertEquals('J',  in.read() & 0xFF);
        assertEquals('B',  in.read() & 0xFF);
        assertEquals('I',  in.read() & 0xFF);
        assertEquals('N',  in.read() & 0xFF);
        assertEquals(0x01, in.read() & 0xFF);
        assertEquals(0x00, in.read() & 0xFF);
        
        assertEquals(new JSONObject("{\"x\":true}"), new BinaryDecoder().read(in));
        assertEquals(0xFF, in.read());
        assertEquals(-1, in.read());
    }
    
    @Test
    public void testMultiple() throws Exception
    {
        ByteArrayTarget target = new ByteArrayTarget();
        JBINOutput output = new JBINOutput(false);
        byte[] data;
        
        DataInputStream in;
        BinaryDecoder decoder;
        
        try(OutputCursor cursor = output.write(target))
        {
            cursor.write(new JSONObject("{\"x\":1}"));
            cursor.write(new JSONObject("{\"y\":2}"));
            cursor.write(new JSONObject("{\"z\":3}"));
        }
        
        data = target.getBytes();
        in = new DataInputStream(new ByteArrayInputStream(data));
        decoder = new BinaryDecoder();
        
        // Verify header
        assertTrue(data.length > 8);
        assertEquals(0xFF, in.read() & 0xFF);
        assertEquals(0xFF, in.read() & 0xFF);
        assertEquals('J',  in.read() & 0xFF);
        assertEquals('B',  in.read() & 0xFF);
        assertEquals('I',  in.read() & 0xFF);
        assertEquals('N',  in.read() & 0xFF);
        assertEquals(0x01, in.read() & 0xFF);
        assertEquals(0x00, in.read() & 0xFF);
        
        assertEquals(new JSONObject("{\"x\":1}"), decoder.read(in));
        assertEquals(new JSONObject("{\"y\":2}"), decoder.read(in));
        assertEquals(new JSONObject("{\"z\":3}"), decoder.read(in));
        assertEquals(0xFF, in.read());
        assertEquals(-1, in.read());
    }
}
