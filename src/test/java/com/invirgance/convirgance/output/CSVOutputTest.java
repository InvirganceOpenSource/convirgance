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
     * Covers most edge cases related to CSV spec.
     */
    @Test
    public void generalOutputTest() throws Exception
    {
        /*
            The following cases are tested:
            - Multi-line text
            - Quoted strings
            - Escaped quotes
            - Preserved whitespace            
            - Field alignment
        */
        
        String test = "["
                + "{\"name\":\"John\", \"age\":\"30\", \"city\":\"New\nYo\nrk\", \"quote\":\"His favorite quote is \\\"Hello World\\\"\"},"
                + "{\"name\":\"John Doe\", \"age\":\" 30 \", \"city\":\"New York\", \"quote\":\"Lives in\\nNew York\"},"
                + "{\"name\":\"Alice\", \"age\":\"25\", \"city\":\"Paris\", \"quote\":\"She said \\\"Hi\\\"\"},"
                + "{\"name\":\"Bob\", \"age\":\"30\",  \"city\": null, \"quote\":\"Welcome!\"}"             
                + "]";
        
        assertCSVEquals(test, "CSV output test for most edge cases.");
    }
    
    /**
     * Make sure missing values are output as null.
     */
    @Test
    public void missingValueTest() throws Exception
    {
        /*
            The following cases are tested:
            - Multi-line text
            - Quoted strings
            - Escaped quotes
            - Missing/null values
        */
        
        String test = "["
                + "{\"name\":\"John\", \"age\":\"30\", \"city\":\"New\nYo\nrk\", \"quote\":\"His favorite quote is \\\"Hello World\\\"\"},"
                + "{\"name\":\"Bob\", \"age\":\"30\", \"quote\":\"Welcome!\"}"             
                + "]";

        JSONArray output = new JSONArray();
        CSVInput tester = new CSVInput();
        JSONObject jsonOutput;
        
        ByteArraySource inputStream = new ByteArraySource(outputCSV(test).getBytes());
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }

        jsonOutput = (JSONObject) output.get(1);
        assertTrue(jsonOutput.containsValue(null));
    }
    
    /**
     * Should throw if no records are found.
     */
    @Test
    public void noRecordsTest() throws Exception
    {
        String test = "[{}]";

        ByteArrayTarget target = new ByteArrayTarget();
        CSVOutput output2 = new CSVOutput();

        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            try (OutputCursor cursor = output2.write(target))
            {
                cursor.write(new JSONArray(test));
            }
        });

        assertEquals("Input data did not contain any records.", exception.getMessage());
    }
}
