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

/**
 * Filters data by comparing a specified key-value pair using a flexible coercive comparator.
 * 
 * @author jbanes
 */
public abstract class ComparatorFilter implements Filter
{
    private CoerciveComparator comparator = new CoerciveComparator();
    
    private String key;
    private Object value;

    /**
     * Creates a new ComparatorFilter.
     */
    public ComparatorFilter()
    {
    }

    /**
     * Creates a ComparatorFilter to use with a provided key and value.
     * @param key The key to compare.
     * @param value The key's value.
     */
    public ComparatorFilter(String key, Object value)
    {
        this.key = key;
        this.value = value;
    }
    
    /**
     * Returns the {@link CoerciveComparator} used for filtering. 
     * @return The CoerciveComparator.
     */
    protected CoerciveComparator getComparator()
    {
        return comparator;
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
     * Gets the expected value for the key.
     * @return The value.
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Sets the value to use for comparison on the provided key.
     * @param value The comparison value.
     */
    public void setValue(Object value)
    {
        this.value = value;
    }
}
