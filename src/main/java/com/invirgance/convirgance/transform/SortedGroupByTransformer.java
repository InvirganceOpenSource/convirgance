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
package com.invirgance.convirgance.transform;

import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.util.*;

/**
 * Groups data together from an iterator based on a specified key.
 * 
 * @author tadghh
 */
public class SortedGroupByTransformer implements Transformer 
{   
    private String[] groupByKey;
    private String outputKey;
    
    /**
     * Creates a new GroupByTransformer to group related data on provided fields. 
     * @param keys The fields we want to group related data on.
     */
    public SortedGroupByTransformer(String[] keys, String output)
    {
        this.groupByKey = keys;
        this.outputKey = output;
    }
    
   
    /**
     * Groups JSONObjects based on a specific matching key value.
     * Ex Collecting atomized weather data for cities and grouping it together.
     * 
     * @param iterator The iterator of JSONObjects
     * @return A new iterator with the grouped data.
     */
    @Override
    public Iterator<JSONObject> transform(Iterator<JSONObject> iterator)
    {
        JSONArray results = new JSONArray<>();
        Map<String, JSONObject> groupMap = new HashMap<>(); 

        JSONObject record;
        String groupKey;
        JSONObject group;
        JSONArray fieldArray;
        
        while (iterator.hasNext())
        {
           
        }

        return results.iterator();
    }
}
