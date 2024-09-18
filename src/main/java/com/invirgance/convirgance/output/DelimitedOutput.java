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

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.target.Target;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Set;

/**
 *
 * @author jbanes
 */
public class DelimitedOutput implements Output
{
    private String[] columns;
    private char delimiter;
    private String encoding = "UTF-8";
    
    public DelimitedOutput()
    {
        this(null, '|');
    }

    public DelimitedOutput(char delimiter)
    {
        this(null, delimiter);
    }
    
    public DelimitedOutput(String[] columns)
    {
        this(columns, '|');
    }
    
    public DelimitedOutput(String[] columns, char delimiter)
    {
        this.columns = columns;
        this.delimiter = delimiter;
    }

    public char getDelimiter()
    {
        return delimiter;
    }

    public void setDelimiter(char delimiter)
    {
        this.delimiter = delimiter;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    @Override
    public OutputCursor write(Target target)
    {
        return new DelimitedOutputWriter(target, this.columns);
    }
    
    @Override
    public String getContentType()
    {
        if(delimiter == ',') return "text/csv";
        
        return "text/plain";
    }
    
    private class DelimitedOutputWriter implements OutputCursor
    {
        private Target target;
        private PrintWriter out;
        private String[] columns;

        public DelimitedOutputWriter(Target target, String[] columns)
        {
            this.target = target;
            this.columns = columns;
        }
    
        private String[] detectColumns(JSONObject record)
        {
            Set<String> keys = record.keySet();

            return keys.toArray(String[]::new);
        }
    
        private String stringify(String[] columns)
        {
            StringBuffer buffer = new StringBuffer();

            for(int i=0; i<columns.length; i++)
            {
                if(i > 0) buffer.append(delimiter);

                buffer.append(columns[i]);
            }

            return buffer.toString();
        }
    
        private String stringify(JSONObject record)
        {
            StringBuffer buffer = new StringBuffer();
            Object value;

            for(int i=0; i<columns.length; i++)
            {
                if(i > 0) buffer.append(delimiter);

                value = record.get(columns[i]);

                if(value != null) buffer.append(value.toString());
            }

            return buffer.toString();
        }
        
        @Override
        public void write(JSONObject record)
        {   
            if(columns == null) columns = detectColumns(record);

            if(out == null) 
            {
                out = new PrintWriter(target.getOutputStream(), false, Charset.forName(encoding));

                out.println(stringify(columns));
                out.flush();
            }

            out.println(stringify(record));
        }
        
        @Override
        public void close()
        {
            if(out != null) out.close();
        }
    }
}
