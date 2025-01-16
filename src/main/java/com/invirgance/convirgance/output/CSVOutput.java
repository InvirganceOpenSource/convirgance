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

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.target.Target;
import java.io.PrintWriter;

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
        return new CSVOutputCursorWriter(target);
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
     
    private class CSVOutputCursorWriter implements OutputCursor
    {     
        private final Target target;
       
        private PrintWriter out;
        private String[] headers;

        public CSVOutputCursorWriter(Target target, String[] headers)
        {
            this.target = target;
            this.headers = headers;
        }
        
        public CSVOutputCursorWriter(Target target)
        {
            this(target, null);
        }
        
        private String escapeAndQuoteValue(Object value)
        {
            boolean needsQuoting;
            String evaluate;
            
            if (value == null) return "";
            
            evaluate = value.toString();
            needsQuoting = evaluate.contains(",")
                    || evaluate.contains("\"")
                    || evaluate.contains("\n")
                    || evaluate.contains("\r");

            if (needsQuoting) return "\"" + evaluate.replace("\"", "\"\"") + "\"";        

            return evaluate;
        }
        
        private String[] detectColumns(JSONObject record)
        {
            return record.keySet().toArray(String[]::new);
        }

        private void stringify(JSONObject record)
        {
            boolean first = true;
            
            for (String header : headers)
            {
                if (!first) out.append(',');
                
                first = false;
                out.append(escapeAndQuoteValue(record.get(header)));
            }
        }

        private void stringify(String[] columns)
        {
            boolean first = true;
            
            for (String header : columns)
            {
                if (!first) out.append(',');
                
                first = false;
                out.append(escapeAndQuoteValue(header));
            }
        }
        
        @Override
        public void write(JSONObject record)
        {   
            if(headers == null) headers = detectColumns(record);

            if(out == null) 
            {
                out = new PrintWriter(target.getOutputStream(), false);
                
                stringify(headers);
                out.print("\r\n");                
            }

            stringify(record);
            out.print("\r\n");
        }
        
        @Override
        public void close()
        {
            if(out != null) out.close();
        }
    }
}
