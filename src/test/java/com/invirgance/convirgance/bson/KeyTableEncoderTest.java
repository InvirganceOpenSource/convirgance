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
package com.invirgance.convirgance.bson;

import com.invirgance.convirgance.jbin.KeyTableEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class KeyTableEncoderTest
{
    @Test
    public void testEncoding() throws Exception
    {
        KeyTableEncoder encoder = new KeyTableEncoder();
        
        assertEquals(0, encoder.get("zero"));
        assertEquals(1, encoder.get("one"));
        assertEquals(2, encoder.get("two"));
        
        assertEquals(3, encoder.size());
        assertEquals("zero", encoder.get(0));
        assertEquals("one", encoder.get(1));
        assertEquals("two", encoder.get(2));
        
        assertEquals(0, encoder.get("zero"));
        assertEquals(1, encoder.get("one"));
        assertEquals(2, encoder.get("two"));
        assertEquals(3, encoder.get("three"));
        
        assertEquals(4, encoder.size());
        assertEquals("zero", encoder.get(0));
        assertEquals("one", encoder.get(1));
        assertEquals("two", encoder.get(2));
        assertEquals("three", encoder.get(3));
        
        encoder.reset(null);
        
        assertEquals(0, encoder.size());
        assertEquals(0, encoder.get("0"));
        assertEquals(1, encoder.get("1"));
        assertEquals(2, encoder.get("2"));
        assertEquals(3, encoder.get("3"));
    }
    
    @Test
    public void testFailures() throws Exception
    {
        KeyTableEncoder encoder = new KeyTableEncoder();
        
        try
        {
            encoder.get(0);
            
            fail("Lookup should have failed.");
        }
        catch(IllegalArgumentException e)
        {
            assertEquals("Key 0 does not exist. 0 keys are available.", e.getMessage());
        }
        
        try
        {
            for(int i=0; i<70000; i++) encoder.get(Integer.toString(i));
            
            fail("Lookup should have failed.");
        }
        catch(IllegalStateException e)
        {
            assertEquals("Maximum number of keys (65535) has been exceeded by key [65535]", e.getMessage());
        }
    }
    
    @Test
    public void testReadWrite() throws Exception
    {
        KeyTableEncoder encoder = new KeyTableEncoder();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        assertEquals(0, encoder.get("zero"));
        assertEquals(1, encoder.get("one"));
        assertEquals(2, encoder.get("two"));
        assertEquals(3, encoder.get("three"));
        
        encoder.write(new DataOutputStream(out));
        encoder.reset(null);
        
        assertEquals(0, encoder.size());
    
        encoder.read(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
        
        assertEquals(4, encoder.size());
        assertEquals("zero", encoder.get(0));
        assertEquals("one", encoder.get(1));
        assertEquals("two", encoder.get(2));
        assertEquals("three", encoder.get(3));
    }
}
