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

// - Mime type will tell us if the file has a header

//  - 1:1 with values till line break

// - Last field must not have comma, 
//  - might have a line ending

// - 
/**
 * Used when streaming CSV values into JSONObjects.
 * @author tadghh
 */
public class CSVInput implements Input<JSONObject>
{
   
    /**
     * Used to stream CSV into JSONObjects.
     * @param source A {@link Source} to CSV data.
     * @return A CSVInputCursor with the decoded stream.
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

        public CSVInputCursor(Source source)
        {
            this.source = source;
        }

        @Override
        public CloseableIterator<JSONObject> iterator()
        {
            return new CloseableIterator<JSONObject>()
            {
                private BufferedReader reader;
                private boolean closed = false;
                private String nextLine = null;
                String headerLine;
                
                {
                    try
                    {
                        reader = new BufferedReader(new InputStreamReader(source.getInputStream()));

                        headerLine = reader.readLine();
                        if (headerLine != null)
                        {
                            headers = parseCSVLine(headerLine);
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
                    return !closed && nextLine != null;
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
                        List<String> values = parseCSVLine(nextLine);
                        nextLine = reader.readLine();

                        if (nextLine == null)
                        {
                            close();
                        }

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

                private List<String> parseCSVLine(String line)
                {
                    List<String> values = new ArrayList<>();
                    StringBuilder currentValue = new StringBuilder();
                    boolean inQuotes = false;

                    for (int i = 0; i < line.length(); i++)
                    {
                        char c = line.charAt(i);

                        if (c == '"')
                        {
                            if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"')
                            {
                                // Handle escaped quotes
                                currentValue.append('"');
                                i++;
                            }
                            else
                            {
                                inQuotes = !inQuotes;
                            }
                        }
                        else if (c == ',' && !inQuotes)
                        {
                            values.add(currentValue.toString().trim());
                            currentValue.setLength(0);
                        }
                        else
                        {
                            currentValue.append(c);
                        }
                    }

                    values.add(currentValue.toString().trim());
                    return values;
                }

                private JSONObject createJSONObject(List<String> values)
                {
                    StringBuilder json = new StringBuilder("{");

                    for (int i = 0; i < Math.min(headers.size(), values.size()); i++)
                    {
                        String value = values.get(i);
                        if (value != null && !value.isEmpty())
                        {
                            if (i > 0)
                            {
                                json.append(",");
                            }
                            json.append("\"").append(headers.get(i)).append("\":\"").append(value).append("\"");
                        }
                    }

                    json.append("}");
                    return new JSONObject(json.toString());
                }

                @Override
                public void close() throws Exception
                {
                    if (!closed)
                    {
                        reader.close();
                        closed = true;
                    }
                }
            };
        }
    }
}
