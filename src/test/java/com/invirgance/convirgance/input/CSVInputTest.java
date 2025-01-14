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

import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.Source;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
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
    @Test
    public void emptyCSVTest()
    {
        String test = "";
        String expected ="";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();
        instance.read(reader.getClass());
         for(JSONObject item : input){
          output.add(item);
        }
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void singleColumnMultipleRows()
    {
        String test = ""; // Empty CSV
        String expected = ""; // Expected output for an empty CSV

        reader = new StringReader(test); // Create reader
                Source source = new Source(reader); // Assuming Source has a constructor that accepts a Reader
        List<JSONObject> input = new ArrayList<>(); // Initialize input
        List<JSONObject> output = new ArrayList<>(); // Initialize output

        CSVInput instance = new CSVInput(); // Create instance of CSVInput

        // Assuming read method populates `input` list directly
        instance.read(source); // Pass the StringReader to the read method

        // Process each item in the input list
        for (JSONObject item : input) {
            output.add(item);
        }
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void commaInQuotes()
    {
        String test = "Name, Age, City\n\"John, Doe\", 30, \"New York\"\n\"Alice, Wonderland\", 25, \"Paris\"";
        String expected ="";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void unescapedQuotesInQuoted()
    {
        String test = "Name, Age, City\n\"John \"Doe\"\", 30, \"New York\"\n\"Alice \"\"Wonderland\"\"\", 25, \"Paris\"";        String expected ="";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void mismatchedQuotes()
    {
        String test = "Name, Age, City\n\"John, 30, \"New York\"\n\"Alice, 25, \"Paris\"";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void extraCols()
    {
        String test = "Name, Age, City\nJohn, 30, New York, ExtraColumn\nAlice, 25, Paris, ExtraColumn";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void trailingComma()
    {
        String test ="Name, Age, City,\nJohn, 30, New York,\nAlice, 25, Paris,";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void newlineInQuotedField()
    {
        String test ="Name, Age, City\n\"John Doe\nNew York\", 30, \"New York\"\n\"Alice Wonderland\nParis\", 25, \"Paris\"";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void missingValueInRow()
    {
        String test ="Name, Age, City\nJohn, 30, New York\nAlice, , Paris\nBob, 25, ";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void embeddedNewlines()
    {
        String test ="Name, Age, City\nJohn, 30, New\nYork\nAlice, 25, Paris";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void lineBreaksInQuotes()
    {
        String test ="Name, Age, City\n\"John Doe\nNew York\", 30, \"New York\"\n\"Alice Wonderland\nParis\", 25, \"Paris\"";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void valueWithSingleQuote()
    {
        String test ="Name, Age, City\nJohn, 30, \"O'Reilly\"\nAlice, 25, \"O'Brien\"";
       reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
    /**
     * Test of read method, of class CSVInput.
     */
    @Test
    public void multiLineBreak()
    {
        String test ="a,b,\"This is a long line that\nwraps\"";
        reader = new StringReader(test);
        
        Source source = null;
        CSVInput instance = new CSVInput();

        
    }
    
}
