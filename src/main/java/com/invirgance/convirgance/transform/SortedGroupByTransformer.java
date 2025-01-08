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
 * Groups data together from an iterator based on a specified keys.
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
     * @param output The field to output the grouped data to.
     */
    public SortedGroupByTransformer(String[] keys, String output)
    {
        this.groupByKeys = keys;
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
            private final Map<String, Object> currentGroupKeys = new HashMap<>();
            private final Iterator<JSONObject> iterator = sourceIterator;

            
            // Advance to first valid record
            {
                advanceToNextValidRecord();
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
                JSONArray groupRecords = new JSONArray();
                 
                // Set the group keys from current parent record
                for (String key : groupByKeys) 
                {
                    group.put(key, currentRecord.get(key));
                    currentGroupKeys.put(key, currentRecord.get(key));
                }
                
                // Children: Process all records for this group
                while (currentRecord != null && keysMatch(currentRecord, currentGroupKeys)) 
                {
                    addFilteredRecordToGroup(groupRecords, currentRecord);
                    advanceToNextValidRecord();
                }
                
                group.put(outputKey, groupRecords);
                return group;
            }
            
            private void addFilteredRecordToGroup(JSONArray groupRecords, JSONObject record)
            {
                for (String key : groupByKeys)
                {
                    record.remove(key);
                }

                groupRecords.add(record);
            }
            
            private void advanceToNextValidRecord() 
            {
                currentRecord = null;
                while (iterator.hasNext()) 
                {
                    JSONObject next = iterator.next();
                    
                    // Skip objects not containing the relavent fields
                    if (containsRequiredKeys(next)) 
                    {
                        currentRecord = next;
                        break;
                    }
                }
            }
            
            private boolean containsRequiredKeys(JSONObject obj) 
            {
                for (String key : groupByKeys) 
                {
                    if (!obj.containsKey(key)) return false;
                }
                
                return true;
            }
            
            private boolean keysMatch(JSONObject record, Map<String, Object> groupKeys)
            {
                for (Object field : groupKeys.values())
                {
                    if (!record.containsValue(field)) return false;
                }
                
                return true;
            }
        };
    }
}
