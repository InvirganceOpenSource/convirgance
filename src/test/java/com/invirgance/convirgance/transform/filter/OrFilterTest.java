/*
 * Copyright 2024 INVIRGANCE LLC

Permission is hereby granted, free of charge, to any person obtaining a copy 
of this software and associated documentation files (the “Software”), to deal 
in the Software without restriction, including without limitation the rights to 
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
of the Software, and to permit persons to whom the Software is furnished to do 
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
SOFTWARE.
 */
package com.invirgance.convirgance.transform.filter;

import com.invirgance.convirgance.json.JSONObject;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class OrFilterTest
{
    @Test
    public void testFilters()
    {
        JSONObject record1 = new JSONObject("{\"x\":1,\"y\":2}");
        JSONObject record2 = new JSONObject("{\"x\":3,\"y\":4}");
        JSONObject record3 = new JSONObject("{\"x\":1,\"y\":4}");
        JSONObject record4 = new JSONObject("{\"x\":3,\"y\":2}");
        OrFilter filter = new OrFilter(new EqualsFilter("x", 1), new EqualsFilter("y", 2));
        
        assertTrue(filter.filter(record1));
        assertFalse(filter.filter(record2));
        assertTrue(filter.filter(record3));
        assertTrue(filter.filter(record4));
        
        // Default with no filters is false for OR since "all conditions" (none) were not true
        assertFalse(new OrFilter(new ArrayList<Filter>()).filter(record1));
    }
    
}
