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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;

/**
 * The StringEncoder class maps strings to unique integer indexes, supporting up to 256 strings.
 * It is ideal for memory-constrained environments, efficient serialization, and quick lookups.
 * 
 * @author jbanes
 */
public class StringEncoder
{
    public static final int STRING_REGISTER_OPERATION = 0xF3;
    
    private HashMap<String,Integer> lookup;
    private String[] keys; 
    private int index;

    /**
     * Creates a new StringEncoder.
     */
    public StringEncoder()
    {
        this.lookup = new HashMap<>();
        this.keys = new String[256];
        this.index = 0;
    }
    
    /**
     * Gets the value for the provided key.
     * @param value The key.
     * @return The value for the key.
     */
    public Integer get(String value)
    {
        return lookup.get(value);
    }
    
    /**
     * Returns the key of the specified index.
     * @param i The index.
     * @return The key at the index.
     */
    public String get(Integer i)
    {
        return keys[i];
    }
    
    /**
     * Writes the string value to the output stream and returns its unique index.
     * @param value The string to write.
     * @param out The output stream to write to.
     * @return The index of the string.
     * @throws IOException If an error occurs while writing to the stream.
     */
    public Integer write(String value, DataOutput out) throws IOException
    {
        Integer id = get(value);
        
        if(id == null)
        {
            if(keys[index] != null) lookup.remove(keys[index]);
            
            keys[index] = value;
            id = index;
            
            lookup.put(value, id);
            
            out.writeByte(STRING_REGISTER_OPERATION);
            out.writeUTF(value);
            
            index = (index + 1) & 0xFF;
        }
        
        return id;
    }
    
    /**
     * Reads a string value from the input stream and registers it with a unique index.
     * @param in The input stream to read from.
     * @throws IOException If an error occurs while reading from the input stream.
     */
    public void read(DataInput in) throws IOException
    {
        String value = in.readUTF();
        
        if(keys[index] != null) lookup.remove(keys[index]);
            
        keys[index] = value;
        
        lookup.put(value, index);
        
        index = (index + 1) & 0xFF;
    }
}
