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


import static com.invirgance.convirgance.jbin.BinaryEncoder.*;
import static com.invirgance.convirgance.jbin.KeyEncoder.*;
import static com.invirgance.convirgance.jbin.StringEncoder.*;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.io.DataInput;
import java.io.IOException;
import java.util.Date;

/**
 * Decodes JBIN encoded streams into JSON.
 * @author jbanes
 */
public class BinaryDecoder
{
    private KeyEncoder keys;
    private StringEncoder strings;

    /**
     * Creates a new BinaryDecoder.
     */
    public BinaryDecoder()
    {
        this(new KeyStreamEncoder());
    }
    
    /**
     * Creates a new BinaryDecoder with the provided KeyEncoder for decoding.
     * @param keys The KeyEncoder.
     */
    public BinaryDecoder(KeyEncoder keys)
    {
        this.keys = keys;
        this.strings = new StringEncoder();
    }
    
    /**
     * Gets the key with the associated id.
     * @param id The id.
     * @return The associated string.
     */
    public String getKey(int id)
    {
        return keys.get(id);
    }
    
    /**
     * Gets the current key count
     * @return The total.
     */
    public int getKeyCount()
    {
        return keys.size();
    }

    /**
     * Gets the current key encoder.
     * @return The encoder.
     */
    public KeyEncoder getKeyEncoder()
    {
        return keys;
    }

    /**
     * Returns the StringEncoder in use.
     * @return The encoder.
     */
    public StringEncoder getStringEncoder()
    {
        return strings;
    }
    
    private JSONObject readObject(DataInput in) throws IOException
    {
        JSONObject record = new JSONObject(true);
        int size = in.readUnsignedShort();
        int[] ids = new int[size];
        
        for(int i=0; i<size; i++)
        {
            ids[i] = in.readUnsignedShort();
        }
        
        for(int i=0; i<size; i++)
        {
            record.put(getKey(ids[i]), read(in));
        }
        
        return record;
    }
    
    private JSONArray readArray(DataInput in) throws IOException
    {
        JSONArray array = new JSONArray();
        int size = in.readInt();
        
        for(int i=0; i<size; i++)
        {
            array.add(read(in));
        }
        
        return array;
    }
    
    private String readCLOB(DataInput in) throws IOException
    {
        byte[] buffer = new byte[in.readInt()];
        
        in.readFully(buffer);
        
        return new String(buffer, "UTF-8");
    }
    
    /**
     * Decodes data from an input stream that uses JBIN encoding.
     * @param in The input stream.
     * @return The decoded Object.
     * @throws IOException If an error occurs while decoding.
     * @throws IllegalStateException If an incorrect/unknown byte is read.
     */
    public Object read(DataInput in) throws IOException
    {
        int type = in.readByte() & 0xFF;
        
        switch(type)
        {
            case TYPE_NULL:
                return null;
                
            case TYPE_STRING:
                return strings.get(in.readByte() & 0xFF);
                
            case TYPE_OBJECT:
                return readObject(in);
                
            case TYPE_ARRAY:
                return readArray(in);
                
            case TYPE_LONG:
                return in.readLong();
                
            case TYPE_INTEGER_U8:
                return in.readUnsignedByte();
                
            case TYPE_INTEGER_U16:
                return in.readUnsignedShort();
                
            case TYPE_INTEGER:
                return in.readInt();
                
            case TYPE_DOUBLE:
                return in.readDouble();
                
            case TYPE_FLOAT:
                return in.readFloat();
                
            case TYPE_SHORT:
                return in.readShort();
                
            case TYPE_BYTE:
                return in.readByte();
                
            case TYPE_BOOLEAN_TRUE:
                return Boolean.TRUE;
                
            case TYPE_BOOLEAN_FALSE:
                return Boolean.FALSE;
                
            case TYPE_CLOB:
                return readCLOB(in);
                
            case TYPE_DATE:
                return new Date(in.readLong());
                
            case KEY_REGISTER_OPERATION:
                keys.read(in);
                return read(in);
                
            case KEY_RESET_OPERATION:
                keys.reset(null);
                return read(in);
                
            case STRING_REGISTER_OPERATION:
                strings.read(in);
                return read(in);
            
            // EOF
            case TYPE_EOF:
                return null;
        }
        
        throw new IllegalStateException("Unknown value type 0x" + Integer.toHexString(type).toUpperCase());
    }
}
