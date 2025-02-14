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
import java.util.Stack;

/**
 * Filter out JSONObjects by using a SQL-esque Like operator.
 * 
 * <p>
 * This class is useful for scenarios where large datasets are being processed, and we need to
 * filter records based on like patterns. For example, finding all records where an
 * industry field contains "Government" as part of its pattern. 
 * </p>
 *
 * <p>Example use case:</p>
 * <pre>
 *    String key = "industry";
 *    String pattern = "&#95;o&#95;&#95;rnment&#37;io&#95;";
 *   
 *    ArrayList&lt;JSONObject&gt; wanted = new ArrayList&lt;JSONObject&gt;();
 *   
 *    FileSource source = new FileSource("src/test/resources/generic/generic_data_sorted_id.json");
 *    Iterator&lt;JSONObject&gt; records = new JSONInput().read(source).iterator();
 *    LikeFilter filter = new LikeFilter(key, pattern);
 *    Iterator&lt;JSONObject&gt; filtered = filter.transform(records);
 * </pre>
 * 
 * @author tadghh
 */
public class LikeFilter implements Filter
{
    private String key;
    private String pattern;
    private char wildcard = '%';
    
    /**
     * Creates a new LikeFilter.
     */
    public LikeFilter()
    {
    }

    /**
     * Creates a new LikeFilter with the specified key and pattern to pattern match with.
     * 
     * @param key The key to evaluate in the JSONObject.
     * @param pattern The pattern to match key values with.
     */
    public LikeFilter(String key, String pattern)
    {
        this.key = key;
        this.pattern = pattern;
    }
    
    /**
     * Gets the comparison key in use.
     * @return The key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Set the comparison key.
     * @param key The key.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Sets the value that will be used as a pattern to match with.
     * You can use an alternative wildcard if needed. 
     * 
     * Supports wildcards '%' (can be overridden) and '_'
     * @param value The pattern to match values with.
     */
    public void setPattern(String value)
    {
        this.pattern = value;
    }
    
    /**
     * Retrieves the pattern used for comparison.
     * 
     * @return The pattern pattern.
     */
    public String getPattern()
    {
        return pattern;
    }

    /**
     * Sets the character that will be the 'wildcard' in the pattern.
     * 
     * @param value Any character
     */
    public void setWildcard(char value)
    {
        this.wildcard = value;
    }
    
    /**
     * Gets the current character used as the pattern wildcard.
     * 
     * @return The wildcard character
     */
    public char getWildcard()
    {
        return wildcard;
    }
    
    @Override
    public boolean test(JSONObject record)
    {
        String recordValue = record.getString(key);
        
        if(recordValue == null) return false;

        return matchPattern(recordValue, pattern);
    }
    
    private boolean matchPattern(String word, String pattern) 
    {
        char currentPattern;
        int wordPosition;
        int patternPosition;
        
        Stack<int[]> positions = new Stack<>();
        
        int[] position = new int[]{0, 0};
        positions.push(position);

        while(!positions.empty()) 
        {
            position = positions.pop();
            wordPosition = position[0];
            patternPosition = position[1];

            if(patternPosition == pattern.length()) 
            {
                if(wordPosition == word.length()) return true;
                
                continue;
            }

            if(wordPosition == word.length()) 
            {
                if(pattern.charAt(patternPosition) == this.wildcard) 
                {
                    position[0] = wordPosition;
                    position[1] = patternPosition + 1;
                    positions.push(position.clone());
                }
                
                continue;
            }

            currentPattern = pattern.charAt(patternPosition);
            
            if(currentPattern == this.wildcard) 
            {
                position[0] = wordPosition;
                position[1] = patternPosition + 1;
                positions.push(position.clone());

                position[0] = wordPosition + 1;
                position[1] = patternPosition;
                positions.push(position.clone());
            } 
            else if(currentPattern == '_' || currentPattern == word.charAt(wordPosition)) 
            {
                position[0] = wordPosition + 1;
                position[1] = patternPosition + 1;
                positions.push(position.clone());
            }
        }
        
        return false;
    }
}
