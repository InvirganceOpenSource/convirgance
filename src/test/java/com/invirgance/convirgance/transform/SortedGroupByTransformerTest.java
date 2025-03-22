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
package com.invirgance.convirgance.transform;

import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * For testing the SortedGroupByTransformer.
 * @author tadghh
 */
public class SortedGroupByTransformerTest
{
    
    public SortedGroupByTransformerTest()
    {
    }
    
    private void assertTransformEquals(String input, String known, String message)
    {
        String[] fields = new String[] { "city" };
       
        assertTransformEquals(input, known, message, fields);
    }
    
    // TODO: This entire thing is backwards and wasn't fixed when the issue was raised
    private void assertTransformEquals(String input, String known, String message, String[] fields)
    {
        JSONArray expected = new JSONArray(known);
        
        assertTrue(processTransform(input, fields).equals(expected), message);
    }
    
    private JSONArray processTransform(String input, String[] fields)
    {
        JSONArray objects = new JSONArray(input);

        JSONArray transformed = new JSONArray();
        SortedGroupByTransformer transformer = new SortedGroupByTransformer(fields, "temps");

        transformer.transform(objects).forEach(elem -> transformed.add(elem));

        return transformed;
    }
    
    /**
     * Test of transform method, of class SortedGroupByTransformer.
     */
    @Test
    public void testTransform()
    {
        String test = "["
                + "{\"city\": \"Tampa\", \"temp\": 35.2, \"weather\": \"rain\"},"
                + "{\"city\": \"Milwaukee\", \"temp\": 23.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 36.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 32.1, \"weather\": \"overcast\"},"
                + "{\"city\": \"Tampa\", \"temp\": 22.1, \"weather\": \"overcast\"}"
                + "]";
        String known = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2,\"weather\":\"rain\"}]},{\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23.2,\"weather\":\"sunny\"}]},{\"city\":\"Tampa\",\"temps\":[{\"temp\":36.2,\"weather\":\"sunny\"},{\"temp\":32.1,\"weather\":\"overcast\"},{\"temp\":22.1,\"weather\":\"overcast\"}]}]";
        
        assertTransformEquals(test,known,"Testing basic transform");
    }
    
    /**
     * Make sure nested values are grouped correctly.
     */
    @Test
    public void testTransformNested()
    {
        String nested = "[{\"city\": \"Tampa\", \"details\": {\"temp\": 35.2, \"weather\": \"rain\"}},"
                + "{\"city\": \"Milwaukee\", \"details\": {\"temp\": 23.2, \"weather\": \"sunny\"}},"
                + "{\"city\": \"Tampa\", \"details\": {\"temp\": 36.2, \"weather\": \"sunny\"}},"
                + "{\"city\": \"Tampa\", \"details\": {\"temp\": 32.1, \"weather\": \"overcast\"}},"
                + "{\"city\": \"Tampa\", \"details\": {\"temp\": 22.1, \"weather\": \"overcast\"}}]";
        String equal = "[{\"city\":\"Tampa\",\"temps\":[{\"details\":{\"temp\":35.2,\"weather\":\"rain\"}}]},{\"city\":\"Milwaukee\",\"temps\":[{\"details\":{\"temp\":23.2,\"weather\":\"sunny\"}}]},{\"city\":\"Tampa\",\"temps\":[{\"details\":{\"temp\":36.2,\"weather\":\"sunny\"}},{\"details\":{\"temp\":32.1,\"weather\":\"overcast\"}},{\"details\":{\"temp\":22.1,\"weather\":\"overcast\"}}]}]";

        assertTransformEquals(nested, equal, "Make sure nested values are grouped correctly.");
    }

