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
    private Set<String> included;
    private Set<String> excluded;
    private String groupByKey;
    
    /**
     * Creates a new GroupByTransformer to group related data with. 
     * @param key The key we want to group related data on.
     */
    public SortedGroupByTransformer(String key)
    {
        this(key, null,null);
    }

    /**
     * Creates a new GroupByTransformer with specified fields to group or exclude.
     *
     * @param key The key to group on.
     * @param included Only group these values.
     * @param excluded Exclude these keys all together.
     */
    public SortedGroupByTransformer( String key,String[] included,String[] excluded)
    {
        this.groupByKey = key;
        this.included = included != null ? new HashSet<>(Arrays.asList(included)) : null;
        this.excluded = excluded != null ? new HashSet<>(Arrays.asList(excluded)) : null;
    }

    /**
     * The array of field names to include when grouping. 
     * If null all are included.
     * 
     * @return The included field names.
     * @throws NullPointerException If included has not been initialized.
     */
    public String[] getIncluded()
    {
        return included.toArray(String[]::new);
    }

    /**
     * The array of field names to exclude when grouping.
     * @return The excluded field names.
     * @throws NullPointerException If excluded has not been initialized.
     */
    public String[] getExcluded()
    {
        return excluded.toArray(String[]::new);
    }
    
    /**
     * Set field names to include when grouping.
     * @param included Array of field/header names to include.
     */
    public void setIncluded(String[] included)
    {
        if(included == null) this.included = null;
        else this.included = new HashSet<>(Arrays.asList(included));
    }
    
    /**
     * Set field names to exclude when grouping values.
     * @param excluded Array of fields/headers to exclude.
     */
    public void setExcluded(String[] excluded)
    {
        if(excluded == null) this.excluded = null;
        else this.excluded = new HashSet<>(Arrays.asList(excluded));
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
            record = iterator.next();
            groupKey = record.getString(groupByKey);
            group = groupMap.get(groupKey);
            
            if (group == null)
            {
                group = new JSONObject();
                group.put(groupByKey, groupKey);
                results.add(group);
                groupMap.put(groupKey, group);
            }

            for (String field : record.keySet())
            {
                if (field.equals(groupByKey)) continue;
                if (excluded != null && excluded.contains(field)) continue;
                if (included != null && !included.contains(field)) continue;
                
                if (!group.containsKey(field))
                {
                    fieldArray = new JSONArray();
                    group.put(field, fieldArray);
                }
                else
                {
                    fieldArray = group.getJSONArray(field);
                }
                fieldArray.add(record.get(field));
            }
        }

        return results.iterator();
    }
}
