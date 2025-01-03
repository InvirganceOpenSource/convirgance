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
import com.invirgance.convirgance.json.JSONObject;

/**
 * Modifies the JSONObject with the provided key and value. 
 * Replacing the keys value if present.
 * @author jbanes
 */
public class InsertKeyTransformer implements IdentityTransformer
{
    private String key;
    private Object value;

    
    /**
     * Creates a new Transformer.
     */
    public InsertKeyTransformer()
    {
    }

    /**
     * Creates a new InsertKeyTransformer to modify JSONObjects, the provided key will either be modified or inserted.
     * @param key The key to insert/modify.
     * @param value The value to insert or replace.
     */
    public InsertKeyTransformer(String key, Object value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key that will either be inserted or modified.
     * @return The name of the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the name of the key to be inserted or modified.
     * @param key The name of the key.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Gets the value to be used with the new or modified key.
     *
     * @return The value to be inserted.
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Sets the value to be used for the key.
     * @param value The key's value.
     */
    public void setValue(Object value)
    {
        this.value = value;
    }

    /**
     * Transforms the JSONObject either replacing or inserting the key.
     * 
     * @param record The JSONObject to modify.
     * @return The modified record.
     * @throws ConvirganceException If an issue occurs when inserting/modifying the key. 
     */
    @Override
    public JSONObject transform(JSONObject record) throws ConvirganceException
    {
        record.put(key, value);
        
        return record;
    }
    
}
