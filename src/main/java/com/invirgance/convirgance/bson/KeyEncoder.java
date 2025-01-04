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

/**
 * Interface for encoding and decoding key-value data, enabling custom serialization for keys in binary data streams.
 * @author jbanes
 */
public interface KeyEncoder
{
    public static final int KEY_REGISTER_OPERATION = 0xF1;
    public static final int KEY_RESET_OPERATION = 0xF2;
    
    /**
     * Resets the encoder state.
     *
     * @param out The output stream.
     */
    public void reset(DataOutput out);
    
    /**
     * Retrieves the integer value associated with a key.
     * 
     * @param key The key.
     * @return The corresponding integer value.
     */    
    public Integer get(String key);
    
    /**
     * Retrieves the key associated with an integer ID.
     *
     * @param id The ID.
     * @return The corresponding key.
     */ 
    public String get(int id);
    
    /**
     * Returns the encoder size.
     *
     * @return The size.
     */ 
    public int size();
    
    /**
     * Reads the encoder state from an input stream.
     *
     * @param in The input stream.
     * @throws IOException If reading fails.
     */
    public default void read(DataInput in) throws IOException
    {
        // Default implementation is blank
    }
    
    /**
     * Writes a key to an output stream.
     *
     * @param key The key.
     * @param out The output stream.
     * @return The number of bytes written.
     * @throws IOException If writing fails.
     */
    public int write(String key, DataOutput out) throws IOException;
}
