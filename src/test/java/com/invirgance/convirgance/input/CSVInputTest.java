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

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.ByteArraySource;
import com.invirgance.convirgance.source.InputStreamSource;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 *
 * @author tadghh
 */
public class CSVInputTest
{
    private static CSVInput tester;
    private static JSONArray output;
    private static JSONArray expected;
    private static StringReader reader;
    private static ByteArraySource inputStream;
    private static InputStreamSource source;
    
    public CSVInputTest()
    {
    }

    private void assertCSVEquals(String input, JSONArray expected, String message)
    {
        processCSV(input);
        assertEquals(expected.toString(), output.toString(), message);
    }

    private void processCSV(String input)
    {
        reader = new StringReader(input);
        inputStream = new ByteArraySource(input.getBytes());
        source = new InputStreamSource(inputStream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }
    }
    
    
    @BeforeAll
    public static void setUpClass()
    {
        tester = new CSVInput();
        output = new JSONArray();
        expected = new JSONArray();

    }
    
    @BeforeEach
    public  void resetTransformed(){
        output = new JSONArray();      
        expected = new JSONArray();
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void normalInput()
    {
        String test = "Name,Age,City\nJohn,30,New York\nAlice,25,Paris";

        expected = new JSONArray("[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New York\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]");
        assertCSVEquals(test, expected, "Test of read method, of class CSVInput.");
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void newLineInQuotes()
    {
        String test = "Name,Age,City\nJohn,30,\"New\nYork\"\nAlice,25,Paris";

        expected = new JSONArray("[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New\\nYork\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]");
        assertCSVEquals(test, expected, "Test of read method, of class CSVInput.");
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void multiplenewLineInQuotes()
    {
        String test = "Name,Age,City\nJohn,30,\"New\nYo\nrk\"\nAlice,25,Paris";

        expected = new JSONArray("[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New\\nYo\\nrk\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]");
        assertCSVEquals(test, expected, "Test of read method, of class CSVInput.");
    }
    
    /**
     * Basic read to end CRLF test.
     */
    @Test
    public void basicCRLFTest() {
        String test = "field1,field2\r\naaa,bbb\r\nzzz,yyy";
        
        expected = new JSONArray("[{\"field1\":\"aaa\",\"field2\":\"bbb\"},{\"field1\":\"zzz\",\"field2\":\"yyy\"}]");
        assertCSVEquals(test, expected, "Basic CRLF delimited CSV");
    }

    /**
     * Can still read records that don't end with CRLF.
     */
    @Test
    public void noFinalLineBreakTest() {
        String test = "field1,field2\r\naaa,bbb\r\nzzz,yyy";
        expected = new JSONArray("[{\"field1\":\"aaa\",\"field2\":\"bbb\"},{\"field1\":\"zzz\",\"field2\":\"yyy\"}]");
        assertCSVEquals(test, expected, "CSV without final line break");
    }

    /**
     * Header test.
     */
    @Test
    public void headerLineTest() {
        String test = "name,age,city\r\nJohn,30,NewYork\r\nAlice,25,Paris";
        expected = new JSONArray("[{\"name\":\"John\",\"age\":\"30\",\"city\":\"NewYork\"},{\"name\":\"Alice\",\"age\":\"25\",\"city\":\"Paris\"}]");
        assertCSVEquals(test, expected, "CSV with header line");
    }

    /**
     * Spaces are preserved in fields and values.
     */
    @Test
    public void preserveSpacesTest() {
        String test = "name, age ,city\r\nJohn Doe, 30 ,New York";
        expected = new JSONArray("[{\"name\":\"John Doe\",\" age \":\" 30 \",\"city\":\"New York\"}]");
        assertCSVEquals(test, expected, "CSV with preserved spaces");
    }

    /**
     * Values with optional/not required quotes.
     */
    @Test
    public void optionalQuotesTest() {
        String test = "name,age,city\r\n\"John\",30,\"New York\"\r\nAlice,\"25\",Paris";
        expected = new JSONArray("[{\"name\":\"John\",\"age\":\"30\",\"city\":\"New York\"},{\"name\":\"Alice\",\"age\":\"25\",\"city\":\"Paris\"}]");
        assertCSVEquals(test, expected, "CSV with optional quotes");
    }

    /**
     * Commas inside values don't interfere.
     */
    @Test
    public void quotesWithSpecialCharsTest() {
        String test = "name,description\r\n\"John\",\"Lives in\r\nNew York\"\r\n\"Alice\",\"Lives,somewhere\"";
        expected = new JSONArray("[{\"name\":\"John\",\"description\":\"Lives in\\nNew York\"},{\"name\":\"Alice\",\"description\":\"Lives,somewhere\"}]");
        assertCSVEquals(test, expected, "CSV with quotes containing special characters");
    }

    /**
     * Escaped CSV quotes are handled properly.
     */
    @Test
    public void escapedQuotesTest() {
        String test = "name,quote\r\n\"John\",\"His favorite quote is \"\"Hello World\"\"\"\r\n\"Alice\",\"She said \"\"Hi\"\"\"";
        expected = new JSONArray("[{\"name\":\"John\",\"quote\":\"His favorite quote is \\\"Hello World\\\"\"},{\"name\":\"Alice\",\"quote\":\"She said \\\"Hi\\\"\"}]");
        assertCSVEquals(test, expected, "CSV with escaped quotes");
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

        reader = new StringReader(test);
        inputStream = new ByteArraySource(test.getBytes());
        source = new InputStreamSource(inputStream.getInputStream());
        tester.read(source);
        
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            tester.read(source).iterator();
        });

        // Assert that the exception message matches what is expected
        assertEquals("CSV file is empty - no header row found.", exception.getMessage());
    }
}
