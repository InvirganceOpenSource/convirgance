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

import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author jbanes
 */
public class BinaryEncoder
{
    public static final int TYPE_NULL = 0x00;
    public static final int TYPE_STRING = 0x01;
    public static final int TYPE_OBJECT = 0x02;
    public static final int TYPE_ARRAY = 0x03;
    public static final int TYPE_INTEGER = 0x04;
    public static final int TYPE_LONG = 0x05;
    public static final int TYPE_DOUBLE = 0x06;
    public static final int TYPE_DATE = 0x07;
    public static final int TYPE_FLOAT = 0x08;
    public static final int TYPE_SHORT = 0x09;
    public static final int TYPE_BYTE = 0x0A;
    public static final int TYPE_BOOLEAN_TRUE = 'T';
    public static final int TYPE_BOOLEAN_FALSE = 'F';
    public static final int TYPE_CLOB = 0x0B;
    public static final int TYPE_EOF = 0xFF;
    
    private KeyEncoder keys;
    

    public BinaryEncoder()
    {
        this(new KeyStreamEncoder());
    }

    public BinaryEncoder(KeyEncoder keys)
    {
        this.keys = keys;
    }
    
    public Integer getKey(String key)
    {
        return keys.get(key);
    }
    
    public String getKey(int id)
    {
        return keys.get(id);
    }
    
    public int getKeyCount()
    {
        return keys.size();
    }

    public KeyEncoder getKeyEncoder()
    {
        return keys;
    }
    
    private void writeString(String value, DataOutput out) throws IOException
    {
        if(value.length() > Short.MAX_VALUE)
        {
            out.writeByte(TYPE_CLOB);
            out.writeInt(value.length());
            out.write(value.getBytes("UTF-8"));
            
            return;
        }
        
        out.writeByte(TYPE_STRING);
        out.writeUTF(value);
    }
    
    private void writeObject(JSONObject value, DataOutput out) throws IOException
    {
        int[] ids = new int[value.size()];
        int index = 0;
        
        // Ensure all the keys are registered
        for(String key : value.keySet()) 
        {
            ids[index++] = keys.write(key, out);
        }
        
        out.writeByte(TYPE_OBJECT);
        out.writeShort(value.size());
        
        // Write types
        for(int i=0; i<ids.length; i++)
        {
            out.writeShort(ids[i]);
        }
        
        // Write values
        for(String key : value.keySet())
        {
            write(value.get(key), out);
        }
    }
    
    private void writeArray(JSONArray value, DataOutput out) throws IOException
    {
        out.writeByte(TYPE_ARRAY);
        out.writeInt(value.size());
        
        for(Object item : value)
        {
            write(item, out);
        }
    }
    
    private void writeNumber(Number value, DataOutput out) throws IOException
    {
        if(value instanceof Long) out.writeByte(TYPE_LONG);
        else if(value instanceof Integer) out.writeByte(TYPE_INTEGER);
        else if(value instanceof Double) out.writeByte(TYPE_DOUBLE);
        else if(value instanceof Float) out.writeByte(TYPE_FLOAT);
        else if(value instanceof Short) out.writeByte(TYPE_SHORT);
        else if(value instanceof Byte) out.writeByte(TYPE_BYTE);
        else throw new IllegalArgumentException("Unknown number type " + value.getClass());
        
        if(value instanceof Long) out.writeLong(value.longValue());
        else if(value instanceof Integer) out.writeInt(value.intValue());
        else if(value instanceof Double) out.writeDouble(value.doubleValue());
        else if(value instanceof Float) out.writeFloat(value.floatValue());
        else if(value instanceof Short) out.writeShort(value.shortValue());
        else if(value instanceof Byte) out.writeByte(value.byteValue());
        else throw new IllegalArgumentException("Unknown number type " + value.getClass());
    }
    
    private void writeBoolean(boolean value, DataOutput out) throws IOException
    {
        out.writeByte(value ? TYPE_BOOLEAN_TRUE : TYPE_BOOLEAN_FALSE);
    }
    
    private void writeDate(Date value, DataOutput out) throws IOException
    {
        out.writeByte(TYPE_DATE);
        out.writeLong(value.getTime());
    }
    
    public void write(Object value, DataOutput out) throws IOException
    {
        if(value == null) out.writeByte(TYPE_NULL);
        else if(value instanceof String) writeString((String)value, out);
        else if(value instanceof JSONObject) writeObject((JSONObject)value, out);
        else if(value instanceof JSONArray) writeArray((JSONArray)value, out);
        else if(value instanceof Number) writeNumber((Number)value, out);
        else if(value instanceof Boolean) writeBoolean((Boolean)value, out);
        else if(value instanceof Date) writeDate((Date)value, out);
        else throw new IllegalStateException("Unknown value type " + value.getClass());
    }
}

