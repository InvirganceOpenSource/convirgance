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
 * Provides streaming support for reading RFC 4180 compliant CSV (Comma-Separated Values) files
 * and converting them to JSONObjects. This class handles:
 * <ul>
 *   <li>Automatic header detection from CSV files</li>
 *   <li>Custom header mapping</li>
 *   <li>Configurable character encoding</li>
 *   <li>Proper handling of quoted values and escape sequences</li>
 * </ul>
 * 
 * @author tadghh
 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC 4180 Specification</a>
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
    }
    
    /**
     * Creates a new CSVInput with an array of headers to use when reading.
     * The default character encoding of `UTF-8` will be used.
     * 
     * @param headers The headers to use when reading.
     */
    public CSVInput(String[] headers)
    {
        this.headers = headers;
    }

    /**
     * Creates a new CSVInput with an array of headers to use when reading.
     * The encoding parameter changes the character encoding used when reading the input stream.
     * 
     * @param headers The headers to use when reading.
     * @param encoding The character encoding to use while reading.
     */
    public CSVInput(String[] headers, String encoding)
    {
        this.headers = headers;
        this.encoding = encoding;
    }

    /**
     * Set the character encoding to use for the input stream. This will override the default of "UTF-8"
     *
     * @param encoding The character encoding to use (e.g., "UTF-8", "ISO-8859-1").        
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
    
    /**
     * Returns the character encoding being used to read the input stream.
     *
     * @return The current character encoding (defaults to "UTF-8")
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Set the headers to use when reading in CSV values. 
     * These headers will also be used as the fields for any new JSONObjects.
     * If the CSV file has no headers of its own the values will be mapped in the order they occur in the columns array. (element1: value1, element2: value2)
     * 
     * @param columns The column headers.
     */
    public void setHeaders(String[] columns)
    {
        this.headers = columns;
    }
    
    /**
     * Returns the headers that will be used as the fields for JSONObjects
     * 
     * @return The column headers
     */
    public String[] getHeaders()
    {
        return headers;
    }
        
    /**
     * Creates an iterator to process CSV data from the provided source, converting
     * records into JSONObjects. Each CSV record is mapped to a JSONObject using
     * either the specified headers or auto-detected headers from the first record.
     *
     * @param source The source containing CSV data to read
     * @return An InputCursor that iterates over the CSV records as JSONObjects
     * @throws ConvirganceException in the following cases:
     *         <ul>
     *           <li>Empty or invalid CSV source provided</li>
     *           <li>Malformed CSV data (invalid quotes, missing fields, etc.)</li>
     *         </ul>
     */
    @Override
    public InputCursor<JSONObject> read(Source source)
    {
        return new CSVInputCursor(source, headers);
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
            this.headers = headers;
            this.source = source;
        }

        @Override
        public CloseableIterator<JSONObject> iterator()
        {
            return new CloseableIterator<JSONObject>() {
                private BufferedReader reader;
                
                {
                    try
                    {
                        reader = new BufferedReader(new InputStreamReader(source.getInputStream(), encoding), 16 * 1024);

                        if (headers == null)
                        {
                            headerLine = reader.readLine();
                            headers = parseCSVLine(headerLine).toArray(new String[0]);                           
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
                    try
                    {                   
                        return createJSONObject();
                    }
                    catch (IOException e)
                    {
                        throw new ConvirganceException("Error reading CSV line", e);
                    }
                }
                
                @Override
                public void close() throws Exception
                {
                    reader.close();
                }

                private List<String> parseCSVLine(String line)
                {
                    boolean quotes = false;
                    char character;
                    
                    // TODO: Is this slow with silly JSON Objects (super big lots of fields)?                    
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

                    if (quotes) throw new ConvirganceException("Unclosed quotes in CSV line: " + line);
                    
                    values.add(builder.toString());
                    
                    return values;
                }
                
                private JSONObject createJSONObject() throws IOException
                {
                    List<String> values = parseCompleteRecord();
                    JSONObject record = new JSONObject(true);
                    
                    for (int i = 0; i < headers.length; i++)
                    {
                        append = values.get(i);
                        
                        record.put(headers[i], append.isEmpty() ? null : append);
                    }
                    
                    return record;
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

            };
        }
    }
}
