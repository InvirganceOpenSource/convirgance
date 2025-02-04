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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests to ensure CSVs are properly parsed according to RFC 4180. 
 * The expected output is a JSONObject for each row of values. 
 * 
 * If a value is missing it will be considered blank.
 * If headers are provided and the CSV file has its own headers the files headers will be considered a row of values (you will end up with a JSONObject of headers).
 * 
 * @author tadghh
 */
public class CSVInputTest
{

    private void assertCSVEquals(String input, JSONArray expected, String message)
    {
        assertTrue(processCSV(input).equals(expected),message);
    }

    private JSONArray processCSV(String input)
    {     
        CSVInput tester = new CSVInput();
        JSONArray output = new JSONArray();
        
        ByteArraySource inputStream = new ByteArraySource(input.getBytes());
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());
        
        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }
        
        return output;
    }
 
    /**
     * Simple read test.
     */
    @Test
    public void testNormalInput()
    {
        String test = "Name,Age,City\nJohn,30,New York\nAlice,25,Paris";

        JSONArray expected = new JSONArray("[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New York\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]");
        assertCSVEquals(test, expected, "Simple read test.");
    }
    
    /**
     * Make sure new lines within quotes are parsed correctly.
     */
    @Test
    public void testNewLineInQuotes()
    {
        String test = "Name,Age,City\nJohn,30,\"New\nYork\"\nAlice,25,Paris";

        JSONArray expected = new JSONArray("[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New\\nYork\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]");
        assertCSVEquals(test, expected, "Make sure quotes in new lines are preserved.");
    }
    
    /**
     *  Make sure multiple + quoted new lines are parsed correctly.
     */
    @Test
    public void testMultiplenewLineInQuotes()
    {
        String test = "Name,Age,City\nJohn,30,\"New\nYo\nrk\"\nAlice,25,Paris";

        JSONArray expected = new JSONArray("[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New\\nYo\\nrk\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]");
        assertCSVEquals(test, expected, "Make sure multiple new lines are preserved.");
    }
    
    /**
     * Basic read to end CRLF test.
     */
    @Test
    public void testBasicCRLF() 
    {
        String test = "field1,field2\r\naaa,bbb\r\nzzz,yyy\r\n";
        
        JSONArray expected = new JSONArray("[{\"field1\":\"aaa\",\"field2\":\"bbb\"},{\"field1\":\"zzz\",\"field2\":\"yyy\"}]");
        assertCSVEquals(test, expected, "Read to end with ending crlf.");
    }

    /**
     * Can still read records that don't end with CRLF.
     */
    @Test
    public void testNoFinalLineBreak() 
    {
        String test = "field1,field2\r\naaa,bbb\r\nzzz,yyy";
        
        JSONArray expected = new JSONArray("[{\"field1\":\"aaa\",\"field2\":\"bbb\"},{\"field1\":\"zzz\",\"field2\":\"yyy\"}]");
        assertCSVEquals(test, expected, "CSV without final line break.");
    }

    /**
     * Header test.
     */
    @Test
    public void testHeaderLine() 
    {
        String test = "name,age,city\r\nJohn,30,NewYork\r\nAlice,25,Paris";
        
        JSONArray expected = new JSONArray("[{\"name\":\"John\",\"age\":\"30\",\"city\":\"NewYork\"},{\"name\":\"Alice\",\"age\":\"25\",\"city\":\"Paris\"}]");
        assertCSVEquals(test, expected, "CSV with header line.");
    }

    /**
     * Spaces are preserved in fields and values.
     */
    @Test
    public void testPreserveSpaces() 
    {
        String test = "name, age ,city\r\nJohn Doe, 30 ,New York";
        
        JSONArray expected = new JSONArray("[{\"name\":\"John Doe\",\" age \":\" 30 \",\"city\":\"New York\"}]");
        assertCSVEquals(test, expected, "CSV with preserved spaces.");
    }

    /**
     * Values with optional/not required quotes.
     */
    @Test
    public void testOptionalQuotes() 
    {
        String test = "name,age,city\r\n\"John\",30,\"New York\"\r\nAlice,\"25\",Paris";
        
        JSONArray expected = new JSONArray("[{\"name\":\"John\",\"age\":\"30\",\"city\":\"New York\"},{\"name\":\"Alice\",\"age\":\"25\",\"city\":\"Paris\"}]");
        assertCSVEquals(test, expected, "CSV with optional quotes.");
    }

    /**
     * Commas inside values don't interfere.
     */
    @Test
    public void testQuotesWithSpecialChars() 
    {
        String test = "name,description\r\n\"John\",\"Lives in\r\nNew York\"\r\n\"Alice\",\"Lives,somewhere\"";
        
        JSONArray expected = new JSONArray("[{\"name\":\"John\",\"description\":\"Lives in\\nNew York\"},{\"name\":\"Alice\",\"description\":\"Lives,somewhere\"}]");
        assertCSVEquals(test, expected, "CSV with quotes containing special characters.");
    }

    /**
     * Escaped CSV quotes are handled properly.
     */
    @Test
    public void testEscapedQuotes() 
    {
        String test = "name,quote\r\n\"John\",\"His favorite quote is \"\"Hello World\"\"\"\r\n\"Alice\",\"She said \"\"Hi\"\"\"";
        
        JSONArray expected = new JSONArray("[{\"name\":\"John\",\"quote\":\"His favorite quote is \\\"Hello World\\\"\"},{\"name\":\"Alice\",\"quote\":\"She said \\\"Hi\\\"\"}]");
        assertCSVEquals(test, expected, "CSV with escaped quotes.");
    }
    
    /**
     * Missing values should be assumed as null, a missing value is not technically an empty string.
     */
    @Test
    public void testMissingValue() 
    {
        String test = "name,age,quote\r\n\"John\",25,\"His favorite quote is \"\"Hello World\"\"\"\r\n\"Alice\",,\"She said \"\"Hi\"\"\"\r\n\"Bob\",30,\"Welcome!\"";

        JSONArray expected = new JSONArray("[{\"name\":\"John\",\"age\":\"25\",\"quote\":\"His favorite quote is \\\"Hello World\\\"\"},{\"name\":\"Alice\",\"age\":null,\"quote\":\"She said \\\"Hi\\\"\"},{\"name\":\"Bob\",\"age\":\"30\",\"quote\":\"Welcome!\"}]");
        assertCSVEquals(test, expected, "CSV with escaped quotes.");
    }

    
    /**
     * Test that headers can be predefined, and any other headers values are removed.
     */
    @Test
    public void testSetHeaders()
    {
        String[] example = new String[] { "Name" };
        String test = "John,30,New York\nAlice,25,Paris";

        JSONArray output = new JSONArray();
        JSONArray expected = new JSONArray("[{\"Name\":\"John\"},{\"Name\":\"Alice\"}]");
        CSVInput tester = new CSVInput();
          
        ByteArraySource inputStream = new ByteArraySource(test.getBytes());
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());

        tester.setHeaders(example);
        
        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }

        assertEquals(expected, output);
    }
    
    /**
     * Make sure CSVInput headers are updated (if needed) when the cursor is run.
     */
    @Test
    public void testHeadersSync()
    {
        String[] expected = new String[]
        {
            "Name","Age","City"
        };
        String test = "Name,Age,City\nJohn,30,New York\nAlice,25,Paris";

        JSONArray output = new JSONArray();
        CSVInput tester = new CSVInput();
            
        ByteArraySource inputStream = new ByteArraySource(test.getBytes());
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }
    
        assertArrayEquals(expected, tester.getHeaders());
    }
    
    /**
     * Make sure CSVInput headers are used correctly, when the file does not contain headers itself.
     */
    @Test
    public void testUseProvidedHeadersWhenFileIsMissingHeaders()
    {
        String[] headers = new String[]
        {
            "Name","Age","City"
        };
        String test = "John,30,New York\nAlice,25,Paris";
        String json = "[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New York\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]";
        
        JSONArray expected = new JSONArray(json);
        JSONArray output = new JSONArray();
        CSVInput tester = new CSVInput();
            
        ByteArraySource inputStream = new ByteArraySource(test.getBytes());
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());

        tester.setHeaders(headers);
        
        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }

        assertTrue(expected.equals(output));
    }
    
    /**
     * Make sure CSVInput throws an exception when using invalid encoding.
     */
    @Test
    public void testBadEncodingSet()
    {
        String test = "Name,Age,City\nJohn,30,New York\nAlice,25,Paris";

        JSONArray output = new JSONArray();
        CSVInput tester = new CSVInput();
           
        ByteArraySource inputStream = new ByteArraySource(test.getBytes());
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());
        
        tester.setEncoding("Base64");
        
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            for (JSONObject item : tester.read(source))
            {
                output.add(item);
            }
        });
      
        assertEquals("Failed to initialize CSV reader", exception.getMessage());
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
