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
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class CSVInputTest
{
    private static InputCursor<JSONObject> input;
    private static CSVInput tester;
    private static JSONArray output;
    private static StringReader reader;
    private static ByteArraySource inputStream;
    private static InputStreamSource source;
    
    public CSVInputTest()
    {
    }

    @BeforeAll
    public static void setUpClass()
    {
        tester = new CSVInput();
        output = new JSONArray();

    }
    
    @BeforeEach
    public  void resetTransformed(){
        output = new JSONArray();      
    }
    
    /**
     * Test of read method, of class CSVInput.
     */

//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void singleColumnMultipleRows()
//    {
//        String test = ""; // Empty CSV
//        String expected = ""; // Expected output for an empty CSV
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void newLineInQuotes()
    {
        String test = "Name,Age,City\nJohn,30,\"New\nYork\"\nAlice,25,Paris";
        reader = new StringReader(test);
        inputStream = new ByteArraySource(test.getBytes());
        source = new InputStreamSource(inputStream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }

        JSONArray expectedJson = new JSONArray("[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New\\nYork\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]");

        assertTrue(expectedJson.equals(output));
    }
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void normalInput()
    {
        String test = "Name,Age,City\nJohn,30,New York\nAlice,25,Paris";
        reader = new StringReader(test);
        inputStream = new ByteArraySource(test.getBytes());
        source = new InputStreamSource(inputStream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }

        JSONArray expectedJson = new JSONArray("[{\"Name\":\"John\",\"Age\":\"30\",\"City\":\"New York\"},{\"Name\":\"Alice\",\"Age\":\"25\",\"City\":\"Paris\"}]");

        assertTrue(expectedJson.equals(output));
    }

//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void unescapedQuotesInQuoted()
//    {
//        String test = "Name, Age, City\n\"John \"Doe\"\", 30, \"New York\"\n\"Alice \"\"Wonderland\"\"\", 25, \"Paris\"";
//        String expected = "";
//        
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void mismatchedQuotes()
//    {
//        String test = "Name, Age, City\n\"John, 30, \"New York\"\n\"Alice, 25, \"Paris\"";
//        String expected = "";
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void extraCols()
//    {
//        String test = "Name, Age, City\nJohn, 30, New York, ExtraColumn\nAlice, 25, Paris, ExtraColumn";
//        String expected = "";
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void trailingComma()
//    {
//        String test = "Name, Age, City,\nJohn, 30, New York,\nAlice, 25, Paris,";
//        String expected = "";
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void newlineInQuotedField()
//    {
//        String test = "Name, Age, City\n\"John Doe\nNew York\", 30, \"New York\"\n\"Alice Wonderland\nParis\", 25, \"Paris\"";
//        String expected = "";
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void missingValueInRow()
//    {
//        String test = "Name, Age, City\nJohn, 30, New York\nAlice, , Paris\nBob, 25, ";
//        String expected = "";
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void embeddedNewlines()
//    {
//        String test = "Name, Age, City\nJohn, 30, New\nYork\nAlice, 25, Paris";
//        String expected = "";
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        
//
//        for (JSONObject item : tester.read(source))
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void lineBreaksInQuotes()
//    {
//        String test = "Name, Age, City\n\"John Doe\nNew York\", 30, \"New York\"\n\"Alice Wonderland\nParis\", 25, \"Paris\"";
//        String expected = "";
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void valueWithSingleQuote()
//    {
//        String test = "Name, Age, City\nJohn, 30, \"O'Reilly\"\nAlice, 25, \"O'Brien\"";
//        String expected = "";
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//
//    /**
//     * Test of read method, of class CSVInput.
//     */
//    @Test
//    public void multiLineBreak()
//    {
//        String test = "a,b,\"This is a long line that\nwraps\"";
//        String expected = "";
//        reader = new StringReader(test);
//
//        inputStream = new ByteArraySource(test.getBytes());
//
//        source = new InputStreamSource(inputStream.getInputStream());
//
//        tester.read(source);
//
//        for (JSONObject item : input)
//        {
//            output.add(item);
//        }
//
//        assertEquals("Output does not match expected for empty CSV", expected, output.toString());
//    }
//    
 
    /**
     * Test that an exception will be raised if the header is missing. 
     * This test could be improved, theres no way to define missing header as 
     * it could be a row of data.
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
