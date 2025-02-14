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
package com.invirgance.convirgance.transform.sets;

import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class IntersectionIterableTest
{

    @Test
    public void testEmptyMainStream() 
    {
        JSONArray locations = new JSONArray("[]");
        JSONArray visited = new JSONArray("[{\"country\": \"USA\", \"state\": \"NY\"}]");

        String[] fields = {"country", "state"};
        IntersectionIterable intersection = new IntersectionIterable(fields, locations, visited);
        
        assertFalse(intersection.iterator().hasNext(), "Should find no matching records with empty stream");
    }
    
    @Test
    public void testEmptySecondStream() 
    {
        JSONArray locations = new JSONArray("[{\"country\": \"USA\", \"state\": \"NY\"}]");
        JSONArray visited = new JSONArray("[]");

        String[] fields = {"country", "state"};
        IntersectionIterable intersection = new IntersectionIterable(fields, locations, visited);
        
        assertFalse(intersection.iterator().hasNext(), "Should find no matching records with empty stream");
    }
    
    @Test
    public void testMultiEmptyStream() 
    {
        JSONArray locations = new JSONArray("[]");
        JSONArray visited = new JSONArray("[]");

        String[] fields = {"country", "state"};
        IntersectionIterable intersection = new IntersectionIterable(fields, locations, visited);      
        
        assertFalse(intersection.iterator().hasNext(), "Should find no matching records with empty stream");
    }
    
    @Test
    public void testMultiEmptyStreamWithIntersection() 
    {            
        JSONArray planned = new JSONArray("[{\"country\": \"Canada\", \"state\": \"ON\"}, {\"country\": \"USA\", \"state\": \"NY\"}]");
        JSONArray locations = new JSONArray("[]");
        JSONArray visited = new JSONArray("[{\"country\": \"France\", \"state\": \"IDF\"}, {\"country\": \"USA\", \"state\": \"NY\"}]");

        String[] fields = {"country", "state"};       
        IntersectionIterable intersection = new IntersectionIterable(fields, planned, locations, visited);
        
        assertFalse(intersection.iterator().hasNext(), "Should find no matching records with empty stream");
    }   

    @Test
    public void testBasicIntersection() 
    {
        int count = 0;
  
        JSONArray locations = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\", \"height\": 175}," +
            "{\"country\": \"USA\", \"state\": \"CA\", \"height\": 165}," +
            "{\"country\": \"USA\", \"state\": \"NY\", \"height\": 170}" +
        "]");

        JSONArray visited = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\", \"height\": 175}," +
            "{\"country\": \"USA\", \"state\": \"CA\", \"height\": 168}," +
            "{\"country\": \"USA\", \"state\": \"NY\", \"height\": 172}" +
        "]");

        String[] fields = {"country", "state"};
        IntersectionIterable intersection = new IntersectionIterable(fields, locations, visited);

        for(JSONObject item : intersection) 
        {
            count++;
            assertTrue(item.containsKey("country"), "Record should have country field");
            assertTrue(item.containsKey("state"), "Record should have state field");
        }
        
        assertEquals(3, count, "Should find 3 matching records");
    }

    @Test
    public void testNoIntersection() 
    {
        int count = 0;
        
        JSONArray locations = new JSONArray("[" +
            "{\"country\": \"USA\", \"state\": \"CA\"}," +
            "{\"country\": \"USA\", \"state\": \"NY\"}" +
        "]");

        JSONArray visited = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"BC\"}," +
            "{\"country\": \"Canada\", \"state\": \"ON\"}" +
        "]");
        
        String[] fields = {"country", "state"};
        IntersectionIterable intersection = new IntersectionIterable(fields, locations, visited);

        for(JSONObject item : intersection) 
        {
            System.out.println(item);
            count++;
        }
        
        assertEquals(0, count, "Should find no matching records");
    }

    @Test
    public void testPartialIntersection() 
    {
        List<JSONObject> results = new ArrayList<>();    
        
        JSONArray locations = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\", \"city\": \"Toronto\"}," +
            "{\"country\": \"Mexico\", \"state\": \"DF\", \"city\": \"Mexico City\"}," +
            "{\"country\": \"USA\", \"state\": \"NY\", \"city\": \"NYC\"}" +
        "]");

        JSONArray visited = new JSONArray("[" +
            "{\"country\": \"Canada\", \"state\": \"ON\", \"city\": \"Ottawa\"}," +
            "{\"country\": \"USA\", \"state\": \"CA\", \"city\": \"LA\"}," +
            "{\"country\": \"USA\", \"state\": \"NY\", \"city\": \"Buffalo\"}" +
        "]");
        
        String[] fields = {"country", "state"};
        IntersectionIterable intersection = new IntersectionIterable(fields, locations, visited); 
        
        for(JSONObject item : intersection) 
        {
            results.add(item);
        }
        
        assertEquals(2, results.size(), "Should find 2 matching records");
    }

    @Test
    public void testThreeWayIntersection() 
    {      
        List<JSONObject> results = new ArrayList<>();
        
        JSONArray planned = new JSONArray("[{\"country\": \"Canada\", \"state\": \"ON\"}, {\"country\": \"USA\", \"state\": \"NY\"}]");
        JSONArray locations = new JSONArray("[{\"country\": \"Mexico\", \"state\": \"DF\"}, {\"country\": \"USA\", \"state\": \"NY\"}]");
        JSONArray visited = new JSONArray("[{\"country\": \"France\", \"state\": \"IDF\"}, {\"country\": \"USA\", \"state\": \"NY\"}]");

        String[] fields = {"country", "state"};       
        IntersectionIterable intersection = new IntersectionIterable(fields, planned, locations, visited);

        for(JSONObject item : intersection) 
        {
            results.add(item);
        }
        
        assertEquals(1, results.size(), "Should find 1 matching record");
        assertEquals("USA", results.get(0).getString("country"), "Country should be USA");
        assertEquals("NY", results.get(0).getString("state"), "State should be NY");
    }
    
    @Test
    public void testSingleKeyIntersection() 
    {
        int count = 0;
        
        JSONArray locations = new JSONArray("[{\"country\": \"USA\", \"state\": \"CA\"}, {\"country\": \"USA\", \"state\": \"NY\"}]");
        JSONArray visited = new JSONArray("[{\"country\": \"USA\", \"state\": \"FL\"}, {\"country\": \"USA\", \"state\": \"TX\"}]");

        String[] fields = {"country"};
        IntersectionIterable intersection = new IntersectionIterable(fields, locations, visited);

        for(JSONObject item : intersection) 
        {
            count++;
            assertEquals("USA", item.getString("country"), "Country should be USA");
        }
        
        assertEquals(2, count, "Should find 2 matching records");
    }

    @Test
    public void testFiveWayIntersectionWithMixedData() 
    {
        List<JSONObject> results = new ArrayList<>();
        
        JSONArray stocksJune = new JSONArray("[{\"code\": \"ABC\", \"type\": 1, \"value\": 100}, {\"code\": \"XYZ\", \"type\": 2, \"value\": 200}]");
        JSONArray stocksMarch = new JSONArray("[{\"code\": \"ABC\", \"type\": 1, \"value\": 150}, {\"code\": \"DEF\", \"type\": 3, \"value\": 300}]");
        JSONArray stocksApril = new JSONArray("[{\"code\": \"ABC\", \"type\": 1, \"value\": 175}, {\"code\": \"GHI\", \"type\": 4, \"value\": 400}]");
        JSONArray stocksOctober = new JSONArray("[{\"code\": \"ABC\", \"type\": 0, \"value\": 125}, {\"code\": \"ABC\", \"type\": 1, \"value\": 125}, {\"code\": \"JKL\", \"type\": 5, \"value\": 500}]");
        JSONArray stocksSeptember = new JSONArray("[{\"code\": \"ABC\", \"type\": 1, \"value\": 160}, {\"code\": \"MNO\", \"type\": 6, \"value\": 600}]");
        
        String[] fields = {"code", "type"};
        IntersectionIterable intersection = new IntersectionIterable(fields, stocksJune, stocksMarch, stocksApril, stocksOctober, stocksSeptember);

        for(JSONObject item : intersection) 
        {
            results.add(item);
        }
        
        assertEquals(1, results.size(), "Should find one record matching across all five streams");
        assertEquals("ABC", results.get(0).getString("code"));
        assertEquals(1, results.get(0).getInt("type"));
    }

    @Test
    public void testMultipleFieldsWithNulls()
    {
        List<JSONObject> results = new ArrayList<>();
          
        JSONArray test = new JSONArray("[{\"field1\": \"key1\", \"field2\": \"value1\"}, {\"field1\": \"key2\", \"field2\": null}, {\"field1\": null, \"field2\": \"value2\"}]");
        JSONArray comparison = new JSONArray("[{\"field1\": \"key1\", \"field2\": \"value1\"}, {\"field1\": \"key2\", \"field2\": null}, {\"field1\": null, \"field2\": \"value3\"}]");
        
        String[] fields = {"field1", "field2"};        
        IntersectionIterable intersection = new IntersectionIterable(fields, test, comparison);

        for(JSONObject item : intersection)
        {
            results.add(item);
        }

        assertEquals(2, results.size(), "Should find two matching records");

        results.sort((a, b) -> a.getString("field1").compareTo(b.getString("field1")));

        assertEquals("key1", results.get(0).getString("field1"));
        assertEquals("value1", results.get(0).getString("field2"));

        assertEquals("key2", results.get(1).getString("field1"));
        assertNull(results.get(1).get("field2"));
    }
    
    /**
     * This test is to make sure no stream is left behind when another iterator has been pulled through to the checkpoint/largest head(record).
     */
    @Test
    public void testSmallestIteratorCheckpointAdvancement() 
    {
        List<JSONObject> results = new ArrayList<>();        
        
        JSONArray small = new JSONArray("[" +
            "{\"field1\": \"key001\", \"field2\": \"val1\"}," + 
            "{\"field1\": \"key002\", \"field2\": \"val2\"}," +
            "{\"field1\": \"key003\", \"field2\": \"val3\"}," +
            "{\"field1\": \"key004\", \"field2\": \"val4\"}," +
            "{\"field1\": \"key005\", \"field2\": \"val5\"}," +
            "{\"field1\": \"key050\", \"field2\": \"val6\"}," +  
            "{\"field1\": \"key051\", \"field2\": \"val7\"}"  +
        "]");

        JSONArray large = new JSONArray("[" +
            "{\"field1\": \"key050\", \"field2\": \"val6\"}," +  
            "{\"field1\": \"key051\", \"field2\": \"val7\"}" +
        "]");

        JSONArray entropy = new JSONArray("[" +
            "{\"field1\": \"key050\", \"field2\": \"val6\"}," +
            "{\"field1\": \"key051\", \"field2\": \"val7\"}" + 
        "]");

        JSONArray smallEntropy = new JSONArray("[" +
            "{\"field1\": \"key001\", \"field2\": \"val1\"}," +
            "{\"field1\": \"key010\", \"field2\": \"valX\"}," +
            "{\"field1\": \"key020\", \"field2\": \"valY\"}," +
            "{\"field1\": \"key030\", \"field2\": \"valZ\"}," +
            "{\"field1\": \"key050\", \"field2\": \"val6\"}," +  
            "{\"field1\": \"key051\", \"field2\": \"val7\"}" +
        "]");
        
        String[] fields = {"field1", "field2"};   
        IntersectionIterable intersection = new IntersectionIterable(fields, small, large, smallEntropy, entropy);

        for(JSONObject item : intersection) 
        {
            results.add(item);
        }

        assertEquals(2, results.size(), "Should find two matching records");

        assertEquals("key050", results.get(0).getString("field1"));
        assertEquals("val6", results.get(0).getString("field2"));

        assertEquals("key051", results.get(1).getString("field1"));
        assertEquals("val7", results.get(1).getString("field2"));
    }

    @Test
    public void testLargeNumberOfIterables() 
    {
        List<Iterable<JSONObject>> streams = new ArrayList<>();
        
        List<JSONObject> results = new ArrayList<>();
        String generated;
        
        for(int i = 0; i < 10; i++) 
        {
            generated = String.format("[{\"id\": %d, \"shared\": \"common\"}, {\"id\": %d, \"shared\": \"common\"}]", i, 100);       
            streams.add(new JSONArray(generated));
        }
        
        String[] fields = {"id", "shared"};
        IntersectionIterable intersection = new IntersectionIterable(fields, streams);

        for(JSONObject item : intersection) 
        {
            results.add(item);
        }
        
        assertEquals(1, results.size(), "Should find one matching record across all streams");
        assertEquals(100, results.get(0).getInt("id"));
        assertEquals("common", results.get(0).getString("shared"));
    }
    
}
