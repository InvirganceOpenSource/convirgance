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
    private static String[] groupKeys;
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
        
        groupKeys = new String[]
        {
            "city"
        };
       
        transformed = new JSONArray();
        transformer = new UnsortedGroupByTransformer(groupKeys, "temps");
    }
    
   /**
     * Test of transform method, of class UnsortedGroupByTransformer.
     */
    @Test
    public void testTransform()
    {
        String equalTest = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35,\"weather\":\"rain\"},{\"temp\":36,\"weather\":\"sunny\"},{\"temp\":32,\"weather\":\"overcast\"},{\"temp\":22,\"weather\":\"overcast\"}]},{\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23,\"weather\":\"sunny\"}]}]";
                
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(equalTest);
        
        transformer.transform(objects).forEach(elem -> transformed.add(elem));       

        assertEquals(transformed.toString(),expected.toString());
    }    
    
    /**
     * Make sure nested values are grouped correctly
     */
    @Test
    public void testTransformNested()
    {
        String equal = "[{\"city\":\"Tampa\",\"temps\":[{\"details\":{\"temp\":35.2,\"weather\":\"rain\"}},{\"details\":{\"temp\":36.2,\"weather\":\"sunny\"}},{\"details\":{\"temp\":32.1,\"weather\":\"overcast\"}},{\"details\":{\"temp\":22.1,\"weather\":\"overcast\"}}]},{\"city\":\"Milwaukee\",\"temps\":[{\"details\":{\"temp\":23.2,\"weather\":\"sunny\"}}]}]";
        String nested = "[{\"city\": \"Tampa\", \"details\": {\"temp\": 35.2, \"weather\": \"rain\"}},"
          + "{\"city\": \"Milwaukee\", \"details\": {\"temp\": 23.2, \"weather\": \"sunny\"}},"
          + "{\"city\": \"Tampa\", \"details\": {\"temp\": 36.2, \"weather\": \"sunny\"}},"
          + "{\"city\": \"Tampa\", \"details\": {\"temp\": 32.1, \"weather\": \"overcast\"}},"
          + "{\"city\": \"Tampa\", \"details\": {\"temp\": 22.1, \"weather\": \"overcast\"}}]";
        
        JSONArray objects = new JSONArray(nested);
        JSONArray expected = new JSONArray(equal);
         
        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        assertEquals(transformed.toString(),expected.toString());
    }    

    /**
     * Test that grouping on multiple fields works as expected.
     */
    @Test
    public void testTransformMultipleFields()
    {
        String equalTest = "[{\"city\":\"Tampa\",\"weather\":\"rain\",\"temps\":[{\"temp\":35}]},{\"city\":\"Milwaukee\",\"weather\":\"sunny\",\"temps\":[{\"temp\":23}]},{\"city\":\"Tampa\",\"weather\":\"sunny\",\"temps\":[{\"temp\":36}]},{\"city\":\"Tampa\",\"weather\":\"overcast\",\"temps\":[{\"temp\":32},{\"temp\":22}]}]";
        String[] keys = new String[]
        {
            "city","weather"
        };
        
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(equalTest);
        
        UnsortedGroupByTransformer transformer = new UnsortedGroupByTransformer(keys, "temps");

        transformer.transform(objects).forEach(elem -> transformed.add(elem));
        
        assertEquals(transformed.toString(),expected.toString());
    }

    /**
     * Test that grouping on a missing field works (although a null value is added).
     */
    @Test
    public void testTransformOneMissingField()
    {
        String equalTest = "[{\"keyboard\":null,\"city\":\"Tampa\",\"temps\":[{\"temp\":35,\"weather\":\"rain\"},{\"temp\":36,\"weather\":\"sunny\"},{\"temp\":32,\"weather\":\"overcast\"},{\"temp\":22,\"weather\":\"overcast\"}]},{\"keyboard\":null,\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23,\"weather\":\"sunny\"}]}]";
      
        String[] keys = new String[]
        {
            "city","keyboard"
        };
             
        JSONArray objects = new JSONArray(testItems);
        JSONArray expected = new JSONArray(equalTest);
        
        UnsortedGroupByTransformer transformer = new UnsortedGroupByTransformer(keys, "temps");
        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        assertEquals(transformed.toString(),expected.toString());
    }

    /**
     * Tests that grouping on fields with values that contain the output key
     * preserves the data correctly (nested).
     */
//    Bad Test
//    @Test
//    public void testTransformDuplicateFields()
//    {       
//        String testItems = "[{\"city\": \"Tampa\", \"temp\": 35.2}, {\"city\": \"Tampa\", \"temps\": [36.2]}]";
//        String expectedDuplicate = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2},{\"temps\":[36.2]}]}]";
//        
//        JSONArray objects = new JSONArray(testItems);
//        JSONArray expected = new JSONArray(expectedDuplicate);
//        
//        String[] groupKeys = new String[]
//        {
//            "city"
//        };
//        
//        UnsortedGroupByTransformer transformer = new UnsortedGroupByTransformer(groupKeys, "temps");
//
//        transformer.transform(objects).forEach(transformed::add);
//
//        // Verify transformation
//        assertTrue(expected.equals(transformed));
//    }
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
        String[] keys = new String[] {"Fish", ""};       

        // Verify that creating the transformer with empty keys throws an exception
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            new UnsortedGroupByTransformer(keys, "temps");
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
        String[] keys = new String[] {"Fish", "Dog"};       

        // Verify that creating the transformer with no output throws an exception
        Exception exception = assertThrows(ConvirganceException.class, () ->
        {
            new UnsortedGroupByTransformer(keys, "");
        });

        // Assert that the exception message matches what is expected
        assertEquals("Output key must not be null or empty.", exception.getMessage());
    }
 
}
