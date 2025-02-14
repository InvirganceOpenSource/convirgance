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

import com.invirgance.convirgance.json.JSONObject;

/**
 * Filters out JSONObjects based on string containment comparison.
 * 
 * <p>
 * This class is useful for scenarios where large datasets are being processed, and we need to
 * filter records based on partial string matches. For example, finding all records where an
 * industry field contains "Services" as part of its value.
 * </p>
 *
 * <p>Example use case:</p>
 * <pre>
 *    String key = &quot;industry&quot;;
 *    String value = &quot;Services&quot;;
 *        
 *    ArrayList&lt;JSONObject&gt; wanted = new ArrayList&lt;JSONObject&gt;();
 *        
 *    FileSource source = new FileSource(&quot;generic_data_sorted_id.json&quot;);
 *    Iterator&lt;JSONObject&gt; records = new JSONInput().read(source).iterator();
 *        
 *    ContainsFilter filter = new ContainsFilter(key, value);
 *    Iterator&lt;JSONObject&gt; filtered = filter.transform(records);
 * </pre>
 * 
 * <p>
 * Important notes:
 * <ul>
 *   <li>The filter performs case-sensitive containment checks</li>
 *   <li>Null values in either the record or comparison value will result in the test returning false</li>
 *   <li>Both the record value and comparison value are converted to strings before comparison</li>
 * </ul>
 * 
 * @see ComparatorFilter
 * @see JSONObject
 * @author tadghh
 */
public class ContainsFilter extends ComparatorFilter
{
    /**
     * Creates a new ContainsFilter.
     */
    public ContainsFilter()
    {
        super();
    }

    /**
     * Creates a new ContainsFilter with the specified key and value to check for containment.
     * 
     * @param key The key to evaluate in the JSONObject.
     * @param value The value to check for containment.
     */
    public ContainsFilter(String key, String value)
    {
        super(key, value);
    }
    
    /**
     * Tests if the value of the specified key in the record contains the comparison value.
     * Both values are converted to strings for the containment check.
     * 
     * @param record The JSONObject to evaluate.
     * @return True if the string representation of the record's value contains the 
     *         string representation of the comparison value, false otherwise.
     */
    @Override
    public boolean test(JSONObject record)
    {
        String current = record.getString(getKey());
        String compare = getValue().toString();
        
        if(current == null) return false;

        return current.contains(compare);
    }
}