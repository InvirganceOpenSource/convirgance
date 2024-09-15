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
package com.invirgance.convirgance.input;

import com.invirgance.convirgance.CloseableIterator;
import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.bson.BinaryDecoder;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.Source;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author jbanes
 */
public class BSONInput implements Input<JSONObject>
{
    @Override
    public InputCursor<JSONObject> read(Source source)
    {
        return new BSONInputCursor(source);
    }

    private class BSONInputCursor implements InputCursor<JSONObject>
    {
        private static final byte[] header = new byte[] {
            (byte)0xFF, (byte)0xFF, 'B', 'S', 'O', 'N'
        };
        
        private Source source;

        public BSONInputCursor(Source source)
        {
            this.source = source;
        }
        
        @Override
        public CloseableIterator<JSONObject> iterator()
        {
            final BufferedInputStream buffer = new BufferedInputStream(source.getInputStream(), 16 * 1024);
            final DataInputStream in;
            final BinaryDecoder decoder = new BinaryDecoder();
            
            int version;
            int flags;
            
            try
            {
                for(int i=0; i<header.length; i++)
                {
                    if(buffer.read() != (header[i] & 0xFF))
                    {
                        throw new ConvirganceException("File is not in Convirgance BSON format");
                    }
                }
            
                version = buffer.read();
                flags = buffer.read();
                
                if(version > 0x01) throw new ConvirganceException("Version " + version + " of the Convirgance BSON format is not supported");

                if((flags & 0x01) > 0) in = new DataInputStream(new GZIPInputStream(buffer));
                else in = new DataInputStream(buffer);
            }
            catch(IOException e) { throw new ConvirganceException(e); }
            
            
            return new CloseableIterator<JSONObject>() {
                
                private JSONObject record;
                
                @Override
                public boolean hasNext()
                {
                    if(record != null) return true;
                    
                    try
                    {
                        record = (JSONObject)decoder.read(in);
                    
                        if(this.record == null) close();
                    }
                    catch(Exception e) { throw new ConvirganceException(e); }
                    
                    return (record != null);
                }

                @Override
                public JSONObject next()
                {
                    JSONObject record = this.record;
                    
                    if(record == null)
                    {
                        try
                        {
                            record = (JSONObject)decoder.read(in);
                    
                            if(record == null) close();
                        }
                        catch(Exception e) { throw new ConvirganceException(e); }
                    }
                    
                    this.record = null;
                    
                    return record;
                }

                @Override
                public void close() throws Exception
                {
                    in.close();
                }
            };
        }
    }
}
