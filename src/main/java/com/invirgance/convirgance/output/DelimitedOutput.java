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
 * Support for writing delimited file formats like tab delimited and pipe delimited formats.
 * @author jbanes
 */
public class DelimitedOutput implements Output
{
    private String[] columns;
    private char delimiter;
    private String encoding = "UTF-8";
    
    /**
     * Creates a new DelimitedOutput, delimiting on '|'.
     */
    public DelimitedOutput()
    {
        this(null, '|');
    }

    /**
     * Creates a new DelimitedOutput using the provided character to delimit content.
     * @param delimiter The delimiting character.
     */
    public DelimitedOutput(char delimiter)
    {
        this(null, delimiter);
    }
    
    /**
     * Creates a DelimitedOuput with specified columns.
     * @param columns A String array of column names.
     */
    public DelimitedOutput(String[] columns)
    {
        this(columns, '|');
    }
    
    /**
     * Creates a DelimitedOuput with specified columns and delimiter.
     * @param columns A String array of column names.
     * @param delimiter The character to delimit on.
     */
    public DelimitedOutput(String[] columns, char delimiter)
    {
        this.columns = columns;
        this.delimiter = delimiter;
    }

    /**
     * Gets the current character used when delimiting content.
     * @return The delimiting character in use.
     */
    public char getDelimiter()
    {
        return delimiter;
    }

    /**
     * Sets the character to use when delimiting.
     * @param delimiter The character to delimit on.
     */
    public void setDelimiter(char delimiter)
    {
        this.delimiter = delimiter;
    }

    /**
     * Gets the current text encoding.
     * @return String representation of the encoding.
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Set the text encoding.
     * @param encoding Encoding type.
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Creates a new writer to output delimited data to the specified target.
     * Uses the current column definitions of this object to format the output.
     * 
     * @param target The target writeable output stream.
     * @return The OutputCursor to write delimited data.
     */
    @Override
    public OutputCursor write(Target target)
    {
        return new DelimitedOutputWriter(target, this.columns);
    }
    
    /**
     * Returns 'text/plain', but if the delimiter in use is ',' 'text/csv' is returned.
     * @return One of the two content mime types.
     */
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