    /**
     * Test that grouping on multiple fields works as expected.
     */
    @Test
    public void testTransformMultipleFields()
    {
        String test = "["
                + "{\"city\": \"Tampa\", \"temp\": 35.2, \"weather\": \"rain\"},"
                + "{\"city\": \"Milwaukee\", \"temp\": 23.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 36.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 32.1, \"weather\": \"overcast\"},"
                + "{\"city\": \"Tampa\", \"temp\": 22.1, \"weather\": \"overcast\"}"
                + "]";
        String known = "[{\"city\":\"Tampa\",\"weather\":\"rain\",\"temps\":[{\"temp\":35.2}]},{\"city\":\"Milwaukee\",\"weather\":\"sunny\",\"temps\":[{\"temp\":23.2}]},{\"city\":\"Tampa\",\"weather\":\"sunny\",\"temps\":[{\"temp\":36.2}]},{\"city\":\"Tampa\",\"weather\":\"overcast\",\"temps\":[{\"temp\":32.1},{\"temp\":22.1}]}]";
        String[] fields = new String[]
        {
            "city","weather"
        };
        
        assertTransformEquals(test,known,"Testing multiple field group",fields);
    }

    /**
     * Test that grouping on a missing field works (although a null value is added).
     */
    @Test
    public void testTransformOneMissingField()
    {
        String test = "["
                + "{\"city\": \"Tampa\", \"temp\": 35.2, \"weather\": \"rain\"},"
                + "{\"city\": \"Milwaukee\", \"temp\": 23.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 36.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 32.1, \"weather\": \"overcast\"},"
                + "{\"city\": \"Tampa\", \"temp\": 22.1, \"weather\": \"overcast\"}"
                + "]";
        
        String expected = "[" +
                "{\"keyboard\":null,\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2,\"weather\":\"rain\"}]}," +
                "{\"keyboard\":null,\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23.2,\"weather\":\"sunny\"}]}," +
                "{\"keyboard\":null,\"city\":\"Tampa\",\"temps\":[{\"temp\":36.2,\"weather\":\"sunny\"},{\"temp\":32.1,\"weather\":\"overcast\"},{\"temp\":22.1,\"weather\":\"overcast\"}]}," +
                "]";
        
        String[] fields = new String[] { "city", "keyboard" };
        
        assertEquals(new JSONArray(expected), processTransform(test, fields));
    }
    
    @Test
    public void testNullValues()
    {
        String expected = "[" +
                "{\"city\":\"Tampa\",\"temps\":[]}," +
                "{\"city\":\"Milwaukee\",\"temps\":[{\"temp\":23.2}]}" +
                "]";
        
        String test = "["
                + "{\"city\": \"Tampa\"},"
                + "{\"city\": \"Milwaukee\", \"temp\": 23.2}"
                + "]";
        
        String[] fields = new String[] { "city" };
        
        assertEquals(new JSONArray(expected), processTransform(test, fields));
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
        String known = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2}]},{\"city\":\"Mexico\",\"temps\":[{\"temps\":[36.2]},{\"temp\":30.2}]},{\"city\":\"Tampa\",\"temps\":[{\"temp\":32},{\"temp\":31}]}]";

       assertTransformEquals(test,known,"Testing duplicate output fields");
    }
    
    /**
     * Fields should be case sensitive.
     */
    @Test
    public void testCaseSensitivityInKeys()
    {
        String test = "["
                + "{\"city\": \"Tampa\", \"temp\": 35.2},"
                + "{\"city\": \"Mexico\", \"temps\": [36.2]},"
                + "{\"city\": \"Mexico\", \"temp\": 30.2},"
                + "{\"City\": \"Tampa\", \"temp\": 32},"
                + "{\"city\": \"Tampa\", \"temp\": 31},"
                + "{\"City\": \"Washington\", \"temp\": 32}"
                + "]";
        String known = "[{\"city\":\"Tampa\",\"temps\":[{\"temp\":35.2}]},{\"city\":\"Mexico\",\"temps\":[{\"temps\":[36.2]},{\"temp\":30.2}]},{\"city\":null,\"temps\":[{\"City\":\"Tampa\",\"temp\":32}]},{\"city\":\"Tampa\",\"temps\":[{\"temp\":31}]},{\"city\":null,\"temps\":[{\"City\":\"Washington\",\"temp\":32}]}]";        
        
        assertTransformEquals(test,known,"Testing case sensitivity");
    }
    
}
