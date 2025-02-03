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
import com.invirgance.convirgance.target.OutputStreamTarget;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
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

    private byte[] outputCSV(String input) throws Exception
    {
        ByteArrayTarget target = new ByteArrayTarget();   
        CSVOutput output = new CSVOutput();  

        try(OutputCursor cursor = output.write(target))
        {
            cursor.write(new JSONArray(input));
        } 
        
        target.getBytes();
   
        return target.getBytes();
    }
    
    /**
     * Covers most edge cases related to CSV spec.
     */
    @Test
    public void testGeneralOutput() throws Exception
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
        
        CSVInput tester = new CSVInput();
        JSONArray output = new JSONArray();
        JSONArray expected = new JSONArray(test);

        ByteArraySource inputStream = new ByteArraySource(outputCSV(test));
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());
        
        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }
        
        assertTrue(expected.equals(output), "CSV output test for most edge cases.");
    }
    
    /**
     * Make sure missing values are output as null.
     */
    @Test
    public void testMissingValue() throws Exception
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
        
        ByteArraySource inputStream = new ByteArraySource(outputCSV(test));
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }

        jsonOutput = (JSONObject) output.get(1);
        assertTrue(jsonOutput.containsValue(null));
    }
    
    /**
     * Write an empty 'file'.
     */
    @Test
    public void testNoRecords() throws Exception
    {
        String test = "[]";
        String result;
        CSVOutput output = new CSVOutput();
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);     
        OutputStreamTarget target = new OutputStreamTarget(printStream);
            
        try (OutputCursor cursor = output.write(target))
        {
            cursor.write(new JSONArray(test));
        }
        
        result = byteArrayOutputStream.toString();

        assertTrue(result.length() == 0);
    }
    
    /**
     * Extra values should be removed.
     */
    @Test
    public void testExtraValue() throws Exception
    {
        /*
            The following cases are tested:
            - Extra values
        */
        
        String test = "["
                + "{\"name\":\"John\", \"age\":\"30\"},"
                + "{\"name\":\"Bob\", \"age\":\"30\", \"quote\":\"Welcome!\"}"
                + "]";

        JSONArray output = new JSONArray();
        CSVInput tester = new CSVInput();
        JSONObject jsonOutput;
        
        ByteArraySource inputStream = new ByteArraySource(outputCSV(test));
        InputStreamSource source = new InputStreamSource(inputStream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }
        
        jsonOutput = (JSONObject) output.get(1);
        assertTrue(!jsonOutput.containsKey("quote"));
    }
    
    /**
     * Output only contains the provided header.
     */
    @Test
    public void testCustomHeader() throws Exception
    {
        String test = "["
                + "{\"name\":\"John\", \"age\":\"30\"},"
                + "{\"name\":\"Bob\", \"age\":\"30\", \"quote\":\"Welcome!\"}"
                + "]";
        
        ByteArrayTarget target = new ByteArrayTarget();   
        CSVOutput outputCSV = new CSVOutput(new String[]{"name"});
        ByteArraySource stream;
        InputStreamSource source;    

        JSONArray output = new JSONArray();
        CSVInput tester = new CSVInput();
        JSONObject jsonOutput;
        
        try(OutputCursor cursor = outputCSV.write(target))
        {
            cursor.write(new JSONArray(test));
        } 
            
        stream = new ByteArraySource(target.getBytes());
        source = new InputStreamSource(stream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            output.add(item);
        }

        jsonOutput = (JSONObject) output.get(1);
        
        assertTrue(
                !jsonOutput.containsKey("quote") 
                && !jsonOutput.containsKey("age") 
                && jsonOutput.containsKey("name")
        );
    }
    
    /**
     * Output contains the provided headers including ones that have no value.
     */
    @Test
    public void testCustomHeaderExtra() throws Exception
    {
        String test = "["
                + "{\"name\":\"John\", \"age\":\"30\"},"
                + "{\"name\":\"Bob\", \"age\":\"30\", \"quote\":\"Welcome!\"}"
                + "]";

        InputStreamSource source;
        ByteArraySource stream;
        ByteArrayTarget target = new ByteArrayTarget();   
        CSVOutput output = new CSVOutput(new String[]{"name","accident"});
        
        JSONArray verify = new JSONArray();
        CSVInput tester = new CSVInput();
        JSONObject jsonOutput;
        
        try(OutputCursor cursor = output.write(target))
        {
            cursor.write(new JSONArray(test));
        } 
            
        stream = new ByteArraySource(target.getBytes());
        source = new InputStreamSource(stream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            verify.add(item);
        }

        jsonOutput = (JSONObject) verify.get(1);
        
        assertTrue(
                !jsonOutput.containsKey("quote") 
                && !jsonOutput.containsKey("age") 
                && jsonOutput.containsKey("name")
                && jsonOutput.containsKey("accident")
        );
    }
    
    /**
     * Make sure CSVOutput headers are updated (if needed) when the cursor is run.
     */
    @Test
    public void testHeaderSync() throws Exception
    {
        String[] headers = new String[]{"name","age"};
        String test = "["
                + "{\"name\":\"John\", \"age\":\"30\"},"
                + "{\"name\":\"Bob\", \"age\":\"30\", \"quote\":\"Welcome!\"}"
                + "]";

        InputStreamSource source;
        ByteArraySource stream;
        ByteArrayTarget target = new ByteArrayTarget();   
        CSVOutput output = new CSVOutput();
        
        JSONArray verify = new JSONArray();
        CSVInput tester = new CSVInput();
            
        try(OutputCursor cursor = output.write(target))
        {
            cursor.write(new JSONArray(test));
        } 
            
        stream = new ByteArraySource(target.getBytes());
        source = new InputStreamSource(stream.getInputStream());

        for (JSONObject item : tester.read(source))
        {
            verify.add(item);
        }
      
        assertTrue(Arrays.equals(output.getHeaders(),headers));                 
    }
    
    /**
     * Make sure CSVOutput throws an exception when using invalid encoding.
     */
    @Test
    public void testBadEncodingSet()
    {
        String test = "["
                + "{\"name\":\"John\", \"age\":\"30\"},"
                + "{\"name\":\"Bob\", \"age\":\"30\", \"quote\":\"Welcome!\"}"
                + "]";

        ByteArrayTarget target = new ByteArrayTarget();   
        CSVOutput output = new CSVOutput();
        
        output.setEncoding("Base64");
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            try (OutputCursor cursor = output.write(target))
            {
                cursor.write(new JSONArray(test));
            }
        });
        
        assertEquals("Failed to initialize CSV output writer", exception.getMessage());
    }
}
