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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class SortedGroupByTransformerTest
{
    private static String testItems;
    private static SortedGroupByTransformer transformer;
    private static String[] groupFields;
    private static JSONArray transformed;
    
    public SortedGroupByTransformerTest()
    {
    }
    
    @BeforeAll
    public static void setUpClass() {
        testItems = "["
                + "{\"city\": \"Tampa\", \"temp\": 35.2, \"weather\": \"rain\"},"
                + "{\"city\": \"Milwaukee\", \"temp\": 23.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 36.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 32.1, \"weather\": \"overcast\"},"
                + "{\"city\": \"Tampa\", \"temp\": 22.1, \"weather\": \"overcast\"}"
                + "]";
        
        groupFields = new String[]
        {
            "city"
        };
       
        transformed = new JSONArray();
        transformer = new SortedGroupByTransformer(groupFields, "temps");
    }
    
    @BeforeEach
    public  void resetTransformed(){
        transformed = new JSONArray();
    }
    
    /**
     * Test of transform method, of class SortedGroupByTransformer.
     */
    @Test
    public void testTransform()
    {
        String known = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2,\"weather\":\"rain\"}]},{\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23.2,\"weather\":\"sunny\"}]},{\"city\":\"Tampa\",\"temps\":[{\"temp\":36.2,\"weather\":\"sunny\"},{\"temp\":32.1,\"weather\":\"overcast\"},{\"temp\":22.1,\"weather\":\"overcast\"}]}]";
                
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(known);
         
        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        assertTrue(transformed.equals(expected));
    }
    
    /**
     * Make sure nested values are grouped correctly
     */
    @Test
    public void testTransformNested()
    {
        String equal = "[{\"city\":\"Tampa\",\"temps\":[{\"details\":{\"temp\":35.2,\"weather\":\"rain\"}}]},{\"city\":\"Milwaukee\",\"temps\":[{\"details\":{\"temp\":23.2,\"weather\":\"sunny\"}}]},{\"city\":\"Tampa\",\"temps\":[{\"details\":{\"temp\":36.2,\"weather\":\"sunny\"}},{\"details\":{\"temp\":32.1,\"weather\":\"overcast\"}},{\"details\":{\"temp\":22.1,\"weather\":\"overcast\"}}]}]";
        String nested = "[{\"city\": \"Tampa\", \"details\": {\"temp\": 35.2, \"weather\": \"rain\"}},"
          + "{\"city\": \"Milwaukee\", \"details\": {\"temp\": 23.2, \"weather\": \"sunny\"}},"
          + "{\"city\": \"Tampa\", \"details\": {\"temp\": 36.2, \"weather\": \"sunny\"}},"
          + "{\"city\": \"Tampa\", \"details\": {\"temp\": 32.1, \"weather\": \"overcast\"}},"
          + "{\"city\": \"Tampa\", \"details\": {\"temp\": 22.1, \"weather\": \"overcast\"}}]";
        
        JSONArray objects = new JSONArray(nested);
        JSONArray expected = new JSONArray(equal);
         
        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        assertTrue(transformed.equals(expected));
    }    

    /**
     * Test that grouping on multiple fields works as expected.
     */
    @Test
    public void testTransformMultipleFields()
    {
        String known = "[{\"city\":\"Tampa\",\"weather\":\"rain\",\"temps\":[{\"temp\":35.2}]},{\"city\":\"Milwaukee\",\"weather\":\"sunny\",\"temps\":[{\"temp\":23.2}]},{\"city\":\"Tampa\",\"weather\":\"sunny\",\"temps\":[{\"temp\":36.2}]},{\"city\":\"Tampa\",\"weather\":\"overcast\",\"temps\":[{\"temp\":32.1},{\"temp\":22.1}]}]";
        String[] fields = new String[]
        {
            "city","weather"
        };
        
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(known);
        
        SortedGroupByTransformer transformer = new SortedGroupByTransformer(fields, "temps");

        transformer.transform(objects).forEach(elem -> transformed.add(elem));
        
        assertTrue(transformed.equals(expected));
    }

    /**
     * Test that grouping on a missing field works (although a null value is added).
     */
    @Test
    public void testTransformOneMissingField()
    {
        String known = "[{\"keyboard\":null,\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2,\"weather\":\"rain\"}]},{\"keyboard\":null,\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23.2,\"weather\":\"sunny\"}]},{\"keyboard\":null,\"city\":\"Tampa\",\"temps\":[{\"temp\":36.2,\"weather\":\"sunny\"},{\"temp\":32.1,\"weather\":\"overcast\"},{\"temp\":22.1,\"weather\":\"overcast\"}]}]";
      
        String[] fields = new String[]
        {
            "city","keyboard"
        };
             
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(known);

        
        SortedGroupByTransformer transformer = new SortedGroupByTransformer(fields, "temps");
        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        assertTrue(transformed.equals(expected));
    }

    /**
     * Tests that grouping on fields with values that contain the output key
     * preserves the data correctly (nested).
     */
    @Test
    public void testTransformDuplicateFields()
    {       
        String testItems = "["
        + "{\"city\": \"Tampa\", \"temp\": 35.2},"
        + "{\"city\": \"Mexico\", \"temps\": [36.2]},"
        + "{\"city\": \"Mexico\", \"temp\": 30.2},"
        + "{\"city\": \"Tampa\", \"temp\": 32},"
        + "{\"city\": \"Tampa\", \"temp\": 31}"
        + "]";
        String known = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2}]},{\"city\":\"Mexico\",\"temps\":[{\"temps\":[36.2]},{\"temp\":30.2}]},{\"city\":\"Tampa\",\"temps\":[{\"temp\":32},{\"temp\":31}]}]";
        
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(known);      

        transformer.transform(objects).forEach(transformed::add);

        // Verify transformation
        assertTrue(expected.equals(transformed));
    }
    
    /**
     * Fields should be case sensitive.
     */
    @Test
    public void testCaseSensitivityInKeys()
    {
        String test = "[{\"city\": \"Tampa\", \"temp\": 35.2}, {\"City\": \"Tampa\", \"temp\": 36.2}]";
        String known = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2}]},{\"city\":null,\"temps\":[{\"City\":\"Tampa\",\"temp\":36.2}]}]";        
        
        JSONArray objects = new JSONArray(test);
        JSONArray expected = new JSONArray(known);
     
        transformer.transform(objects).forEach(transformed::add);

        assertTrue(expected.equals(transformed));
    }
    
    /**
     * Test that grouping on empty fields will throw.
     */
    @Test
    public void testTransformSillyFields()
    {       
        String[] fields = new String[] {"Fish", ""};       

        // Verify that creating the transformer with empty keys throws an exception
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            new SortedGroupByTransformer(fields, "temps");
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
        String[] fields = new String[] {"Fish", "Dog"};       

        // Verify that creating the transformer with no output throws an exception
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            new SortedGroupByTransformer(fields, "");
        });

        // Assert that the exception message matches what is expected
        assertEquals("Output key must not be null or empty.", exception.getMessage());
    }
    
    /**
     * Test that an exception will be raised if we try to iterate when there are no other items.
     */
    @Test
    public void testTransformNoMoreInput()
    {        
        // Verify that creating the transformer with no output throws an exception
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {           
           transformer.transform(new JSONArray("[]")).iterator().next();
        });

        // Assert that the exception message matches what is expected
        assertEquals("Attempted to iterate with no next element.", exception.getMessage());
    }    
}
