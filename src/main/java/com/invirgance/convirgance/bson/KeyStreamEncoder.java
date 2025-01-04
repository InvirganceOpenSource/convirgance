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

import com.invirgance.convirgance.ConvirganceException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;

/**
 * Use when you need to map string keys to unique integer IDs for stream processing.
 * @author jbanes
 */
public class KeyStreamEncoder implements KeyEncoder
{
    private HashMap<String,Integer> lookup;
    private String[] keys; 
    private int index;
    
    public KeyStreamEncoder()
    {
        reset(null);
    }

    @Override
    public void reset(DataOutput out)
    {
        // Key indexes are stored as a 16 bit value
        this.lookup = new HashMap<>();
        this.keys = new String[0xFFFF];
        this.index = 0;
        
        try
        {
            if(out != null) out.write(KEY_RESET_OPERATION);
        }
        catch(IOException e) { throw new ConvirganceException(e); }
    }

    @Override
    public Integer get(String key)
    {
        return lookup.get(key);
    }

    @Override
    public String get(int id)
    {
        // TODO: We should probably allow this to wrap
        if(id >= index) throw new IllegalArgumentException("Key " + id + " does not exist. " + index + " keys are available.");
        
        return keys[id];
    }

    @Override
    public int size()
    {
        return index;
    }
    
    @Override
    public void read(DataInput in) throws IOException
    {
        int id;
        
        // TODO: We should probably allow this to wrap
        if(index >= keys.length) throw new IllegalStateException("Maximum number of keys (" + 0xFFFF + ") has been exceeded during read!");

        id = index++;
        keys[id] = in.readUTF();
        
        lookup.put(keys[id], id);
    }
    
    /**
     * Encodes a key and writes it to the DataOutput.
     * @param key The key to encode
     * @param out The stream to write to.
     * @return The key index.
     * @throws IOException If an error occurs during writing.
     * @throws IllegalStateException If the maximum number of keys is exceeded.
     */
    @Override
    public int write(String key, DataOutput out) throws IOException
    {
        Integer id = lookup.get(key);
        
        if(id != null) return id;
        
        // TODO: We should probably allow this to wrap
        if(index >= keys.length) throw new IllegalStateException("Maximum number of keys (" + 0xFFFF + ") has been exceeded by key [" + key + "]");

        id = index++;
        keys[id] = key;

        lookup.put(key, id);

        out.write(KEY_REGISTER_OPERATION);
        out.writeUTF(key);
        
        return id;
    }
}
