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
import com.invirgance.convirgance.source.Source;
import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import java.io.*;
import java.util.ArrayList;

/**
 * 
 * @author jbanes
 */
public class DelimitedInput implements Input<JSONObject>
{
    private String[] columns;
    private String encoding; 
    private char delimiter;

    public DelimitedInput()
    {
        this('|');
    }

    public DelimitedInput(char delimiter)
    {
        this(null, "UTF-8", delimiter);
    }
    
    public DelimitedInput(String[] columns, char delimiter)
    {
        this(columns, "UTF-8", delimiter);
    }
    
    public DelimitedInput(String encoding, char delimiter)
    {
        this(null, encoding, delimiter);
    }
    
    public DelimitedInput(String[] columns, String encoding, char delimiter)
    {
        this.columns = columns;
        this.encoding = encoding;
        this.delimiter = delimiter;
    }

    public String[] getColumns()
    {
        return columns;
    }

    public void setColumns(String[] columns)
    {
        this.columns = columns;
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
    
    static String[] parseLine(String line, char delimiter)
    {
        ArrayList<String> list = new ArrayList<>();
        int start = 0;
        int end;
        
        if(line.length() < 1) return new String[0];
        
        while(start < line.length())
        {
            end = line.indexOf(delimiter, start);
            
            if(end < 0) break;
            
            if(start == end) list.add("");
            else list.add(line.substring(start, end));
            
            start = end+1;
        }
        
        // Snag the last item
        list.add(line.substring(start, line.length()));
        
        return list.toArray(String[]::new);
    }

    @Override
    public InputCursor<JSONObject> read(Source source)
    {
        return new DelimitedInputCursor(source, columns);
    }
    
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
                        JSONObject record = new JSONObject();
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
