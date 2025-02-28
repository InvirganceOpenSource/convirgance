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
 * A transformer that inserts or updates a key-value pair in a JSON object.
 * If the key already exists, its value will be replaced. If the key doesn't exist,
 * a new key-value pair will be added.
 * 
 * @author jbanes
 */
public class InsertKeyTransformer implements IdentityTransformer
{
    private String key;
    private Object value;

    
    /**
     * Prepares a new InsertKeyTransformer. The {@link #setKey(String)} and
     * {@link #setValue(Object)} methods must be called before attempting a
     * transform.
     */
    public InsertKeyTransformer()
    {
        
    }
    
    /**
     * Creates a new InsertKeyTransformer with the specified key-value pair.
     * 
     * <p>Example usage:</p>
     * <pre>
     * // Create transformer to add/update a "status" field
     * InsertKeyTransformer transformer = new InsertKeyTransformer("status", "active");
     * 
     * // Input:  {"id": 123, "name": "John"}
     * // Output: {"id": 123, "name": "John", "status": "active"}
     * 
     * // Input:  {"id": 456, "status": "inactive", "name": "Jane"}
     * // Output: {"id": 456, "status": "active", "name": "Jane"}
     * </pre>
     *
     * @param key The key to insert or update.
     * @param value The value to associate with the key. Supports the use of a {@link ValueGenerator}
     */
    public InsertKeyTransformer(String key, Object value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key that will be inserted or updated.
     * 
     * @return The key name.
     * @throws NullPointerException if the key has not been set.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the key that will be inserted or updated.
     * 
     * @param key The key name.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Gets the value that will be associated with the key.
     *
     * @return The value to be inserted or used for update.
     * @throws NullPointerException if the value has not been set.
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Sets the value that will be associated with the key. If the value is a
     * {@link ValueGenerator}, the result of the generator will be inserted
     * rather than the generator itself.
     * 
     * @param value The value to be inserted or used for update.
     */
    public void setValue(Object value)
    {
        this.value = value;
    }

    /**
     * Transforms a JSON object by inserting or updating the specified key-value pair.
     * This operation modifies the input object directly.
     * 
     * <p>If the key already exists in the input object, its value will be replaced.
     * If the key doesn't exist, a new key-value pair will be added to the object.</p>
     * 
     * <p>If the value is a {@link ValueGenerator}, the result of generation will
     * be set rather than the generator itself.</p>
     * 
     * @param record The JSON object to modify.
     * @return The modified JSON object (same instance as input).
     * @throws ConvirganceException if the key is null, or if there's an error
     *         during the modification operation.
     */
    @Override
    public JSONObject transform(JSONObject record) throws ConvirganceException
    {
        if(value instanceof ValueGenerator) record.put(key, ((ValueGenerator)value).generate(record));
        else record.put(key, value);
        
        return record;
    }
    
}
