/*
 * The MIT License
 *
 * Copyright 2025 Invirgance LLC
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
package com.invirgance.convirgance.transform.sets;


import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class ComplementIterableTest
{
    
    @Test
    public void testBasicComplement() 
    {
        int count = 0;
        
        JSONArray stream1 = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\", \"height\": 175}," +
            "{\"country\": \"USA\", \"state\": \"CA\", \"height\": 165}," +
            "{\"country\": \"USA\", \"state\": \"NY\", \"height\": 170}" +
        "]");

        JSONArray stream2 = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\", \"height\": 175}," +
            "{\"country\": \"USA\", \"state\": \"NY\", \"height\": 170}" +
        "]");

        String[] fields = {"country", "state"};
        ComplementIterable complement = new ComplementIterable(fields, stream1, stream2);

        for(JSONObject item : complement) 
        {
            count++;
            assertTrue(item.containsKey("country"), "Record should have country field");
            assertTrue(item.containsKey("state"), "Record should have state field");
            assertEquals("USA", item.getString("country"), "Should be USA record");
            assertEquals("CA", item.getString("state"), "Should be CA state");
        }
        
        assertEquals(1, count, "Should find 1 unique record");
    }

    @Test
    public void testNoComplement() 
    {        
        JSONArray stream1 = new JSONArray("[" +
            "{\"country\": \"USA\", \"state\": \"CA\"}," +
            "{\"country\": \"USA\", \"state\": \"NY\"}" +
        "]");

        JSONArray stream2 = new JSONArray("[" +
            "{\"country\": \"USA\", \"state\": \"CA\"}," +
            "{\"country\": \"USA\", \"state\": \"NY\"}" +
        "]");
        
        String[] fields = {"country", "state"};
        ComplementIterable complement = new ComplementIterable(fields, stream1, stream2);
        
        assertFalse(complement.iterator().hasNext(), "Should find no unique records");
    }

    @Test
    public void testEmptySecondStream() 
    {
        List<JSONObject> results = new ArrayList<>();
        
        JSONArray stream1 = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\"}," +
            "{\"country\": \"USA\", \"state\": \"CA\"}" +
        "]");

        JSONArray stream2 = new JSONArray("[]");
        
        String[] fields = {"country", "state"};
        ComplementIterable complement = new ComplementIterable(fields, stream1, stream2);
        
        for(JSONObject item : complement) 
        {
            results.add(item);
        }
        
        assertEquals(2, results.size(), "Should find all records from first stream");
    }
    
    @Test
    public void testEmptyStreams() 
    {   
        JSONArray stream1 = new JSONArray("[]");

        JSONArray stream2 = new JSONArray("[]");
        
        String[] fields = {"country", "state"};
        ComplementIterable complement = new ComplementIterable(fields, stream1, stream2);
        
        assertFalse(complement.iterator().hasNext(), "Should contain no records");
    }

    @Test
    public void testEmptyFirstStream() 
    {
        JSONArray stream1 = new JSONArray("[]");
        
        JSONArray stream2 = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\"}," +
            "{\"country\": \"USA\", \"state\": \"CA\"}" +
        "]");

        String[] fields = {"country", "state"};
        ComplementIterable complement = new ComplementIterable(fields, stream1, stream2);
        
        assertFalse(complement.iterator().hasNext(), "Should find no records with empty first stream");
    }
    
    @Test
    public void testSingleStream() 
    {     
        JSONArray stream1 = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\"}," +
            "{\"country\": \"USA\", \"state\": \"CA\"}" +
        "]");
        
        List<Iterable<JSONObject>> streams =  new ArrayList<>();
        streams.add(stream1);
        
        String[] fields = {"country", "state"};
        ComplementIterable complement = new ComplementIterable(fields, streams);
        
        assertFalse(complement.iterator().hasNext(), "Should find no records with empty first stream");
    }

    @Test
    public void testThreeWayComplement() 
    {
        List<JSONObject> results = new ArrayList<>();
        
        JSONArray stream1 = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\"}," +
            "{\"country\": \"USA\", \"state\": \"CA\"}," +
            "{\"country\": \"Mexico\", \"state\": \"DF\"}" +
        "]");

        JSONArray stream2 = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\"}" +
        "]");

        JSONArray stream3 = new JSONArray("[" +
            "{\"country\": \"USA\", \"state\": \"CA\"}" +
        "]");

        String[] fields = {"country", "state"};
        ComplementIterable complement = new ComplementIterable(fields, stream1, stream2, stream3);

        for(JSONObject item : complement) 
        {
            results.add(item);
        }
        
        assertEquals(1, results.size(), "Should find 1 unique record");
        assertEquals("Mexico", results.get(0).getString("country"), "Country should be Mexico");
        assertEquals("DF", results.get(0).getString("state"), "State should be DF");
    }
  
    @Test
    public void testSmallestIteratorCheckpointAdvancement() 
    {
        List<JSONObject> results = new ArrayList<>();
        
        JSONArray small = new JSONArray("[" +
            "{\"field1\": \"key001\", \"field2\": \"val1\"}," +
            "{\"field1\": \"key002\", \"field2\": \"val2\"}," +
            "{\"field1\": \"key003\", \"field2\": \"val3\"}," +
            "{\"field1\": \"key004\", \"field2\": \"val4\"}," +
            "{\"field1\": \"key005\", \"field2\": \"val5\"}" +
        "]");

        JSONArray large = new JSONArray("[" +
            "{\"field1\": \"key003\", \"field2\": \"val3\"}," +
            "{\"field1\": \"key004\", \"field2\": \"val4\"}" +
        "]");

        JSONArray entropy = new JSONArray("[" +
            "{\"field1\": \"key004\", \"field2\": \"val4\"}" +
        "]");
        
        String[] fields = {"field1", "field2"};
        ComplementIterable complement = new ComplementIterable(fields, small, large, entropy);

        for(JSONObject item : complement) 
        {
            results.add(item);
        }

        assertEquals(3, results.size(), "Should find three unique records");
        assertEquals("key001", results.get(0).getString("field1"));
        assertEquals("key002", results.get(1).getString("field1"));
        assertEquals("key005", results.get(2).getString("field1"));
    }
}
