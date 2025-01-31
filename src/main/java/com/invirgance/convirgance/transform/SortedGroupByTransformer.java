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
 * Transforms pre-sorted JSON data by grouping records that share common field values.
 * This transformer expects input data to be sorted based on the grouping fields.
 * 
 * <p>For example, given weather data sorted by city and date, this transformer can
 * group all weather readings for each unique city-date combination under a single record.</p>
 * 
 * @author tadghh
 */
public class SortedGroupByTransformer implements Transformer 
{   
    private String[] fields;
    private String output;
    
    /**
     * Creates a new transformer that groups pre-sorted JSON objects based on common field values.
     * 
     * <p>Example usage:</p>
     * <pre>
     * // Group weather readings by city and date
     * String[] groupFields = {"city", "date"};
     * String outputField = "readings";
     * SortedGroupByTransformer transformer = new SortedGroupByTransformer(groupFields, outputField);
     * 
     * // Input records:
     * // {"city": "Seattle", "date": "2024-01-01", "temp": 72, "humidity": 65}
     * // {"city": "Seattle", "date": "2024-01-01", "temp": 75, "humidity": 62}
     * 
     * // Output record(s):
     * // {
     * //   "city": "Seattle",
     * //   "date": "2024-01-01",
     * //   "readings": [
     * //     {"temp": 72, "humidity": 65},
     * //     {"temp": 75, "humidity": 62}
     * //   ]
     * // }
     * </pre>
     * 
     * @param fields The fields to group by. All records sharing the same values for these fields
     *               will be grouped together.
     * @param output The field name under which the grouped records will be stored as an array.
     * @throws ConvirganceException if:
     *         <ul>
     *         <li>The fields array is null or empty</li>
     *         <li>If a field name in the array is null or empty</li>
     *         <li>The output field name is null or empty</li>
     *         </ul>
     */
    public SortedGroupByTransformer(String[] fields, String output)
    {
        // Test dependent exception messages.
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
     * Transforms an iterator of JSON objects by grouping records with matching field values.
     * 
     * <p>The transformer maintains the original sort order of the input data while
     * grouping records. For each unique combination of the grouping field values,
     * it creates a new JSON object containing:</p>
     * <ul>
     * <li>The common field values from the grouped records</li>
     * <li>An array of the grouped records (excluding the common fields) under the specified output field</li>
     * </ul>
     * 
     * <p>Important: This transformer assumes the input iterator provides records that are
     * already sorted by the grouping fields. Providing unsorted data may result in
     * incorrect grouping.</p>
     *
     * @param iterator An iterator of pre-sorted JSONObjects to be grouped.
     * @return A new iterator that provides the grouped JSON objects.
     * @throws ConvirganceException if attempting to get the next element when none exists.
     */
    @Override
    public Iterator<JSONObject> transform(Iterator<JSONObject> iterator) {
        return new Iterator<JSONObject>() {
            private JSONObject current = null;          
            private JSONObject group;
            private JSONArray children;
            
            {
                if (iterator.hasNext()) current = iterator.next();
            }
            
            @Override
            public boolean hasNext() 
            {
                return current != null;
            }
            
            @Override
            public JSONObject next() 
            {
                // Initial: Create new group object and array for this group's records
                group = new JSONObject();
                children = new JSONArray();
                
                if (!hasNext())
                {
                    throw new ConvirganceException("Attempted to iterate with no next element.");
                }
        
                // Set the group keys from current parent record
                for (String key : fields) 
                {
                    group.put(key, current.get(key));
                }
                
                // Children: Process all records for this group
                while (current != null && keysMatch(group)) 
                {
                    children.add(addFilteredRecordToGroup(current));

                    current = null;
                    
                    if(iterator.hasNext()) current = iterator.next();                 
                }
                
                group.put(output, children);
                return group;
            }
            
            private JSONObject addFilteredRecordToGroup(JSONObject record)
            {
                for (String key : fields)
                {
                    record.remove(key);
                }

                return record;
            }
                     
            private boolean keysMatch(JSONObject groupKeys)
            {
                for (String key : groupKeys.keySet())
                {
                    if (!Objects.equals(current.get(key), groupKeys.get(key))) return false;
                }
                
                return true;
            }
        };
    }
}
