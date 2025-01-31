/*
 * The MIT License
 *
 * Copyright 2025 INVIRGANCE LLC.
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
package com.invirgance.convirgance.input;

import com.invirgance.convirgance.CloseableIterator;
import com.invirgance.convirgance.ConvirganceException;
import static com.invirgance.convirgance.input.DelimitedInput.parseLine;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.Source;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * A class used when working with tab delimited input
 * @author timur
 */
public class TabDelimitedInput extends DelimitedInput
{
    private String[] columns;
    private String encoding; 
    private final char delimiter = '\t';
    
    /**
     * Creates a new TabDelimitedInput.
     */
    public TabDelimitedInput()
    {
        this(null, "UTF-8");
    }
    
    /**
     * Creates a new TabDelimitedInput with the provided column headers
     * @param columns The column headers.
     */
    public TabDelimitedInput(String[] columns)
    {
        this(columns, "UTF-8");
    }
    
    /**
     * Creates a new TabDelimitedInput with custom text encoding.
     * @param encoding The text encoding of the input content.
     */
    public TabDelimitedInput(String encoding)
    {
        this(null, encoding);
    }
    
    /**
     * Creates a new TabDelimitedInput with the provided column headers and custom text encoding.
     * @param columns The column headers.
     * @param encoding The text encoding of the input content.
     */
    public TabDelimitedInput(String[] columns, String encoding)
    {
        this.columns = columns;
        this.encoding = encoding;
    }
    
    /**
     * Overriding the SetDelimiter method to throw an exception when usage is attempted.
     * @param delimiter The delimiter being set.
     * @throws ConvirganceException always as usage not allowed
     * 
     * Q: Does this still allow the usage of the super method by calling 
     *  ((DelimitedInput) tabDelimitedInput).SetDelimiter('|')
     */
    @Override
    public void setDelimiter(char delimiter)
    {
        throw new ConvirganceException("Cannot set delimiter for TabDelimitedInput class");
    }
    
     /**
     * Creates a new cursor to read JSON objects from the given presumably
     * delimited source stream. The method uses the current column definitions
     * of this object to map data from the source to JSON objects.
     *
     * @param source A DataSource representing the input stream of delimited
     * data.
     * @return A InputCursor to iterate over the parsed objects.
     * @throws ConvirganceException If an I/O error occurs while reading from
     * the source or if parsing fails.
     */
    @Override
    public InputCursor<JSONObject> read(Source source)
    {
        return new TabDelimitedInput.DelimitedInputCursor(source, columns);
    }
    
    /**
     * Copied private class from DelimitedInput.java
     * Q: Should I rename this to TabDelimitedInputCursor or no need?
     */
    
    private class DelimitedInputCursor implements InputCursor<JSONObject>
    {
        private final Source source;
        private final String[] columns;

        public DelimitedInputCursor(Source source, String[] columns)
        {
            this.source = source;
            this.columns = columns;
        }
        
        @Override
        public CloseableIterator<JSONObject> iterator()
        {
            final String[] columns;
            final BufferedReader reader;
            final InputStream in;

            try
            {
                reader = new BufferedReader(new InputStreamReader(source.getInputStream(), encoding), 16 * 1024);

                if(this.columns != null) columns = this.columns;
                else columns = parseLine(reader.readLine(), delimiter);

                return new CloseableIterator<JSONObject>() {

                    private String line = reader.readLine();
                    private boolean closed = false;

                    @Override
                    public boolean hasNext()
                    {
                        if(line == null) close();

                        return (line != null);
                    }

                    @Override
                    public JSONObject next()
                    {
                        JSONObject record = new JSONObject(true);
                        String[] data = parseLine(line, delimiter);

                        for(int i=0; i<columns.length; i++)
                        {
                            if(i < data.length) record.put(columns[i], data[i]);
                        }

                        try
                        {
                            line = reader.readLine();

                            if(line == null) close();
                        }
                        catch(IOException e) { throw new ConvirganceException(e); }

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
