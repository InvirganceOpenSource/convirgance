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
package com.invirgance.convirgance.input;
import com.invirgance.convirgance.CloseableIterator;
import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.Source;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides support for reading comma separated (CSV) files as a stream of data.
 * Compliant with RFC 4180.
 * 
 * @author tadghh
 */
public class CSVInput implements Input<JSONObject>
{
    private String encoding = "UTF-8"; 
    private String[] headers;
    
    /**
     * Creates a new CSVInput.
     * 
     * By default no headers are selected, they will be auto-detected. 
     * The expected file encoding is UTF-8, this can be changed with setEncoding()
     */
    public CSVInput()
    {
        this(null);
    }
    
    /**
     * Creates a new CSVInput with an array of headers to use when reading.
     * The encoding parameter can be changed to support the input file's encoding.
     * 
     * @param headers The headers to use when reading.
     */
    public CSVInput(String[] headers)
    {
        this.headers = headers;
    }
    
    /**
     * Used to iterate across a source contains CSV values, converting them into JSONObjects.
     *
     * @param source A {@link Source} to CSV data.
     * @return A CSVInputCursor with the decoded stream.
     * @throws ConvirganceException Exceptions are thrown when:
     * - An iteration into nothing was attempted.
     * - An invalid JSONObject is created (CSV -> JSON spec conflicts)
     * - There is an issue with the CSV file itself.
     */
    @Override
    public InputCursor<JSONObject> read(Source source)
    {
        return new CSVInputCursor(source, headers);
    }

    /**
     * Set the content encoding to use on the input
     *
     * @param encoding The content encoding to use
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
    
    /**
     * Returns the file encoding being used on the input
     *
     * @return The current expected content encoding
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Set the headers to use when reading in CSV values
     *
     * @param columns The column headers.
     */
    public void setHeaders(String[] columns)
    {
        this.headers = columns;
    }
    
    /**
     * Returns the column headers to use when getting values for JSONObjects
     * 
     * @return The column headers
     */
    public String[] getHeaders()
    {
        return headers;
    }

    private class CSVInputCursor implements InputCursor<JSONObject>
    {
        private final Source source;  
        private final StringBuilder builder = new StringBuilder();
        
        private String[] headers;
        private String nextLine;
        private String headerLine;
        private String append;        

        public CSVInputCursor(Source source, String[] headers)
        {
            if(headers != null) this.headers = headers;
            
            this.source = source;
        }

        @Override
        public CloseableIterator<JSONObject> iterator()
        {
            return new CloseableIterator<JSONObject>(){
                private BufferedReader reader;
                {
                    try
                    {
                        reader = new BufferedReader(new InputStreamReader(source.getInputStream(), encoding), 16 * 1024);

                        if (headers == null)
                        {
                            headerLine = reader.readLine();
                            
                            if (headerLine != null)
                            {
                                headers = parseCSVLine(headerLine).toArray(new String[0]);

                                CSVInput.this.headers = headers;
                            }
                            else
                            {
                                throw new ConvirganceException("CSV file is empty - no header row found.");
                            }
                        }

                        nextLine = reader.readLine();
                    }
                    catch (IOException e)
                    {
                        throw new ConvirganceException("Failed to initialize CSV reader", e);
                    }
                }

                @Override
                public boolean hasNext()
                {
                    return nextLine != null;
                }

                @Override
                public JSONObject next()
                {
                    if (!hasNext())
                    {
                        throw new ConvirganceException("Attempted to iterate with no next element.");
                    }               

                    try
                    {                   
                        return createJSONObject(parseCompleteRecord());
                    }
                    catch (IOException e)
                    {
                        throw new ConvirganceException("Error reading CSV line", e);
                    }
                    catch (Exception e)
                    {
                        throw new ConvirganceException("Error processing CSV line", e);
                    }
                }

                private List<String> parseCompleteRecord() throws IOException
                {
                    int quoteCount = 0;
                    
                    builder.setLength(0);
                    builder.append(nextLine);

                    for (char c : nextLine.toCharArray())
                    {
                        if (c == '"') quoteCount++;                        
                    }

                    while (quoteCount % 2 != 0 && (nextLine = reader.readLine()) != null)
                    {
                        builder.append("\n").append(nextLine);
                        
                        for (char c : nextLine.toCharArray())
                        {
                            if (c == '"') quoteCount++;                     
                        }
                    }

                    nextLine = reader.readLine();

                    return parseCSVLine(builder.toString());
                }

                private List<String> parseCSVLine(String line)
                {
                    boolean quotes = false;
                    char character;
                    
                    List<String> values = new ArrayList<>();
               
                    builder.setLength(0);

                    for (int i = 0; i < line.length(); i++)
                    {
                        character = line.charAt(i);

                        if (character == '"')
                        {
                            // Check if this is an escaped quote
                            if (quotes && i + 1 < line.length() && line.charAt(i + 1) == '"')
                            {                             
                                builder.append('"');
                               
                                i++; 
                                continue; 
                            }
                            
                            quotes = !quotes;
                        }
                        else if (character == ',' && !quotes)
                        {
                            values.add(builder.toString());
                            builder.setLength(0);
                        }
                        else 
                        {
                            builder.append(character);
                        }
                    }

                    values.add(builder.toString());
                 
                    if (quotes)
                    {
                        throw new ConvirganceException("Unclosed quotes in CSV line: " + line);
                    }
                    
                    return values;
                }

                private JSONObject createJSONObject(List<String> values)
                {
                    JSONObject record = new JSONObject(true);
                    
                    for (int i = 0; i < Math.min(headers.length, values.size()); i++)
                    {
                        append = values.get(i);
                        
                        record.put(headers[i], append.isEmpty() ? null : append);
                    }
                    
                    return record;
                }

                @Override
                public void close() throws Exception
                {
                    reader.close();
                }
            };
        }
    }
}
