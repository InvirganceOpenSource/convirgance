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
 * KeyTableEncoder provides a fixed-size mapping between string keys and unique integer indexes. 
 * It supports efficient key encoding/decoding, serialization, and deserialization of the mappings.
 * @author jbanes
 */
public class KeyTableEncoder implements KeyEncoder
{
    private HashMap<String,Integer> lookup;
    private String[] keys; 
    private int index;

    /**
     * Creates a new KeyTableEncoder without an output stream.
     */
    public KeyTableEncoder()
    {
        reset(null);
    }
    
    /**
     * Resets the encoder state.
     * @param out Not implemented.
     */
    @Override
    public void reset(DataOutput out)
    {
        // Key indexes are stored as a 16 bit value
        this.lookup = new HashMap<>();
        this.keys = new String[0xFFFF];
        this.index = 0;
    }
    
    /**
     * Retrieves the integer index associated with the specified key.
     * @param key The key.
     * @return The index of the corresponding key.
     * @throws IllegalStateException Occurs if the index is greater or equal to the current keys.
     */
    @Override
    public Integer get(String key)
    {
        Integer id = lookup.get(key);
        
        if(id == null)
        {
            if(index >= keys.length) throw new IllegalStateException("Maximum number of keys (" + 0xFFFF + ") has been exceeded by key [" + key + "]");
            
            id = index++;
            keys[id] = key;
            
            lookup.put(key, id);
        }
        
        return id;
    }
    
    /**
     * Returns the key for the provided index.
     * @param id The index.
     * @return The key at the associated index.
     * @throws IllegalArgumentException The id is greater or equal to the max index.
     */
    @Override
    public String get(int id)
    {
        if(id >= index) throw new IllegalArgumentException("Key " + id + " does not exist. " + index + " keys are available.");
        
        return keys[id];
    }
    
    /**
     * Reads key mappings from the provided input stream.
     * @param in The stream to read from.
     * @throws IOException If an error occurs while reading from the stream.
     */
    @Override
    public void read(DataInput in) throws IOException
    {
        reset(null);
        
        index = in.readUnsignedShort();
        
        for(int i=0; i<index; i++)
        {
            keys[i] = in.readUTF();
        }
    }
    
    /**
     * Writes out the current key mappings to the provided stream.
     * @param out The output stream.
     * @throws IOException If an error occurs when writing to the stream.
     */
    public void write(DataOutput out) throws IOException
    {
        out.writeShort(index);
        
        for(int i=0; i<index; i++)
        {
            out.writeUTF(keys[i]);
        }
    }
    
    /**
     * Writes the value for the provided key to the output stream. // TODO: not implemented?
     *
     * @param key The key to retrieve the index for.
     * @param out The output stream (not used in the current implementation).
     * @return The index of the provided key.
     * @throws IOException If an error occurs while interacting with the output
     * stream (if used in the future).
     */
    @Override
    public int write(String key, DataOutput out) throws IOException
    {
        return get(key);
    }
    
    /**
     * Returns the current index size.
     * @return Index size.
     */
    @Override
    public int size()
    {
        return index;
    }
}

