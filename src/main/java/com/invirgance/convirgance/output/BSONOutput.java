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
package com.invirgance.convirgance.output;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.bson.BinaryEncoder;
import com.invirgance.convirgance.bson.KeyEncoder;
import com.invirgance.convirgance.bson.KeyStreamEncoder;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.target.Target;
import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author jbanes
 */
public class BSONOutput implements Output
{
    private boolean compressed;

    public BSONOutput()
    {
        this(true);
    }

    public BSONOutput(boolean compressed)
    {
        this.compressed = compressed;
    }

    public boolean isCompressed()
    {
        return compressed;
    }

    public void setCompressed(boolean compressed)
    {
        this.compressed = compressed;
    }

    @Override
    public OutputCursor write(Target target)
    {
        return new BSONOutputCursor(target, compressed);
    }
    
    private class BSONOutputCursor implements OutputCursor
    {
        private final DataOutputStream out;
        private final KeyEncoder keys;
        private final BinaryEncoder json;
        
        private int count;
        
        public BSONOutputCursor(Target target, boolean compressed)
        {
            BufferedOutputStream buffered;
            
            try
            {
                buffered = new BufferedOutputStream(target.getOutputStream(), 16 * 1024);
                
                buffered.write(0xFF);
                buffered.write(0xFF);
                buffered.write('B');
                buffered.write('S');
                buffered.write('O');
                buffered.write('N');
                buffered.write(0x01); // Version 1
                buffered.write(getFlags(compressed)); // Flags
                
                this.out = new DataOutputStream(compressed ? new GZIPOutputStream(buffered) : buffered);
                this.keys = new KeyStreamEncoder();
                this.json = new BinaryEncoder(keys);
            }
            catch(IOException e)
            {
                throw new ConvirganceException(e);
            }
        }
        
        private int getFlags(boolean compressed)
        {
            int flags = 0;
            
            if(compressed) flags |= 0x01;
            
            return flags;
        }

        @Override
        public void write(JSONObject record)
        {
            try
            {
                this.json.write(record, out);

                count++;
            }
            catch(IOException e)
            {
                throw new ConvirganceException(e);
            }
        }

        @Override
        public void close() throws Exception
        {
            this.out.write(BinaryEncoder.TYPE_EOF);
            this.out.close();
        }
    }
    
}
