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

import com.invirgance.convirgance.jbin.StringEncoder;
import com.invirgance.convirgance.jbin.KeyTableEncoder;
import com.invirgance.convirgance.jbin.BinaryEncoder;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class BinaryEncoderTest
{

    @Test
    public void testWriteNull() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        BinaryEncoder writer = new BinaryEncoder();
        
        writer.write(null, out);

        assertEquals(1, buffer.size());
        assertEquals(BinaryEncoder.TYPE_NULL, buffer.toByteArray()[0]);
    }
    
    @Test
    public void testWriteString() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        BinaryEncoder writer = new BinaryEncoder();
        DataInputStream in;
        
        writer.write("Hello World!", out);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        
        assertEquals(17, buffer.size());
        assertEquals(StringEncoder.STRING_REGISTER_OPERATION, in.read());
        assertEquals("Hello World!", in.readUTF());
        assertEquals(BinaryEncoder.TYPE_STRING, in.read());
        assertEquals(0, in.read());
    }
    
    @Test
    public void testWriteNumber() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        BinaryEncoder writer = new BinaryEncoder();
        DataInputStream in;
        
        writer.write((byte)1, out);
        writer.write((short)2, out);
        writer.write(3, out);
        writer.write(456789L, out);
        writer.write(1.23f, out);
        writer.write(1.2345, out);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        
        assertEquals(30, buffer.size());
        assertEquals(BinaryEncoder.TYPE_BYTE, in.read());
        assertEquals(1, in.read());
        assertEquals(BinaryEncoder.TYPE_SHORT, in.read());
        assertEquals(2, in.readShort());
        assertEquals(BinaryEncoder.TYPE_INTEGER_U8, in.read());
        assertEquals(3, in.readUnsignedByte());
        assertEquals(BinaryEncoder.TYPE_LONG, in.read());
        assertEquals(456789L, in.readLong());
        assertEquals(BinaryEncoder.TYPE_FLOAT, in.read());
        assertEquals(1.23f, in.readFloat(), 0.000001);
        assertEquals(BinaryEncoder.TYPE_DOUBLE, in.read());
        assertEquals(1.2345, in.readDouble(), 0.000001);
    }
    
    @Test
    public void testBoolean() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        BinaryEncoder writer = new BinaryEncoder();
        DataInputStream in;
        
        writer.write(true, out);
        writer.write(false, out);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        
        assertEquals(2, buffer.size());
        assertEquals(BinaryEncoder.TYPE_BOOLEAN_TRUE, in.read());
        assertEquals(BinaryEncoder.TYPE_BOOLEAN_FALSE, in.read());
    }
    
    @Test
    public void testObject() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        BinaryEncoder writer = new BinaryEncoder(new KeyTableEncoder());
        DataInputStream in;
        
        writer.write(new JSONObject("{\"x\":1, \"y\":\"Hi\", \"z\":{\"x\":true, \"y\":false, \"z\":1.23}}"), out);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        
        assertEquals(38, buffer.size());
        
        assertEquals(BinaryEncoder.TYPE_OBJECT, in.read());
        assertEquals(3, in.readUnsignedShort());   // 3 keys in object
        assertEquals(0, in.readShort()); // x
        assertEquals(1, in.readShort()); // y
        assertEquals(2, in.readShort()); // z
        
        assertEquals(BinaryEncoder.TYPE_INTEGER_U8, in.read());
        assertEquals(1, in.readUnsignedByte());
        
        assertEquals(StringEncoder.STRING_REGISTER_OPERATION, in.read());
        assertEquals("Hi", in.readUTF());
        
        assertEquals(BinaryEncoder.TYPE_STRING, in.read());
        assertEquals(0, in.read());
        
        assertEquals(BinaryEncoder.TYPE_OBJECT, in.read());
        assertEquals(3, in.readUnsignedShort());   // 3 keys in object
        assertEquals(0, in.readShort()); // x
        assertEquals(1, in.readShort()); // y
        assertEquals(2, in.readShort()); // z
        
        assertEquals(BinaryEncoder.TYPE_BOOLEAN_TRUE, in.readByte());
        assertEquals(BinaryEncoder.TYPE_BOOLEAN_FALSE, in.readByte());
        
        assertEquals(BinaryEncoder.TYPE_DOUBLE, in.readByte());
        assertEquals(1.23, in.readDouble(), 0.0000001);
    }
    
    @Test
    public void testArray() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        BinaryEncoder writer = new BinaryEncoder(new KeyTableEncoder());
        DataInputStream in;
        
        writer.write(new JSONArray("[123, true, {\"x\":\"Hi\", \"y\": []}]"), out);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        
        assertEquals(27, buffer.size());
        assertEquals(BinaryEncoder.TYPE_ARRAY, in.read());
        assertEquals(3, in.readInt());
        
        assertEquals(BinaryEncoder.TYPE_INTEGER_U8, in.read());
        assertEquals(123, in.readUnsignedByte());
        
        assertEquals(BinaryEncoder.TYPE_BOOLEAN_TRUE, in.read());
        
        assertEquals(BinaryEncoder.TYPE_OBJECT, in.read());
        assertEquals(2, in.readUnsignedShort());   // 2 values in the object
        assertEquals(0, in.readShort()); // x
        assertEquals(1, in.readShort()); // y
        
        assertEquals(StringEncoder.STRING_REGISTER_OPERATION, in.read());
        assertEquals("Hi", in.readUTF());
        
        assertEquals(BinaryEncoder.TYPE_STRING, in.read());
        assertEquals(0, in.readByte());
        
        assertEquals(BinaryEncoder.TYPE_ARRAY, in.read());
        assertEquals(0, in.readInt());
    }
    
    @Test
    public void testHeader() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        KeyTableEncoder keys = new KeyTableEncoder();
        BinaryEncoder writer = new BinaryEncoder(keys);
        DataInputStream in;
        
        writer.write(new JSONObject("{\"x\":1, \"y\":\"Hi\", \"z\":{\"x\":true, \"y\":false, \"z\":1.23}}"), out);
        buffer.reset();
        keys.write(out);
        
        in = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        
        assertEquals(11, buffer.size());
        assertEquals(3, in.readUnsignedShort());
        assertEquals("x", in.readUTF());
        assertEquals("y", in.readUTF());
        assertEquals("z", in.readUTF());
    }
    
    @Test
    public void testCLOB() throws Exception
    {
        StringBuffer buffer = new StringBuffer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = new BinaryEncoder();
        
        DataInputStream in;
        byte[] data = new byte[230000];
        
        for(int i=0; i<10000; i++)
        {
            buffer.append("0123456789ABCDEFabcdef\n");
        }

        encoder.write(buffer.toString(), new DataOutputStream(out));
        
        in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        
        assertEquals(230005, out.size());
        assertEquals(BinaryEncoder.TYPE_CLOB, in.read());
        assertEquals(230000, in.readInt());
        
        in.readFully(data);
        
        assertEquals(buffer.toString(), new String(data, "UTF-8"));
    }
    
    @Test
    public void testDate() throws Exception
    {
        long expected = System.currentTimeMillis();
        
        StringBuffer buffer = new StringBuffer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = new BinaryEncoder();
        DataInputStream in;

        encoder.write(new Date(expected), new DataOutputStream(out));
        
        in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        
        assertEquals(9, out.size());
        assertEquals(BinaryEncoder.TYPE_DATE, in.read());
        assertEquals(expected, in.readLong());
    }
}
