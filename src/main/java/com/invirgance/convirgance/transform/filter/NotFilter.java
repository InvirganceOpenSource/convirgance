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
package com.invirgance.convirgance.transform.filter;

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.wiring.annotation.Wiring;

/**
 * Filters JSONObjects that do not meet the condition of an existing filter.
 * @author jbanes
 */
@Wiring
public class NotFilter implements Filter
{
    private Filter filter;

    /**
     * Creates a NotFilter without an initial filter.
     */
    public NotFilter()
    {
    }

    /**
     * Creates a new NotFilter with the provided filter to invert.
     * @param filter The filter to invert.
     */
    public NotFilter(Filter filter)
    {
        this.filter = filter;
    }

    /**
     * Gets the current filter that will be inverted during evaluation.
     * @return The current filter.
     */
    public Filter getFilter()
    {
        return filter;
    }

    /**
     * Set the filter to invert during comparison.
     * @param filter The filter.
     */
    public void setFilter(Filter filter)
    {
        this.filter = filter;
    }
    
    /**
     * Evaluates the record against the filter and returns the opposite result.
     * @param record The record to evaluate.
     * @return The negated result of the filter evaluation.
     */
    @Override
    public boolean test(JSONObject record)
    {
        return !this.filter.test(record);
    }
    
}
