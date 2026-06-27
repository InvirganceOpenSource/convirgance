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
import com.invirgance.convirgance.jbin.KeyTableEncoder;
import com.invirgance.convirgance.jbin.BinaryEncoder;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class BinaryDecoderTest
{

    @Test
    public void testNull() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        BinaryEncoder writer = new BinaryEncoder();
        BinaryDecoder decoder = new BinaryDecoder();
        DataInputStream in;
        
        writer.write(null, out);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        
        assertEquals(1, buffer.size());
        assertNull(decoder.read(in));
    }

    @Test
    public void testString() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        BinaryEncoder writer = new BinaryEncoder();
        BinaryDecoder decoder = new BinaryDecoder();
        DataInputStream in;
        
        writer.write("Hello World!", out);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        
        assertEquals(17, buffer.size());
        assertEquals("Hello World!", decoder.read(in));
    }
    
    @Test
    public void testNumbers() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        BinaryEncoder writer = new BinaryEncoder();
        BinaryDecoder decoder = new BinaryDecoder();
        DataInputStream in;
        
        writer.write((byte)1, out);
        writer.write((short)2, out);
        writer.write((int)3, out);
        writer.write((long)4, out);
        writer.write(1.23f, out);
        writer.write(1.2345, out);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        
        assertEquals(30, buffer.size());
        assertEquals((byte)1, decoder.read(in));
        assertEquals((short)2, decoder.read(in));
        assertEquals((int)3, decoder.read(in));
        assertEquals((long)4, decoder.read(in));
        assertEquals(1.23f, (float)decoder.read(in), 0.000001);
        assertEquals(1.2345, (double)decoder.read(in), 0.000001);
    }

    @Test
    public void testObject() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        
        ByteArrayOutputStream keyBuffer = new ByteArrayOutputStream();
        DataOutputStream keyOut = new DataOutputStream(keyBuffer);
        
        KeyTableEncoder keys = new KeyTableEncoder();
        BinaryEncoder writer = new BinaryEncoder(keys);
        BinaryDecoder decoder = new BinaryDecoder(keys);
        
        DataInputStream in;
        DataInputStream keyIn;
        JSONObject record;
        
        writer.write(new JSONObject("{\"x\":1, \"y\":\"Hi\", \"z\":{\"x\": true, \"y\": false, \"z\": 1.23}}"), out);
        keys.write(keyOut);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        keyIn = new DataInputStream(new ByteArrayInputStream(keyBuffer.toByteArray()));
        
        keys.read(keyIn);        
        
        assertEquals(11, keyBuffer.size());
        assertEquals(3, decoder.getKeyCount());
        
        record = (JSONObject)decoder.read(in);
        
        assertEquals(38, buffer.size());
        assertEquals(3, record.size());
        assertEquals(1, record.get("x"));
        assertEquals("Hi", record.get("y"));
        assertEquals(3, record.getJSONObject("z").size());
        assertEquals(Boolean.TRUE, record.getJSONObject("z").get("x"));
        assertEquals(Boolean.FALSE, record.getJSONObject("z").get("y"));
        assertEquals(1.23, record.getJSONObject("z").get("z"));
    }

    @Test
    public void testArray() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        
        ByteArrayOutputStream keyBuffer = new ByteArrayOutputStream();
        DataOutputStream keyOut = new DataOutputStream(keyBuffer);
        
        KeyTableEncoder keys = new KeyTableEncoder();
        BinaryEncoder writer = new BinaryEncoder(keys);
        BinaryDecoder decoder = new BinaryDecoder(keys);
        
        DataInputStream in;
        DataInputStream keyIn;
        JSONArray record;
        
        writer.write(new JSONArray("[123, true, {\"x\":\"Hi\", \"y\":[]}]"), out);
        keys.write(keyOut);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        keyIn = new DataInputStream(new ByteArrayInputStream(keyBuffer.toByteArray()));
        
        keys.read(keyIn);        
        
        assertEquals(8, keyBuffer.size());
        assertEquals(2, decoder.getKeyCount());
        
        record = (JSONArray)decoder.read(in);
        
        assertEquals(27, buffer.size());
        assertEquals(3, record.size());
        assertEquals(123, record.get(0));
        assertEquals(Boolean.TRUE, record.get(1));
        assertEquals("Hi", record.getJSONObject(2).get("x"));
        assertEquals(0, record.getJSONObject(2).getJSONArray("y").size());
    }
    
    @Test
    public void testCLOB() throws Exception
    {
        StringBuffer buffer = new StringBuffer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = new BinaryEncoder();
        BinaryDecoder decoder = new BinaryDecoder();
        DataInputStream in;
        
        for(int i=0; i<10000; i++)
        {
            buffer.append("0123456789ABCDEFabcdef\n");
        }

        encoder.write(buffer.toString(), new DataOutputStream(out));
        
        in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        
        assertEquals(buffer.toString(), decoder.read(in));
    }
    
    @Test
    public void testDate() throws Exception
    {
        long expected = System.currentTimeMillis();
        
        StringBuffer buffer = new StringBuffer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = new BinaryEncoder();
        BinaryDecoder decoder = new BinaryDecoder();
        DataInputStream in;

        encoder.write(new Date(expected), new DataOutputStream(out));
        
        in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        
        assertEquals(9, out.size());
        assertEquals(new Date(expected), decoder.read(in));
    }
    
    @Test
    public void testBLOB() throws Exception
    {
        byte[] expected = {0x01, 0x02, 0x03, (byte)0xFF};
        byte[] value;
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = new BinaryEncoder();
        BinaryDecoder decoder = new BinaryDecoder();
        DataInputStream in;
        
        encoder.write(expected, new DataOutputStream(out));
        
        in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        value = (byte[])decoder.read(in);
        
        assertEquals(expected.length, value.length);
        assertEquals(expected[0], value[0]);
        assertEquals(expected[1], value[1]);
        assertEquals(expected[2], value[2]);
        assertEquals(expected[3], value[3]);
    }
    
    @Test
    public void testBigInteger() throws Exception
    {
        BigInteger expected = new BigInteger("12345678901234567890");
        
        BinaryEncoder writer = new BinaryEncoder();
        BinaryDecoder decoder = new BinaryDecoder();
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        
        writer.write(expected, out);
        
        assertEquals(expected, decoder.read(new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()))));
    }
    
    @Test
    public void testBigDecimal() throws Exception
    {
        BigDecimal expected = new BigDecimal("1234567890123456789.0123456789");
        
        BinaryEncoder writer = new BinaryEncoder();
        BinaryDecoder decoder = new BinaryDecoder();
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        
        writer.write(expected, out);
        
        assertEquals(expected, decoder.read(new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()))));
    }
}

