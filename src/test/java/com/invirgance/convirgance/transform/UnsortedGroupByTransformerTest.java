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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class UnsortedGroupByTransformerTest
{
    private static String testItems;
    private static UnsortedGroupByTransformer transformer;
    private static String[] groupFields;
    private static JSONArray transformed;
    
    public UnsortedGroupByTransformerTest()
    {
    }

    
    @BeforeEach
    public  void resetTransformed(){
        transformed = new JSONArray();
    }
        
    @BeforeAll
    public static void setUpClass() {
        testItems = "[{\"city\": \"Tampa\", \"temp\": 35, \"weather\": \"rain\"}, {\"city\": \"Milwaukee\", \"temp\": 23, \"weather\": \"sunny\"}, {\"city\": \"Tampa\", \"temp\": 36, \"weather\": \"sunny\"}, {\"city\": \"Tampa\", \"temp\": 32, \"weather\": \"overcast\"}, {\"city\": \"Tampa\", \"temp\": 22, \"weather\": \"overcast\"}]";
        
        groupFields = new String[]
        {
            "city"
        };
       
        transformed = new JSONArray();
        transformer = new UnsortedGroupByTransformer(groupFields, "temps");
    }
    
   /**
     * Test of transform method, of class UnsortedGroupByTransformer.
     */
    @Test
    public void testTransform()
    {
        String known = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35,\"weather\":\"rain\"},{\"temp\":36,\"weather\":\"sunny\"},{\"temp\":32,\"weather\":\"overcast\"},{\"temp\":22,\"weather\":\"overcast\"}]},{\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23,\"weather\":\"sunny\"}]}]";
                
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(known);
        
        transformer.transform(objects).forEach(elem -> transformed.add(elem));       

        assertEquals(transformed.toString(),expected.toString());
    }    
    
    /**
     * Make sure nested values are grouped correctly
     */
    @Test
    public void testTransformNested()
    {
        String known = "[{\"city\":\"Tampa\",\"temps\":[{\"details\":{\"temp\":35.2,\"weather\":\"rain\"}},{\"details\":{\"temp\":36.2,\"weather\":\"sunny\"}},{\"details\":{\"temp\":32.1,\"weather\":\"overcast\"}},{\"details\":{\"temp\":22.1,\"weather\":\"overcast\"}}]},{\"city\":\"Milwaukee\",\"temps\":[{\"details\":{\"temp\":23.2,\"weather\":\"sunny\"}}]}]";
        String nested = "[{\"city\": \"Tampa\", \"details\": {\"temp\": 35.2, \"weather\": \"rain\"}},"
          + "{\"city\": \"Milwaukee\", \"details\": {\"temp\": 23.2, \"weather\": \"sunny\"}},"
          + "{\"city\": \"Tampa\", \"details\": {\"temp\": 36.2, \"weather\": \"sunny\"}},"
          + "{\"city\": \"Tampa\", \"details\": {\"temp\": 32.1, \"weather\": \"overcast\"}},"
          + "{\"city\": \"Tampa\", \"details\": {\"temp\": 22.1, \"weather\": \"overcast\"}}]";
        
        JSONArray objects = new JSONArray(nested);
        JSONArray expected = new JSONArray(known);
         
        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        assertEquals(transformed.toString(),expected.toString());
    }    

    /**
     * Test that grouping on multiple fields works as expected.
     */
    @Test
    public void testTransformMultipleFields()
    {
        String known = "[{\"city\":\"Tampa\",\"weather\":\"rain\",\"temps\":[{\"temp\":35}]},{\"city\":\"Milwaukee\",\"weather\":\"sunny\",\"temps\":[{\"temp\":23}]},{\"city\":\"Tampa\",\"weather\":\"sunny\",\"temps\":[{\"temp\":36}]},{\"city\":\"Tampa\",\"weather\":\"overcast\",\"temps\":[{\"temp\":32},{\"temp\":22}]}]";
        
        String[] fields = new String[]
        {
            "city","weather"
        };
        
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(known);
        
        UnsortedGroupByTransformer transformer = new UnsortedGroupByTransformer(fields, "temps");

        transformer.transform(objects).forEach(elem -> transformed.add(elem));
        
        assertEquals(transformed.toString(),expected.toString());
    }

    /**
     * Test that grouping on a missing field works (although a null value is added).
     */
    @Test
    public void testTransformOneMissingField()
    {
        String known = "[{\"keyboard\":null,\"city\":\"Tampa\",\"temps\":[{\"temp\":35,\"weather\":\"rain\"},{\"temp\":36,\"weather\":\"sunny\"},{\"temp\":32,\"weather\":\"overcast\"},{\"temp\":22,\"weather\":\"overcast\"}]},{\"keyboard\":null,\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23,\"weather\":\"sunny\"}]}]";
      
        String[] fields = new String[]
        {
            "city","keyboard"
        };
             
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(known);
        
        UnsortedGroupByTransformer transformer = new UnsortedGroupByTransformer(fields, "temps");
        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        assertEquals(transformed.toString(),expected.toString());
    }

    /**
     * Tests that grouping on fields with values that contain the output key
     * preserves the data correctly (nested).
     */
    @Test
    public void testTransformDuplicateFields()
    {       
        String test = "["
        + "{\"city\": \"Tampa\", \"temp\": 35.2},"
        + "{\"city\": \"Mexico\", \"temps\": [36.2]},"
        + "{\"city\": \"Mexico\", \"temp\": 30.2},"
        + "{\"city\": \"Tampa\", \"temp\": 32},"
        + "{\"city\": \"Tampa\", \"temp\": 31}"
        + "]";
        String known = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2},{\"temp\":32},{\"temp\":31}]},{\"city\":\"Mexico\",\"temps\":[{\"temps\":[36.2]},{\"temp\":30.2}]}]";
        
        JSONArray objects = new JSONArray(test);
        JSONArray expected = new JSONArray(known);
        
        String[] fields = new String[]
        {
            "city"
        };
        
        UnsortedGroupByTransformer transformer = new UnsortedGroupByTransformer(fields, "temps");

        transformer.transform(objects).forEach(transformed::add);

        // Verify transformation
        assertEquals(transformed.toString(),expected.toString());
    }
//
//    Bad Test     
//    /**
//     * Fields should be case sensitive.
//     */
//    @Test
//    public void testCaseSensitivityInKeys()
//    {
//        String testItems = "[{\"city\": \"Tampa\", \"temp\": 35.2}, {\"City\": \"Tampa\", \"temp\": 36.2}]";
//        String equalObj = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2}]},{\"city\":null,\"temps\":[{\"City\":\"Tampa\",\"temp\":36.2}]}]";        
//        
//        JSONArray objects = new JSONArray(testItems);
//        JSONArray objectsEq = new JSONArray(equalObj);
//     
//        transformer.transform(objects).forEach(transformed::add);
//
//        assertTrue(objectsEq.equals(transformed));
//    }
    
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
            new UnsortedGroupByTransformer(fields, "temps");
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
            new UnsortedGroupByTransformer(fields, "");
        });

        // Assert that the exception message matches what is expected
        assertEquals("Output key must not be null or empty.", exception.getMessage());
    }
 
}
