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
import java.nio.charset.Charset;

/**
 * Writes data in RFC 4180 compliant CSV format. String data will be quoted with
 * double quotes, quotes in data will be escaped by two double quotes, and newlines
 * are allowed in quoted data. Lines are always terminated with CRLF as recommended
 * by the specification.
 * 
 * @author tadghh
 */
public class CSVOutput implements Output
{
    private String encoding = "UTF-8";
    private String[] headers;
    
    /**
     * Creates a new CSVOutput
     */
    public CSVOutput()
    {
        this(null);
    }
    
    /**
     * Creates a new CSVOutput only writing the provided columns
     * 
     * @param headers The columns to include or add while writing
     */
    public CSVOutput(String[] headers)
    {
        this.headers = headers;
    }
    
    /**
     * Set the file encoding to use for the output
     *
     * @param encoding The content encoding to use
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Returns the file encoding being used for the output
     *
     * @return The current expected content encoding
     */
    public String getEncoding()
    {
        return encoding;
    }
    
    /**
     * Set the headers to use when writing out CSV values
     *
     * @param columns The column headers.
     */
    public void setHeaders(String[] columns)
    {
        this.headers = columns;
    }
    
    /**
     * Returns the current headers that will be used when writing. If columns 
     * aren't provided, they will be created based on the keys found in the first 
     * record.
     * 
     * @return The headers used while writing.
     * @throws NullPointerException If no headers are set or found.
     */
    public String[] getHeaders()
    {
        return this.headers;
    }
    
    /**
     * Creates a new writer to output data to the specified target
     * 
     * @param target the target writeable output stream
     * @return the OutputCursor to write data
     */
    @Override
    public OutputCursor write(Target target)
    {
        if(headers != null) return new CSVOutputCursorWriter(target, headers);
              
        return new CSVOutputCursorWriter(target);
    }

    /**
     * Returns the <code>text/csv</code> MIME type
     * 
     * @return the MIME type
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
        
        private String[] detectHeaders(JSONObject record)
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
            if(headers == null) {
                headers = detectHeaders(record);
                CSVOutput.this.headers = headers;
            }
            
            if(out == null) 
            {
                out = new PrintWriter(target.getOutputStream(), false, Charset.forName(encoding));
        
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
