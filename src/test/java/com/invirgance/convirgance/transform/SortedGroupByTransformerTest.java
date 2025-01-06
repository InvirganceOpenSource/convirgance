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
import com.invirgance.convirgance.json.JSONObject;
import java.util.Iterator;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class SortedGroupByTransformerTest
{
    
    public SortedGroupByTransformerTest()
    {
    }

    /**
     * Test of transform method, of class SortedGroupByTransformer.
     */
    @Test
    public void testTransform()
    {
        String jsonArray = "["
                + "{\"city\": \"Tampa\", \"temp\": 35.2, \"weather\": \"rain\"},"
                + "{\"city\": \"Milwaukee\", \"temp\": 23.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 36.2, \"weather\": \"sunny\"},"
                + "{\"city\": \"Tampa\", \"temp\": 32.1, \"weather\": \"overcast\"}"
                + "]";
                String[] groupKeys = new String[]{"city"};
        String outputKey = "temps";
        JSONArray jsonObjects = new JSONArray(jsonArray);
        SortedGroupByTransformer transformer = new SortedGroupByTransformer(groupKeys,outputKey);
        Iterator<JSONObject> result = instance.transform(iterator);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
