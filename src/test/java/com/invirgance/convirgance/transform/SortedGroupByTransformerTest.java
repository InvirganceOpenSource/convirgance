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
package com.invirgance.convirgance.transform;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class SortedGroupByTransformerTest
{
    private static String testItems;
    public SortedGroupByTransformerTest()
    {
    }
    @BeforeAll
    public static void setUpClass() {
        testItems = "["
                + "{\"city\": \"Tampa\", \"temp\": 35.2, \"weather\": \"rain\"},"
                + "{\"city\": \"Milwaukee\", \"temp\": 23.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 36.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 32.1, \"weather\": \"overcast\"}"
                + "]";
  
    }
    
    /**
     * Test of transform method, of class SortedGroupByTransformer.
     */
    @Test
    public void testTransform()
    {
        String equalTest = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2,\"weather\":\"rain\"}]},{\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23.2,\"weather\":\"sunny\"}]},{\"city\":\"Tampa\",\"temps\":[{\"temp\":36.2,\"weather\":\"sunny\"},{\"temp\":32.1,\"weather\":\"overcast\"}]}]";

        
        String[] groupKeys = new String[]
        {
            "city"
        };
        
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(equalTest);
        JSONArray transformed = new JSONArray();
        
        SortedGroupByTransformer transformer = new SortedGroupByTransformer(groupKeys, "temps");
        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        assertTrue(transformed.equals(expected));
    }
    

    /**
     * Test that grouping on multiple fields works as expected.
     */
    @Test
    public void testTransformMultipleFields()
    {
        String equalTest = "[{\"city\":\"Tampa\",\"weather\":\"rain\",\"temps\":[{\"temp\":35.2}]},{\"city\":\"Milwaukee\",\"weather\":\"sunny\",\"temps\":[{\"temp\":23.2}]},{\"city\":\"Tampa\",\"weather\":\"sunny\",\"temps\":[{\"temp\":36.2}]},{\"city\":\"Tampa\",\"weather\":\"overcast\",\"temps\":[{\"temp\":32.1}]}]";

        
        String[] groupKeys = new String[]
        {
            "city", "weather"
        };
        
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(equalTest);
        JSONArray transformed = new JSONArray();
        
        SortedGroupByTransformer transformer = new SortedGroupByTransformer(groupKeys, "temps");
        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        assertTrue(transformed.equals(expected));
    }
    
    /**
     * Test that grouping on no fields raises and exception.
     */
    @Test
    public void testTransformNoFields()
    {       
        String[] groupKeys = new String[] {};       

        // Verify that creating the transformer with empty keys throws an exception
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            new SortedGroupByTransformer(groupKeys, "temps");
        });

        // Assert that the exception message matches what is expected
        assertEquals("Fields must not be null or empty.", exception.getMessage());
    }
    
    /**
     * Test that grouping on empty fields will throw.
     */
    @Test
    public void testTransformSillyFields()
    {       
        String[] groupKeys = new String[] {"Fish", ""};       

        // Verify that creating the transformer with empty keys throws an exception
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            new SortedGroupByTransformer(groupKeys, "temps");
        });

        // Assert that the exception message matches what is expected
        assertEquals("Fields must not contain null or empty values.", exception.getMessage());
    }

    /**
     * Test that an exception will throw when no output key is provided.
     */
    @Test
    public void testTransformNoOutput()
    {       
        String[] groupKeys = new String[] {"Fish", "Dog"};       

        // Verify that creating the transformer with empty keys throws an exception
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            new SortedGroupByTransformer(groupKeys, "");
        });

        // Assert that the exception message matches what is expected
        assertEquals("Output key must not be null or empty.", exception.getMessage());
    }
}
