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

import com.invirgance.convirgance.json.JSONArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        
        System.out.println(transformed.toString());
        System.out.println(expected.toString());
        System.out.println("\nTemp Array Elements and HashCodes:");
        System.out.println("\nTemp Array Temps HashCodes:");

        assertEquals(transformed.toString(),expected.toString());
    }    
}
