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

import com.invirgance.convirgance.input.CSVInput;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.ByteArraySource;
import com.invirgance.convirgance.source.InputStreamSource;
import com.invirgance.convirgance.target.ByteArrayTarget;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests to ensure the JSON data is output to CSV specifications.
 * 
 * @author tadghh
 */
public class CSVOutputTest
{
    
    public CSVOutputTest()
    {
    }

    private void assertCSVEquals(String known, String message) throws Exception
    {
        CSVInput tester = new CSVInput();
        JSONArray output = new JSONArray();
        JSONArray expected = new JSONArray(known);

        ByteArraySource inputStream = new ByteArraySource(outputCSV(known).getBytes());
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());
        
        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }

        assertTrue(expected.equals(output), message);
    }

    private String outputCSV(String input) throws Exception
    {
        ByteArrayTarget target = new ByteArrayTarget();   
        CSVOutput output = new CSVOutput();
        byte[] bytes;

        try(OutputCursor cursor = output.write(target))
        {
            cursor.write(new JSONArray(input));
        } 
        
        bytes = target.getBytes();
   
        return new String(bytes);
    }
    
    /**
     * Generic output test.
     */
    @Test
    public void normalOutput() throws Exception
    {
        String expected ="[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New York\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]";
       
        assertCSVEquals(expected, "Simple output test for CSVOuput.");
    }   
        
    /**
     * Make sure new lines within quotes are parsed correctly.
     */
    @Test
    public void newLineInQuotes() throws Exception
    {
        String expected = "[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New\\nYo\\nrk\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]";
        
        assertCSVEquals(expected, "Ensure quoted new lines are preserved when output with CSVOutput.");
    }
       
    /**
     * Make sure multiple + quoted new lines are parsed correctly.
     */
    @Test
    public void multiplenewLineInQuotes() throws Exception
    {
        String expected = "[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New\\nYo\\nrk\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]";
        
        assertCSVEquals(expected, "Multi new line parse.");
    }

    /**
     * Header test.
     */
    @Test
    public void headerLineTest() throws Exception 
    {    
        String expected = "[{\"name\":\"John\",\"age\":\"30\",\"city\":\"NewYork\"},{\"name\":\"Alice\",\"age\":\"25\",\"city\":\"Paris\"}]";
        
        assertCSVEquals(expected, "CSVOutput with header line.");
    }

    /**
     * Spaces are preserved in fields and values.
     */
    @Test
    public void preserveSpacesTest() throws Exception 
    {   
        String expected = "[{\"name\":\"John Doe\",\" age \":\" 30 \",\"city\":\"New York\"}]";
        
        assertCSVEquals(expected, "CSVOutput with preserved spaces.");
    }

    /**
     * Values with optional/not required quotes.
     */
    @Test
    public void optionalQuotesTest() throws Exception 
    {
        String expected = "[{\"name\":\"John\",\"age\":\"30\",\"city\":\"New York\"},{\"name\":\"Alice\",\"age\":\"25\",\"city\":\"Paris\"}]";
        
        assertCSVEquals(expected, "CSVOutput with optional quotes.");
    }

    /**
     * Commas inside values don't interfere.
     */
    @Test
    public void quotesWithSpecialCharsTest() throws Exception 
    {
        String expected = "[{\"name\":\"John\",\"description\":\"Lives in\\nNew York\"},{\"name\":\"Alice\",\"description\":\"Lives,somewhere\"}]";
        
        assertCSVEquals(expected, "CSVOutput with quotes containing special characters.");
    }

    /**
     * Escaped CSV quotes are handled properly.
     */
    @Test
    public void escapedQuotesTest() throws Exception 
    {   
        String expected = "[{\"name\":\"John\",\"quote\":\"His favorite quote is \\\"Hello World\\\"\"},{\"name\":\"Alice\",\"quote\":\"She said \\\"Hi\\\"\"}]";
        
        assertCSVEquals(expected, "CSVOutput with escaped quotes.");
    }
    
    /**
     * Missing values should be assumed as null, a missing value is not technically an empty string.
     */
    @Test
    public void missingValueTest() throws Exception
    {     
        String expected = "[{\"name\":\"John\",\"age\":\"25\",\"quote\":\"His favorite quote is \\\"Hello World\\\"\"},{\"name\":\"Alice\",\"age\":null,\"quote\":\"She said \\\"Hi\\\"\"},{\"name\":\"Bob\",\"age\":\"30\",\"quote\":\"Welcome!\"}]";
        
        assertCSVEquals(expected, "CSVOutput with missing values.");
    }
}
