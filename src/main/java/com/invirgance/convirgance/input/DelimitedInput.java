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
import com.invirgance.convirgance.source.Source;
import com.invirgance.convirgance.wiring.annotation.Wiring;
import java.io.*;
import java.util.ArrayList;

/**
 * Provides support for reading character delimited files as a stream of data.
 * Care must be taken to ensure that the chosen delimiter never appears in the
 * data as no facilities are provided for escaping the delimiter character. This
 * is fine for formats like pipe-delimited or tab-delimited (tsv) where the 
 * delimiter is highly unlikely to be in the data. If you are attempting to 
 * read CSV data, use the {@link CSVInput} class instead.
 * 
 * @author jbanes
 * @see CSVInput
 */
@Wiring
public class DelimitedInput implements Input<JSONObject>
{
    private String[] columns;
    private String encoding; 
    private char delimiter;
    private boolean nullable;

    /**
     * Creates a new DelimitedInput, with '|' as the delimiter.
     */
    public DelimitedInput()
    {
        this('|');
    }

    /**
     * Creates a new DelimitedInput with the provided delimiter.
     * @param delimiter The character that the input content is delimited with.
     */
    public DelimitedInput(char delimiter)
    {
        this(null, "UTF-8", delimiter);
    }
    
    /**
     * Creates a new DelimitedInput with the provided column headers and value delimiter.
     * @param columns The column headers.
     * @param delimiter The character that the input content is delimited with.
     */
    public DelimitedInput(String[] columns, char delimiter)
    {
        this(columns, "UTF-8", delimiter);
    }
    
    /**
     * Creates a new DelimitedInput with custom text encoding and a specified value delimiter.
     * @param encoding The text encoding of the input content.
     * @param delimiter The delimiter of the input content.
     */
    public DelimitedInput(String encoding, char delimiter)
    {
        this(null, encoding, delimiter);
    }
    
    /**
     * Creates a new DelimitedInput with the provided column headers and value
     * delimiter along with the specified content encoding.
     *
     * @param columns The column headers.
     * @param encoding The text encoding of the input content.
     * @param delimiter The character that the input content is delimited with.
     */ 
    public DelimitedInput(String[] columns, String encoding, char delimiter)
    {
        this.columns = columns;
        this.encoding = encoding;
        this.delimiter = delimiter;
        this.nullable = true;
    }

    /**
     * Returns the column headers of the expected input content.
     * @return The column headers.
     */
    public String[] getColumns()
    {
        return columns;
    }

    /**
     * Set the columns to use as headers.
     * @param columns The column headers.
     */
    public void setColumns(String... columns)
    {
        this.columns = columns;
    }

    /**
     * Get the delimiter used to split the input content.
     * @return The delimiter in use.
     */
    public char getDelimiter()
    {
        return delimiter;
    }

    /**
     * Set the delimiter to split values on.
     * @param delimiter The delimiter to use.
     */
    public void setDelimiter(char delimiter)
    {
        this.delimiter = delimiter;
    }

    /**
     * Returns the current encoding in use.
     * @return The current expected content encoding.
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Set the content encoding to use for the input.
     * @param encoding The content encoding to use.
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * The current setting for whether blank columns will be interpreted as 
     * nulls (true) or empty strings (false). Default is to interpret empty
     * strings as null. (true)
     * 
     * @return true for nulls, false for empty strings
     */
    public boolean isNullable()
    {
        return nullable;
    }

    /**
     * Sets whether blank columns will be interpreted as nulls (true) or empty
     * strings (false)
     * 
     * @param nullable true for nulls, false for empty strings
     */
    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }
    
    static String[] parseLine(String line, char delimiter, boolean nullable)
    {
        ArrayList<String> list = new ArrayList<>();
        int start = 0;
        int end;
        
        if(line.length() < 1) return new String[0];
        
        while(start < line.length())
        {
            end = line.indexOf(delimiter, start);
            
            if(end < 0) break;
            
            if(start == end) list.add(nullable ? null : "");
            else list.add(line.substring(start, end));
            
            start = end+1;
        }
        
        // Snag the last item
        if(start == line.length()) list.add(nullable ? null : "");
        else list.add(line.substring(start, line.length()));
        
        return list.toArray(String[]::new);
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
        return new DelimitedInputCursor(source, columns, nullable);
    }
    
    private class DelimitedInputCursor implements InputCursor<JSONObject>
    {
        private final Source source;
        private final String[] columns;
        private final boolean nullable;

        public DelimitedInputCursor(Source source, String[] columns, boolean nullable)
        {
            this.source = source;
            this.columns = columns;
            this.nullable = nullable;
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
                else columns = parseLine(reader.readLine(), delimiter, false);

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
                        String[] data = parseLine(line, delimiter, nullable);

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
