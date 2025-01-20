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
package com.invirgance.convirgance.jbin;

import com.invirgance.convirgance.jbin.BinaryDecoder;
import com.invirgance.convirgance.jbin.KeyEncoder;
import com.invirgance.convirgance.jbin.KeyStreamEncoder;
import com.invirgance.convirgance.jbin.BinaryEncoder;
import com.invirgance.convirgance.json.JSONObject;
import java.io.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class KeyStreamEncoderTest
{
    
    @Test
    public void testEncoding() throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(out);
        KeyStreamEncoder encoder = new KeyStreamEncoder();
        
        assertEquals(0, encoder.write("zero", data));
        assertEquals(1, encoder.write("one", data));
        assertEquals(2, encoder.write("two", data));
        assertEquals(19, out.size());
        assertEquals(3, encoder.size());
        
        assertEquals("zero", encoder.get(0));
        assertEquals("one", encoder.get(1));
        assertEquals("two", encoder.get(2));
        
        assertEquals(0, encoder.write("zero", data));
        assertEquals(1, encoder.write("one", data));
        assertEquals(2, encoder.write("two", data));
        assertEquals(3, encoder.write("three", data));
        assertEquals(27, out.size());
        assertEquals(4, encoder.size());
        
        assertEquals("zero", encoder.get(0));
        assertEquals("one", encoder.get(1));
        assertEquals("two", encoder.get(2));
        assertEquals("three", encoder.get(3));
        
        encoder.reset(data);
        assertEquals(28, out.size());
        assertEquals(0, encoder.size());
        
        assertEquals(0, encoder.write("0", data));
        assertEquals(1, encoder.write("1", data));
        assertEquals(2, encoder.write("2", data));
        assertEquals(3, encoder.write("3", data));
        assertEquals(44, out.size());
        assertEquals(4, encoder.size());
    }
    
    @Test
    public void testInlineEncoding() throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(out);
        
        KeyEncoder keys = new KeyStreamEncoder();
        BinaryEncoder encoder = new BinaryEncoder(keys);
        BinaryDecoder decoder = new BinaryDecoder(keys);
        
        byte[] result;
        
        encoder.write(new JSONObject("{\"x\":true}"), data);
        
        result = out.toByteArray();
        
        assertEquals(10, out.size());
        assertEquals(new JSONObject("{\"x\":true}"), decoder.read(new DataInputStream(new ByteArrayInputStream(result))));
    }
}
