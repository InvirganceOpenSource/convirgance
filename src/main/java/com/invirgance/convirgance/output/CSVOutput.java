/*
 * The MIT License
 *
 * Copyright 2025 tadghh.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.invirgance.convirgance.output;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.json.JSONWriter;
import com.invirgance.convirgance.target.Target;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Used when writing JSON to CSV that will then be written to some target stream.
 * @author tadghh
 */
public class CSVOutput implements Output
{
    /**
     * Used to reliably write out data to the provided {@link Target}.
     * @param target A place to write data, ex File, Network.
     * @return A CSVOutputCursor.
     */
    @Override
    public OutputCursor write(Target target)
    {
        return new CSVOutputCursor(target);
    }

    /**
     * Returns the mime-type for the content that will be output. 
     * For this specific class it will be 'text/csv'.
     * 
     * @return The mime-type.
     */
    @Override
    public String getContentType()
    {
        return "text/csv";
    }
    
    private class CSVOutputCursor implements OutputCursor
    {
        private final Target target;
        private final Writer writer;
        private final JSONWriter json;
        private int count;
        private List<String> headers;
        
        public CSVOutputCursor(Target target)
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
                // Grab headers
                if(count == 0){                
                    headers = new ArrayList<>(record.keySet());
                }else
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
