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
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.json.JSONWriter;
import com.invirgance.convirgance.target.Target;
import java.io.*;

/**
 *
 * @author jbanes
 */
public class JSONOutput implements Output
{

    @Override
    public OutputCursor write(Target target)
    {
        return new JSONOutputCursor(target);
    }

    @Override
    public String getContentType()
    {
        return "application/json";
    }
    
    private class JSONOutputCursor implements OutputCursor
    {
        private final Target target;
        private final Writer writer;
        private final JSONWriter json;
        private int count;

        public JSONOutputCursor(Target target)
        {
            try
            {
                this.target = target;
                this.writer = new BufferedWriter(new OutputStreamWriter(target.getOutputStream(), "UTF-8"), 16 * 1024);
                this.json = new JSONWriter(this.writer);
                
                this.writer.write("[\n");
            }
            catch(IOException e)
            {
                throw new ConvirganceException(e);
            }
        }

        @Override
        public void write(JSONObject record)
        {
            try
            {
                if(count > 0) 
                {
                    this.writer.write(",\n");
                }
                
                this.json.write(record);

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
            this.writer.write("\n]\n");
            this.json.close();
        }
        
    }
    
}
