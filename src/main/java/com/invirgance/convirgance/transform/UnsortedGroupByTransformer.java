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
 * Transforms unsorted JSON data by grouping records that share common field values.
 * Unlike {@link SortedGroupByTransformer}, this transformer can handle input data in any order.
 * 
 * <p>For example, given weather data with records in any order, this transformer can
 * group all weather readings for each unique city-weather combination under a single record.</p>
 * 
 * @author tadghh
 */
public class UnsortedGroupByTransformer implements Transformer 
{   
    private String[] fields; 
    private Set<String> fieldKeys;    
    private final String output;   
     
    /**
     * Creates a new transformer that groups JSON objects based on common field values,
     * regardless of the records order in the iterator.
     * 
     * <p>Example usage:</p>
     * <pre>
     * // Group weather readings by city and weather condition
     * String[] groupFields = {"city", "weather"};
     * String outputField = "readings";
     * UnsortedGroupByTransformer transformer = new UnsortedGroupByTransformer(groupFields, outputField);
     * 
     * // Input records (in any order):
     * // {"city": "Tampa", "weather": "sunny", "temp": 85, "humidity": 70}
     * // {"city": "Miami", "weather": "rainy", "temp": 78, "humidity": 85}
     * // {"city": "Tampa", "weather": "sunny", "temp": 87, "humidity": 68}
     * 
     * // Output records:
     * // {
     * //   "city": "Tampa",
     * //   "weather": "sunny",
     * //   "readings": [
     * //     {"temp": 85, "humidity": 70},
     * //     {"temp": 87, "humidity": 68}
     * //   ]
     * // },
     * // {
     * //   "city": "Miami",
     * //   "weather": "rainy",
     * //   "readings": [
     * //     {"temp": 78, "humidity": 85}
     * //   ]
     * // }
     * </pre>
     * 
     * @param fields The fields to group by. All records sharing the same values for these fields
     *               will be grouped together.
     * @param output The field name under which the grouped records will be stored as an array.
     * @throws ConvirganceException if:
     *         <ul>
     *         <li>The output field name is null or empty</li>
     *         </ul>
     */
    public UnsortedGroupByTransformer(String[] fields, String output)
    {
        if (output == null || output.isEmpty()) throw new ConvirganceException("Output key must not be null or empty.");
        
        this.output = output;
        this.setFields(fields);
    }
    
    /**
     * Sets the fields to evaluate with when grouping records.
     *
     * @param fields The fields.
     * @throws ConvirganceException if:
     * <ul>
     * <li>The fields array is null or empty</li>
     * <li>If a field name in the array is null or empty</li>
     * </ul>
     */
    public final void setFields(String[] fields)
    {
        if (fields == null || fields.length == 0) throw new ConvirganceException("Fields must not be null or empty.");      

        for (String key : fields)
        {
            if (key == null || key.isEmpty()) throw new ConvirganceException("Fields must not contain null or empty values.");
        }

        this.fields = fields;
        this.fieldKeys = new HashSet<>(Arrays.asList(fields));
    }
    
    /**
     * Returns the current fields being used to evaluate grouping with.
     * @return The fields.
     */
    public String[] getFields()
    {
        return fields;
    }      
    
    /**
     * Transforms an iterator of JSON objects by grouping records with matching field values.
     * 
     * <p>The transformer processes records in memory to group them by the specified fields.
     * For each unique combination of the grouping field values, it creates a new JSON object containing:</p>
     * <ul>
     * <li>The common field values from the grouped records</li>
     * <li>An array of the grouped records (excluding the common fields) under the specified output field</li>
     * </ul>
     * 
     * <p>Note: This transformer maintains all records in memory until iteration is complete,
     * which may impact performance with very large datasets. For pre-sorted data,
     * consider using {@link SortedGroupByTransformer} instead.</p>
     *
     * @param iterator An iterator of JSONObjects to be grouped.
     * @return A new iterator that provides the grouped JSON objects.
     */
    @Override
    public Iterator<JSONObject> transform(Iterator<JSONObject> iterator)
    {
        String header;
        JSONArray group;
        JSONObject record;
        JSONObject collected; 
        JSONObject clone;
        
        Map<String, JSONArray> related = new HashMap();
        JSONArray groups = new JSONArray();
              
        while (iterator.hasNext())
        {
            record = iterator.next();
            header = createGroupKey(record);
            group = related.get(header);
            
            if (group == null)
            {
                collected = new JSONObject();
                group = new JSONArray();

                for (String key : fields)
                {
                    collected.put(key, record.get(key));
                }

                collected.put(output, group);
                related.put(header, group);
                groups.add(collected);
            }

            clone = new JSONObject(record);
            clone.keySet().removeAll(fieldKeys);
            
            group.add(clone);
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
