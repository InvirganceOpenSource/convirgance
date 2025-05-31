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
package com.invirgance.convirgance.transform.filter;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.input.JSONInput;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.FileSource;
import com.invirgance.convirgance.transform.ValueGenerator;
import java.util.ArrayList;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class ContainsFilterTest
{

    @Test
    public void testContainsFilter()
    {
        String key = "industry";
        String value = "Services";
        
        ArrayList<JSONObject> wanted = new ArrayList<JSONObject>();
        
        FileSource source = new FileSource("src/test/resources/generic/generic_data_sorted_id.json");
        Iterator<JSONObject> records = new JSONInput().read(source).iterator();
        
        ContainsFilter filter = new ContainsFilter(key, value);
        Iterator<JSONObject> filtered = filter.transform(records);
        
        while(filtered.hasNext())
        {
            wanted.add(filtered.next());
        }
        
        for(JSONObject record : wanted)
        {
            assertTrue(record.getString(key).contains(value));
        }
    }
    
    @Test
    public void testContainsFilterUnrelatedKey()
    {
        String key = "Car";
        String value = "Services";
        
        FileSource source = new FileSource("src/test/resources/generic/generic_data_sorted_id.json");
        Iterator<JSONObject> records = new JSONInput().read(source).iterator();
        
        ContainsFilter filter = new ContainsFilter(key, value);
        Iterator<JSONObject> filtered = filter.transform(records);
        
        assertFalse(filtered.hasNext());
    }
    
    @Test
    public void testValueGenerator()
    {
        String key = "color";
        
        JSONObject record1 = new JSONObject("{\"color\": \"metallic red\"}");
        JSONObject record2 = new JSONObject("{\"color\": \"ocean blue\"}");
        ValueGenerator generator = new ValueGenerator() {
            @Override
            public Object generate(JSONObject record) throws ConvirganceException {
                return "red";
            }
        };
        
        ContainsFilter filter = new ContainsFilter(key, generator);
        
        assertTrue(filter.test(record1));
        assertFalse(filter.test(record2));
    }
    
}
