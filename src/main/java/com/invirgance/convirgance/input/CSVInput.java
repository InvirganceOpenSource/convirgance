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

// - EOL/EOF Test

/**
 * Used to stream the contents of a CSV Source into JSONObjects.
 * @author tadghh
 */
public class CSVInput implements Input<JSONObject>
{

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
        return new CSVInputCursor(source);
    }

    private class CSVInputCursor implements InputCursor<JSONObject>
    {
        private final Source source;
        private List<String> headers;
        private BufferedReader reader;
        private String nextLine;
        private String headerLine;
        private String append;
        private final StringBuilder builder = new StringBuilder();
        private final StringBuilder current = new StringBuilder();

        public CSVInputCursor(Source source)
        {
            this.source = source;
        }

        @Override
        public CloseableIterator<JSONObject> iterator()
        {
            return new CloseableIterator<JSONObject>()
            {

                {
                    try
                    {
                        reader = new BufferedReader(new InputStreamReader(source.getInputStream()));

                        headerLine = reader.readLine();

                        if (headerLine != null)
                        {
                            headers = parseCSVLine(headerLine);
                            
                        }else throw new ConvirganceException("CSV file is empty - no header row found.");                      

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
                    if (!hasNext()) throw new ConvirganceException("Attempted to iterate with no next element.");                    

                    try
                    {
                        List<String> values = parseCompleteRecord();
                        return createJSONObject(values);
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
                    current.setLength(0);
                    current.append(nextLine);

                    int quoteCount = 0;

                    for (char c : nextLine.toCharArray())
                    {
                        if (c == '"') quoteCount++;                        
                    }

                    while (quoteCount % 2 != 0 && (nextLine = reader.readLine()) != null)
                    {
                        current.append("\n").append(nextLine);
                        for (char c : nextLine.toCharArray())
                        {
                            if (c == '"') quoteCount++;                     
                        }
                    }

                    nextLine = reader.readLine();

                    return parseCSVLine(current.toString());
                }

                private List<String> parseCSVLine(String line)
                {
                    List<String> values = new ArrayList<>();
                    builder.setLength(0);
                    boolean quotes = false;
                    char character;

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
                        else builder.append(character);
                       
                    }

                    values.add(builder.toString());
                 
                    if (quotes) throw new ConvirganceException("Unclosed quotes in CSV line: " + line);
                    
                    return values;
                }

                private JSONObject createJSONObject(List<String> values)
                {
                    builder.setLength(0);
                    builder.append("{");
                    String escapedHeader;
                    String escapedValue;
                    
                    for (int i = 0; i < Math.min(headers.size(), values.size()); i++)
                    {
                        append = values.get(i);
                        if (append != null && !append.isEmpty())
                        {
                            if (i > 0) builder.append(",");                           
                            
                            escapedHeader = headers.get(i).replace("\"", "\\\"");
                            escapedValue = append.replace("\"", "\\\"");
                            builder.append("\"")
                                    .append(escapedHeader)
                                    .append("\":\"")
                                    .append(escapedValue)
                                    .append("\"");
                        }
                    }
                    
                    builder.append("}");
                    return new JSONObject(builder.toString());
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
