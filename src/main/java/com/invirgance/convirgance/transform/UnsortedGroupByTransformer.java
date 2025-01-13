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

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONArray;
import com.invirgance.convirgance.json.JSONObject;
import java.util.*;

/**
 * Groups data together from an iterator based on the specified fields, the resulting data will be sorted. 
 * 
 * @author tadghh
 */
public class UnsortedGroupByTransformer implements Transformer 
{   
    private final String[] fields; 
    private final String output;   

    /**
     * Creates a new UnsortedGroupByTransformer to group related data on provided fields. 
     * @param fields The fields you want to group with.
     * @param output The new field to assign the grouped data on.
     * @throws ConvirganceException An exception will be raised when one of the following occurs:
     *  - One of the provided grouping fields is null or empty.
     *  - No fields were provided at all.
     *  - The output field to group data must not be null.
     */
    public UnsortedGroupByTransformer(String[] fields, String output)
    {
        if (fields == null || fields.length == 0) throw new ConvirganceException("Fields must not be null or empty.");      

        for (String key : fields)
        {
            if (key == null || key.isEmpty()) throw new ConvirganceException("Fields must not contain null or empty values.");
        }

        if (output == null || output.isEmpty()) throw new ConvirganceException("Output key must not be null or empty.");
        
        this.fields = fields;
        this.output = output;
    }
     
    /**
     * Groups unsorted JSONObjects based on the provided fields.
     * Ex Collecting atomized weather data for cities and grouping it together.
     * 
     * @param iterator The iterator of JSONObjects.
     * @return A new iterator with the grouped data.
     */
    @Override
    public Iterator<JSONObject> transform(Iterator<JSONObject> iterator)
    {
        String header;
        JSONArray group;
        JSONObject record;
        JSONObject collected; 
        
        Map<String, JSONArray> related = new HashMap();
        List<JSONObject> groups = new ArrayList<>();
        
        Set<String> fieldKeys = new HashSet<>(Arrays.asList(fields));     
        
        while (iterator.hasNext())
        {
            record = iterator.next();
            header = createGroupKey(record);
            group = related.get(header);
            
            // Create new group only when needed
            if (group == null)
            {
                collected = new JSONObject();
                group = new JSONArray();

                // Set group keys
                for (String key : fields)
                {
                    collected.put(key, record.get(key));
                }

                collected.put(output, group);
                related.put(header, group);
                groups.add(collected);
            }

            record.keySet().removeAll(fieldKeys);
            group.add(record);
        }

        return groups.iterator();
    }

    private String createGroupKey(JSONObject record)
    {
        Object[] values = new Object[fields.length];
        
        for (int i = 0; i < fields.length; i++)
        {
            values[i] = record.get(fields[i]);
        }
        
        return Arrays.toString(values);
    }
    
}
