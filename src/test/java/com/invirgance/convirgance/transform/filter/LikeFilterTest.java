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
package com.invirgance.convirgance.transform.filter;

import com.invirgance.convirgance.input.JSONInput;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.FileSource;
import java.util.ArrayList;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class LikeFilterTest
{
    
    public LikeFilterTest()
    {
    }
    
    @Test
    public void testLikeWildcard()
    {
        String key = "industry";
        String value = "%it%";
        
        ArrayList<JSONObject> wanted = new ArrayList<JSONObject>();
        
        FileSource source = new FileSource("src/test/resources/generic/generic_data_sorted_id.json");
        Iterator<JSONObject> records = new JSONInput().read(source).iterator();
        
        LikeFilter filter = new LikeFilter(key, value);
        Iterator<JSONObject> filtered = filter.transform(records);
        
        while(filtered.hasNext())
        {
            wanted.add(filtered.next());
        }

        assertTrue(wanted.size() == 1581);
    }
    
    @Test
    public void testLikeCustomWildcard()
    {
        char wildcard = '?';
        String key = "industry";
        String value = "?it?";
        
        ArrayList<JSONObject> wanted = new ArrayList<JSONObject>();
        
        FileSource source = new FileSource("src/test/resources/generic/generic_data_sorted_id.json");
        Iterator<JSONObject> records = new JSONInput().read(source).iterator();
        
        LikeFilter filter = new LikeFilter(key, value);
        Iterator<JSONObject> filtered = filter.transform(records);
        
        filter.setWildcard(wildcard);
        
        while(filtered.hasNext())
        {
            wanted.add(filtered.next());
        }

        assertTrue(wanted.size() == 1581);
    }
    
    @Test
    public void testSingleWildcard()
    {
        String key = "industry";
        String value = "Alternative Medic_ne";
        
        ArrayList<JSONObject> wanted = new ArrayList<JSONObject>();
        
        FileSource source = new FileSource("src/test/resources/generic/generic_data_sorted_id.json");
        Iterator<JSONObject> records = new JSONInput().read(source).iterator();
        
        LikeFilter filter = new LikeFilter(key, value);
        Iterator<JSONObject> filtered = filter.transform(records);
        
        while(filtered.hasNext())
        {
            wanted.add(filtered.next());
        }

        assertTrue(wanted.size() == 258);
    }
    
    @Test
    public void testSingleAndMultiWildcard()
    {
        String key = "industry";
        String value = "Go__rnment%";
        
        ArrayList<JSONObject> wanted = new ArrayList<JSONObject>();
        
        FileSource source = new FileSource("src/test/resources/generic/generic_data_sorted_id.json");
        Iterator<JSONObject> records = new JSONInput().read(source).iterator();
        
        LikeFilter filter = new LikeFilter(key, value);
        Iterator<JSONObject> filtered = filter.transform(records);
        
        while(filtered.hasNext())
        {
            wanted.add(filtered.next());
        }

        assertTrue(wanted.size() == 250);
    }
    
    @Test
    public void testSingleAndMultiWildcardAlternative()
    {
        String key = "industry";
        String value = "_o__rnment%io_";
 
        ArrayList<JSONObject> wanted = new ArrayList<JSONObject>();
        
        FileSource source = new FileSource("src/test/resources/generic/generic_data_sorted_id.json");
        Iterator<JSONObject> records = new JSONInput().read(source).iterator();
        
        LikeFilter filter = new LikeFilter(key, value);
        Iterator<JSONObject> filtered = filter.transform(records);
        
        while(filtered.hasNext())
        {
            wanted.add(filtered.next());
        }
        
        assertTrue(wanted.size() == 135);
    }
    
}
