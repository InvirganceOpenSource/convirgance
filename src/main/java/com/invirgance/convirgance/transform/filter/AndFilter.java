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
import java.util.Arrays;
import java.util.List;

/**
 * Filters JSONObjects based on multiple criteria, requiring all conditions to be met.
 * Combines multiple filters, returning true only if every filter evaluates the 
 * given record as true.
 * @author jbanes
 */
@Wiring
public class AndFilter implements Filter
{
    private List<Filter> filters;

    /**
     * Creates an empty AndFilter.
     */
    public AndFilter()
    {
    }

    /**
     * Creates a AndFilter with the list of filters that need to pass during
     * evaluation.
     *
     * @param filters List of filters to use.
     */
    public AndFilter(List<Filter> filters)
    {
        this.filters = filters;
    }
    
    /**
     * Creates a AndFilter with the list of filters that need to pass during
     * evaluation.
     *
     * @param filters List of filters to use.
     */
    public AndFilter(Filter... filters)
    {
        this.filters = Arrays.asList(filters);
    }

    /**
     * Returns the current filters that form the criteria.
     * @return The filters.
     */
    public List<Filter> getFilters()
    {
        return filters;
    }

    /**
     * Sets the filters used to form the criteria.
     * @param filters The filters.
     */
    public void setFilters(List<Filter> filters)
    {
        this.filters = filters;
    }

    /**
     * Evaluates the given record against all the filters.
     *
     * @param record The record to evaluate.
     * @return True if the record meets all the criteria defined by the filters,
     * false if it fails any of them.
     */
    @Override
    public boolean test(JSONObject record)
    {
        for(Filter filter : filters)
        {
            if(!filter.test(record)) return false;
        }
        
        return true;
    }
    
}
