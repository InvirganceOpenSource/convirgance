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

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.input.CSVInput;
import com.invirgance.convirgance.input.InputCursor;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.ByteArraySource;
import com.invirgance.convirgance.source.InputStreamSource;
import com.invirgance.convirgance.target.ByteArrayTarget;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests to ensure the JSON content is output to CSV properly following specifications.
 * 
 * @author tadghh
 */
public class CSVOutputTest
{
    
    public CSVOutputTest()
    {
    }

    private void assertCSVEquals(String input, String known, String message) throws Exception
    {
        CSVInput tester = new CSVInput();
        JSONArray output = new JSONArray();
        JSONArray expected = new JSONArray(known);

        ByteArraySource inputStream = new ByteArraySource(processCSV(known).getBytes());
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());
        
        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }

        assertTrue(output.equals(expected), message);
    }

    private String processCSV(String input) throws Exception
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
     * Generic read test.
     */
    @Test
    public void normalInput() throws Exception
    {
        String test = "Name,Age,City\nJohn,30,New York\nAlice,25,Paris";

        String expected ="[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New York\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]";
        assertCSVEquals(test, expected, "Test of read method, of class CSVInput.");
    }   
        
    /**
     * Make sure new lines within quotes are parsed correctly.
     */
    @Test
    public void newLineInQuotes() throws Exception
    {
        String test = "Name,Age,City\nJohn,30,\"New\nYork\"\nAlice,25,Paris";

        String expected = "[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New\\nYo\\nrk\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]";
        assertCSVEquals(test, expected, "Test of read method, of class CSVInput.");
    }
       
    /**
     * Make sure multiple + quoted new lines are parsed correctly.
     */
    @Test
    public void multiplenewLineInQuotes() throws Exception
    {
        String test = "Name,Age,City\nJohn,30,\"New\nYo\nrk\"\nAlice,25,Paris";

        String expected = "[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New\\nYo\\nrk\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]";
        assertCSVEquals(test, expected, "Multi new line parse.");
    }
    
    /**
     * Basic read to end CRLF test.
     * @throws Exception .
     */
    @Test
    public void basicCRLFTest() throws Exception
    {
        String test = "field1,field2\r\naaa,bbb\r\nzzz,yyy";
        
        String expected = "[{\"field1\":\"aaa\",\"field2\":\"bbb\"},{\"field1\":\"zzz\",\"field2\":\"yyy\"}]";
        assertCSVEquals(test, expected, "Basic CRLF delimited CSV.");
    }

    /**
     * Can still read records that don't end with CRLF.
     */
    @Test
    public void noFinalLineBreakTest()  throws Exception
    {
        String test = "field1,field2\r\naaa,bbb\r\nzzz,yyy";
        
        String expected = "[{\"field1\":\"aaa\",\"field2\":\"bbb\"},{\"field1\":\"zzz\",\"field2\":\"yyy\"}]";
        assertCSVEquals(test, expected, "CSV without final line break.");
    }

    /**
     * Header test.
     */
    @Test
    public void headerLineTest()  throws Exception {
        String test = "name,age,city\r\nJohn,30,NewYork\r\nAlice,25,Paris";
        
        String expected = "[{\"name\":\"John\",\"age\":\"30\",\"city\":\"NewYork\"},{\"name\":\"Alice\",\"age\":\"25\",\"city\":\"Paris\"}]";
        assertCSVEquals(test, expected, "CSV with header line.");
    }

    /**
     * Spaces are preserved in fields and values.
     */
    @Test
    public void preserveSpacesTest()  throws Exception {
        String test = "name, age ,city\r\nJohn Doe, 30 ,New York";
        
        String expected = "[{\"name\":\"John Doe\",\" age \":\" 30 \",\"city\":\"New York\"}]";
        assertCSVEquals(test, expected, "CSV with preserved spaces.");
    }

    /**
     * Values with optional/not required quotes.
     */
    @Test
    public void optionalQuotesTest()  throws Exception {
        String test = "name,age,city\r\n\"John\",30,\"New York\"\r\nAlice,\"25\",Paris";
        
        String expected = "[{\"name\":\"John\",\"age\":\"30\",\"city\":\"New York\"},{\"name\":\"Alice\",\"age\":\"25\",\"city\":\"Paris\"}]";
        assertCSVEquals(test, expected, "CSV with optional quotes.");
    }

    /**
     * Commas inside values don't interfere.
     */
    @Test
    public void quotesWithSpecialCharsTest()  throws Exception {
        String test = "name,description\r\n\"John\",\"Lives in\r\nNew York\"\r\n\"Alice\",\"Lives,somewhere\"";
        
        String expected = "[{\"name\":\"John\",\"description\":\"Lives in\\nNew York\"},{\"name\":\"Alice\",\"description\":\"Lives,somewhere\"}]";
        assertCSVEquals(test, expected, "CSV with quotes containing special characters.");
    }

    /**
     * Escaped CSV quotes are handled properly.
     */
    @Test
    public void escapedQuotesTest()  throws Exception 
    {
        String test = "name,quote\r\n\"John\",\"His favorite quote is \"\"Hello World\"\"\"\r\n\"Alice\",\"She said \"\"Hi\"\"\"";
        
        String expected = "[{\"name\":\"John\",\"quote\":\"His favorite quote is \\\"Hello World\\\"\"},{\"name\":\"Alice\",\"quote\":\"She said \\\"Hi\\\"\"}]";
        assertCSVEquals(test, expected, "CSV with escaped quotes.");
    }
    
    /**
     * Missing values should be assumed as null, a missing value is not technically an empty string.
     */
    @Test
    public void missingValueTest()  throws Exception
    {
        String test = "name,age,quote\r\n\"John\",25,\"His favorite quote is \"\"Hello World\"\"\"\r\n\"Alice\",,\"She said \"\"Hi\"\"\"\r\n\"Bob\",30,\"Welcome!\"";

        String expected = "[{\"name\":\"John\",\"age\":\"25\",\"quote\":\"His favorite quote is \\\"Hello World\\\"\"},{\"name\":\"Alice\",\"age\":null,\"quote\":\"She said \\\"Hi\\\"\"},{\"name\":\"Bob\",\"age\":\"30\",\"quote\":\"Welcome!\"}]";
        assertCSVEquals(test, expected, "CSV with escaped quotes.");
    }

    /**
     * Test that an exception will be raised if the header is missing. 
     * This test could be improved, there's no way to define missing header as 
     * it could be a row of data. For now we just assume the first row is the header.
     */
    @Test
    public void testTransformNoMoreInput()
    {
        String test = "";
        ByteArraySource inputStream = new ByteArraySource(test.getBytes());
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());
        InputCursor<JSONObject> tester = new CSVInput().read(source);
        
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            tester.iterator();
        });

        // Assert that the exception message matches what is expected
        assertEquals("CSV file is empty - no header row found.", exception.getMessage());
    }
}
