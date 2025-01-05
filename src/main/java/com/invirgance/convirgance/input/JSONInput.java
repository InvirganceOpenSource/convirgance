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
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.json.JSONParser;
import com.invirgance.convirgance.source.Source;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Creates a cursor for traversing JSON data while maintaining proper resource handling.
 * @author jbanes
 */
public class JSONInput implements Input<JSONObject>
{
    
    /**
     * Creates a new {@link JSONInputCursor} to use for reading from some data
     * source/stream.
     *
     * @param source The stream.
     * @return A new {@link InputCursor} that can be used to iterate over
     * {@link JSONObject} records from the provided source.
     */
    @Override
    public InputCursor<JSONObject> read(Source source)
    {
        return new JSONInputCursor(source);
    }
    
    private class JSONInputCursor implements InputCursor<JSONObject>
    {
        private Source source;

        public JSONInputCursor(Source source)
        {
            this.source = source;
        }
        
        private static boolean find(BufferedReader reader) throws IOException
        {
            int c;
            
            reader.mark(16);

            c = reader.read();

            while(c >= 0 && c != '{')
            {
                if(c == ']') return false;
                
                if(!Character.isWhitespace(c) && c != '[' && c != ',')
                {
                    throw new ConvirganceException("Unexpected character: " + (char)c + " (0x" + Integer.toHexString(c) + ")");
                }
                
                reader.mark(16);
                
                c = reader.read();
            }
            
            if(c < 0) return false;
            
            reader.reset();
            
            return true;
        }
        
        private static JSONObject read(BufferedReader reader, JSONParser parser)
        {
            try
            {
                if(!find(reader)) return null;
                
                return parser.parseObject();
            }
            catch(IOException e)
            {
                throw new ConvirganceException(e);
            }
        }

        @Override
        public CloseableIterator<JSONObject> iterator()
        {
            final BufferedReader reader;
            final JSONParser parser;

            try
            {
                reader = new BufferedReader(new InputStreamReader(source.getInputStream(), "UTF-8"), 16 * 1024);
                parser = new JSONParser(reader);

                return new CloseableIterator<JSONObject>() {

                    private boolean closed = false;
                    private JSONObject record = read(reader, parser);

                    @Override
                    public boolean hasNext()
                    {
                        if(record == null) close();

                        return (record != null);
                    }

                    @Override
                    public JSONObject next()
                    {
                        JSONParser parser = new JSONParser(reader);
                        JSONObject record = this.record;
                        
                        this.record = read(reader, parser);

                        if(this.record == null) close();

                        return record;
                    }

                    @Override
                    public void close()
                    {
                        if(closed) return;

                        try
                        {
                            reader.close();
                        }
                        catch(IOException e) { throw new ConvirganceException(e); }

                        closed = true;
                    }
                };
            }
            catch(IOException e)
            {
                throw new ConvirganceException(e);
            }
        }
        
    }
}
