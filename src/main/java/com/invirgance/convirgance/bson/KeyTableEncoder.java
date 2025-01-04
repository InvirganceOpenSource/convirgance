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
    
    @Override
    public String get(int id)
    {
        if(id >= index) throw new IllegalArgumentException("Key " + id + " does not exist. " + index + " keys are available.");
        
        return keys[id];
    }
    
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
    
    public void write(DataOutput out) throws IOException
    {
        out.writeShort(index);
        
        for(int i=0; i<index; i++)
        {
            out.writeUTF(keys[i]);
        }
    }
    
    @Override
    public int write(String key, DataOutput out) throws IOException
    {
        return get(key);
    }
    
    @Override
    public int size()
    {
        return index;
    }
}

