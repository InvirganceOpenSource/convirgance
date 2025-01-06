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
    private String[] groupByKeys;
    private String outputKey;
    
    /**
     * Creates a new GroupByTransformer to group related data on provided fields. 
     * @param keys The fields we want to group related data on.
     */
    public SortedGroupByTransformer(String[] keys, String output)
    {
        this.groupByKeys = keys;
        this.outputKey = output;
    }
    
    private boolean containsKeys(JSONObject object, String[] keys){
        for(String key : keys){
            if(!object.containsKey(key)) return false;
        }
        return true;
    }
    
    private boolean keysMatch(JSONObject record, Map<String, Object> currentKeys)
    {
        return Arrays.stream(this.groupByKeys)
                .allMatch(key -> record.get(key).equals(currentKeys.get(key)));
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
        JSONArray results = new JSONArray();
        Map<String, Object> currentKeys = new HashMap<>();
        JSONObject group = new JSONObject();
        JSONArray fieldArray = new JSONArray();
        JSONObject record;

        while (iterator.hasNext())
        {
            record = iterator.next();
            
            // Record doesn't contain the required fields.
            if (!containsKeys(record, this.groupByKeys))
            {
                continue;
            }

            if (currentKeys.isEmpty())
            {
                for (String key : this.groupByKeys)
                {
                    currentKeys.put(key, record.get(key));
                    group.put(key, record.get(key));
                }
                group.put(this.outputKey, fieldArray);
            }

            // Data should be sorted... New object time
            if (!keysMatch(record, currentKeys))
            {
                results.add(group);
 
                group = new JSONObject();
                fieldArray = new JSONArray();
                currentKeys.clear();

                for (String key : this.groupByKeys)
                {
                    currentKeys.put(key, record.get(key));
                    group.put(key, record.get(key));
                }
                group.put(this.outputKey, fieldArray);
            }

            JSONObject nestedObj = new JSONObject();
            for (String key : record.keySet())
            {
                if (!Arrays.asList(this.groupByKeys).contains(key))
                {
                    nestedObj.put(key, record.get(key));
                }
            }
            fieldArray.add(nestedObj);
        }

        if (!group.isEmpty()) results.add(group);
        

        return results.iterator();
    }
}
