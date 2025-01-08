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
 * Use this transformer when grouping on common fields with ALREADY sorted data.
 * 
 * @author tadghh
 */
public class SortedGroupByTransformer implements Transformer 
{   
    private String[] groupByKeys;
    private String outputKey;
    
    /**
     * Creates a new SortedGroupByTransformer to group related data on provided fields. 
     * @param fields The fields we want to group related data on.
     * @param output The field to output the grouped data to.
     * @throws ConvirganceException An error will be thrown when one of the following occurs:
     *  - One of the provided grouping fields is null or empty.
     *  - No fields were provided at all.
     *  - The output field to group data must not be null.
     */
    public SortedGroupByTransformer(String[] fields, String output)
    {
        if (fields == null || fields.length == 0) throw new ConvirganceException("Fields must not be null or empty.");      

        for (String key : fields)
        {
            if (key == null || key.isEmpty()) throw new ConvirganceException("Fields must not contain null or empty values.");
        }

        if (output == null || output.isEmpty()) throw new ConvirganceException("Output key must not be null or empty.");
        
        this.groupByKeys = fields;
        this.outputKey = output;
    }
    
    /**
     * Groups JSONObjects based on a specific matching key value.
     * Ex Collecting atomized weather data for cities and grouping it together.
     * 
     * @param sourceIterator The iterator of JSONObjects
     * @throws NoSuchElementException There was no next element.
     * @return A new iterator with the grouped data.
     */
    @Override
    public Iterator<JSONObject> transform(Iterator<JSONObject> sourceIterator) {
        return new Iterator<JSONObject>() {
            private JSONObject currentRecord = null;
            private final Iterator<JSONObject> iterator = sourceIterator;
        
            {
                if (iterator.hasNext()) currentRecord = iterator.next();
            }
            
            @Override
            public boolean hasNext() 
            {
                return currentRecord != null;
            }
            
            @Override
            public JSONObject next() 
            {
                if (!hasNext()) 
                {
                    throw new NoSuchElementException();
                }
                
                // Initial: Create new group object and array for this group's records
                JSONObject group = new JSONObject();
                JSONArray children = new JSONArray();
                 
                // Set the group keys from current parent record
                for (String key : groupByKeys) 
                {
                    group.put(key, currentRecord.get(key));
                }
                
                // Children: Process all records for this group
                while (currentRecord != null && keysMatch(group)) 
                {
                    children.add(addFilteredRecordToGroup(currentRecord));

                    currentRecord = null;
                    
                    if(iterator.hasNext()) currentRecord = iterator.next();                 
                }
                
                group.put(outputKey, children);
                return group;
            }
            
            private JSONObject addFilteredRecordToGroup(JSONObject record)
            {
                for (String key : groupByKeys)
                {
                    record.remove(key);
                }

               return record;
            }
            
            
            private boolean keysMatch(JSONObject groupKeys)
            {
                for (String key : groupKeys.keySet())
                {
                    if (!Objects.equals(currentRecord.get(key), groupKeys.get(key))) return false;
                }
                
                return true;
            }
        };
    }
}
