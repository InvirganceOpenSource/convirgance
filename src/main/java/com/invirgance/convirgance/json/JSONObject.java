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
package com.invirgance.convirgance.json;

import com.invirgance.convirgance.ConvirganceException;
import java.io.IOException;
import java.util.*;

/**
 * A JSON object implementation that represents a collection of key-value pairs.
 * Provides methods for parsing, manipulating, and serializing JSON data while
 * maintaining optional key ordering.
 * 
 * @author jbanes
 */
public class JSONObject implements Map<String, Object>
{
    private final HashMap<String, Object> map;
    
    private boolean ordered = false;
    private OrderedKeys<String> orderedKeys;

    /**
     * Creates a new JSONObject with ordering set to false.
     */
    public JSONObject()
    {
        this(false);
    }

    /**
     * Creates a JSONObject and sets its ordering.
     * @param ordered The ordering state to set.
     */
    public JSONObject(boolean ordered)
    {
        map = new HashMap<>();
        
        if(ordered) setOrdered(ordered);
    }

    /**
     * Creates a JSONObject by parsing the provided JSON string.
     * Initializes the internal map, ordering state, and ordered keys collection
     * from the parsed content.
     * @param json The JSON string to parse.
     * @throws ConvirganceException if an error occurs during JSON parsing.
     */
    public JSONObject(String json)
    {
        JSONObject object;
        
        try
        {
            object = new JSONParser(json).parseObject();
            
            this.map = object.map;
            this.ordered = object.ordered;
            this.orderedKeys = object.orderedKeys;
        }
        catch(IOException e) { throw new ConvirganceException(e); }
    }
    
    /**
      * Creates a JSONObject based on the provided map parameter. 
      * If the map is a JSONObject and is ordered, a shallow copy of the ordered keys is made.
      * Additionally, this object's internal map is populated with the contents of the provided map.
      * 
      * @param map A map of key-value pairs. If the map is an instance of JSONObject and is ordered, 
      *            this JSONObject will inherit the ordering and its ordered keys.
     */
    public JSONObject(Map<String, Object> map)
    {
        this();
        
        this.map.putAll(map);
        
        if(map instanceof JSONObject)
        {
            if(((JSONObject)map).isOrdered())
            {
                this.ordered = true;
                this.orderedKeys = new OrderedKeys<>(((JSONObject)map).orderedKeys);
            }
        }
    }

    /**
     * Returns if the object is ordered or unordered.
     * @return True (ordered) or false (unordered).
     */
    public boolean isOrdered()
    {
        return ordered;
    }

    /**
    * Controls whether this object's map maintains key order.
    * If switching to ordered mode, creates and populates a new OrderedKeys collection 
    * with the current map's keys. If disabling order, clears the OrderedKeys collection.
    * @param ordered true to enable key ordering, false to disable it.
    */
    public void setOrdered(boolean ordered)
    {
        if(ordered && !this.ordered) 
        {
            orderedKeys = new OrderedKeys<>();
            
            for(String key : this.map.keySet()) orderedKeys.add(key);
        }
        else if(!ordered)
        {
            orderedKeys = null;
        }
        
        this.ordered = ordered;
    }
    
    @Override
    public int size()
    {
        return this.map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.map.isEmpty();
    }
    
    /**
     * Checks if a key's value is null, returns true if key is not present.
     * @param key The key.
     * @return If the key or the key's value is null.
     */
    public boolean isNull(String key)
    {
        return (this.map.get(key) == null);
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.map.containsValue(value);
    }

    @Override
    public Object get(Object key)
    {
        return this.map.get(key);
    }
    
    /**
     * Attempts to coerce the key's value to a Boolean. 
     * Returns true if the value is already a Boolean.
     * Otherwise if the value is a String, Boolean.parseBoolean() is used (case-insensitive)
     * @param key A key.
     * @return The value parsed to a Boolean.
     * @throws ConvirganceException When the key doesn't exist or key's value is not of type Boolean, String or is null.
     */
    public boolean getBoolean(String key) throws ConvirganceException
    {
        Object value = this.map.get(key);
        
        if(value == null) throw new ConvirganceException(key + " is null and therefore can't be converted to a boolean");
        if(value instanceof Boolean) return ((Boolean)value);
        if(value instanceof String) return Boolean.parseBoolean(value.toString());
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to a boolean");
    }
    
    /**
     * Gets the Boolean equivalent of value for the given key with a given default alternative.
     * Returns true if the value is already a Boolean.
     * Otherwise if the value is a String, Boolean.parseBoolean() is used (case-insensitive)
     * If the value is null or the given key doesn't exist the default value is returned.
     * @param key
     * @param defaultValue A Boolean to return if the keys value is null.
     * @return The key's value parsed with Boolean.parseBoolean().
     * Otherwise if the key's value is null defaultValue will be returned.
     * @throws ConvirganceException When the key's value is not of type null, Boolean or String.
     */
    public boolean getBoolean(String key, boolean defaultValue) throws ConvirganceException
    {
        Object value = this.map.get(key);
       
        if(value == null) return defaultValue;
        if(value instanceof Boolean) return ((Boolean)value);
        if(value instanceof String) return Boolean.parseBoolean(value.toString());
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to a boolean");
    }
    
    /**
     * Gets the value associated with the specified key as a Double.
     * If the key's value is a Double its returned.
     * If the key's value is a String we pass the toString() of value to Double.parseDouble() 
     * @param key The key whose associated value is to be retrieved.
     * @return The key's value or a String coerced into a Double.
     * @throws ConvirganceException When the key or key's value is null, or its type cannot be coerced to a Double.
     */
    public double getDouble(String key) throws ConvirganceException
    {
        Object value = this.map.get(key);
        
        if(value == null) throw new ConvirganceException(key + " is null and therefore can't be converted to a double");
        if(value instanceof Double) return ((Double)value);
        if(value instanceof String) return Double.parseDouble(value.toString());
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to a double");
    }
    
    /**
     * Gets the value associated with the specified key as a Double returning the 
     * provided default if the value or key is null.
     * If the key's value is a Double its returned.
     * If the key's value is a String we pass the toString() of value to Double.parseDouble() 
     * Otherwise if the key or key's value was null, defaultValue is returned.
     * @param key The key whose associated value is to be retrieved.
     * @param defaultValue The default Double value to return if the key's value is null.
     * @return The key's value parsed to Double, or defaultValue if value or key is null.
     * @throws ConvirganceException When the key's value cannot be converted to a Double.
     */
    public double getDouble(String key, double defaultValue) throws ConvirganceException
    {
        Object value = this.map.get(key);
        
        if(value == null) return defaultValue;
        if(value instanceof Double) return ((Double)value);
        if(value instanceof String) return Double.parseDouble(value.toString());
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to a double");
    }
    
    /**
     * Gets the value associated with the specified key as a Int. 
     * Returns value if its already an Int.
     * Otherwise we use Integer.parseInt() and the toString() of value.
     * @param key The key whose associated value is to be retrieved.
     * @return The key's value parsed to Int.
     * @throws ConvirganceException When the key's value cannot be converted to a Int. Or the key itself is null.
     */
    public int getInt(String key) throws ConvirganceException
    {
        Object value = this.map.get(key);
        
        if(value == null) throw new ConvirganceException(key + " is null and therefore can't be converted to an int");
        if(value instanceof Integer) return ((Integer)value);
        if(value instanceof String) return Integer.parseInt(value.toString());
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to an int");
    }
    
    /**
     * Gets the value associated with the specified key as a Int returning the 
     * provided default if the value is null.
     * If value is null or the key doesn't exist defaultValue is returned.
     * Otherwise if value is of type Int, it will be returned unchanged.
     * @param key The key whose associated value is to be retrieved.
     * @param defaultValue The default Int value to return if the key's value is null.
     * @return The key's value parsed to Int, or defaultValue when value is null.
     * @throws ConvirganceException When the key's value cannot be converted to a Int.
     */
    public int getInt(String key, int defaultValue) throws ConvirganceException
    {
        Object value = this.map.get(key);
        
        if(value == null) return defaultValue;
        if(value instanceof Integer) return ((Integer)value);
        if(value instanceof String) return Integer.parseInt(value.toString());
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to an int");
    }
    
    
    /**
     * Gets a key's value returning a JSONArray, returning null on a null key value.
     * @param key A key whose value is JSON.
     * @return Value converted to JSONArray or null.
     * @throws ConvirganceException When the value is not of type JSONArray.
     */
    public JSONArray getJSONArray(String key) throws ConvirganceException
    {
        Object value = this.map.get(key);
        
        if(value == null) return null;
        if(value instanceof JSONArray) return ((JSONArray)value);
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to a JSONArray");
    }
    
    /**
     * Gets a key's value returning a JSONArray, returning null on a null key value.
     * @param key A key whose value is JSON.
     * @param defaultValue A JSONArray to return when value is null.
     * @return Value converted to JSONArray or null.
     * @throws ConvirganceException When the value is not of type JSONArray or is not null.
     */
    public JSONArray getJSONArray(String key, JSONArray defaultValue) throws ConvirganceException
    {
        Object value = this.map.get(key);
        
        if(value == null) return defaultValue;
        if(value instanceof JSONArray) return ((JSONArray)value);
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to a JSONArray");
    }
    
    /**
     * Gets the value for a key. Returning null or the value as a JSONObject.
     * @param key The key.
     * @return The key's value as a JSONObject or null.
     * @throws ConvirganceException When the key's value is not of type JSONObject.
     */
    public JSONObject getJSONObject(String key) throws ConvirganceException
    {
        Object value = this.map.get(key);
        
        if(value == null) return null;
        if(value instanceof JSONObject) return ((JSONObject)value);
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to a JSONObject");
    }
    
    /**
     * Gets the value for a key. 
     * Returning the provided default if the key's value is null. 
     * Otherwise the key's value as a JSONObject.
     * @param key The key.
     * @param defaultValue The default to use when the key's value is null.
     * @return The key's value as a JSONObject, or the provided default.
     * @throws ConvirganceException When the key's value is not of type JSONObject and is not null.
     */
    public JSONObject getJSONObject(String key, JSONObject defaultValue) throws ConvirganceException
    {
        Object value = this.map.get(key);
        
        if(value == null) return defaultValue;
        if(value instanceof JSONObject) return ((JSONObject)value);
        
        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for " + key + " cannot be converted to a JSONObject");
    }
    
    /**
     * Gets the keys value and returns its string representation.
     * Returns null if the key's value is null.
     * @param key The key.
     * @return A string representation of the key's value or null.
     */
    public String getString(String key)
    {
        Object value = this.map.get(key);
        
        if(value == null) return null;
        
        return value.toString();
    }
    
    /**
     * Gets the key's value and returns its string representation.
     * Otherwise returns the provided default value if the key doesn't exist or the key's value is null. 
     * @param key The key.
     * @param defaultValue The value to return if the key is missing or the key's value is null.
     * @return A string representation of the key's value or the provided defaultValue.
     */
    public String getString(String key, String defaultValue)
    {
        Object value = this.map.get(key);
        
        if(value == null) return defaultValue;
        
        return value.toString();
    }

    @Override
    public Object put(String key, Object value)
    {
        if(ordered && !orderedKeys.contains(key)) orderedKeys.add(key);
        
        return this.map.put(key, value);
    }

    @Override
    public Object remove(Object key)
    {
        if(ordered) orderedKeys.remove((String)key);
        
        return this.map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> map)
    {
        if(ordered)
        {
            for(String key : map.keySet())
            {
                if(!orderedKeys.contains(key)) orderedKeys.add(key);
            }
        }
        
        this.map.putAll(map);
    }

    @Override
    public void clear()
    {
        if(ordered) orderedKeys.clear();
        
        this.map.clear();
    }

    @Override
    public Set<String> keySet()
    {
        if(ordered) return orderedKeys;
        
        return this.map.keySet();
    }

    @Override
    public Collection<Object> values()
    {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet()
    {
        return this.map.entrySet();
    }

    @Override
    public String toString()
    {
        try
        {
            return new JSONWriter().write(this).toString();
        }
        catch(IOException e) { throw new ConvirganceException(e); }
    }

    /**
     * Returns this object as a String after its process by JSONWriter.
     * @param indent The number of spaces to use for each level of indentation.
     * @return A formatted JSON string representation of this object.
     * @throws ConvirganceException If the JSONWriter encounters an error during string conversion.
     */
    public String toString(int indent)
    {
        try
        {
            return new JSONWriter(indent).write(this).toString();
        }
        catch(IOException e) { throw new ConvirganceException(e); }
    }
    
    @Override
    public boolean equals(Object obj)
    {
        JSONObject other;
        Object left;
        Object right;
        
        if(obj == this) return true;
        if(!(obj instanceof JSONObject)) return false;
        
        other = (JSONObject)obj;
        
        if(other.size() != size()) return false;
        
        for(String key : keySet())
        {
            if(!other.containsKey(key)) return false;
            
            left = get(key);
            right = other.get(key);
            
            if(left == null && right == null) continue;
            if(left == null) return false;
            if(right == null) return false;

            if(!left.getClass().equals(right.getClass())) return false;
            if(!left.equals(right)) return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode()
    {
        int hash = 0xC0FFEE;
        Object value;

        for(String key : keySet())
        {
            value = get(key);
            
            if(value == null) continue;

            hash += value.hashCode();
        }

        return hash + size();
    }
    
    /**
     * A hybrid collection that maintains insertion order like an ArrayList while ensuring
     * unique elements like a Set.
     * @param <T> The type of elements maintained by this collection.
     */
    private class OrderedKeys<T> extends ArrayList<T> implements Set<T>
    {
        /**
        * Constructs an empty ordered set.
        */
        public OrderedKeys()
        {
            super();
        }
        
        /**
        * Constructs an ordered set containing the elements of the specified collection.
        * @param collection The collection whose elements are to be placed into this ordered set.
        */
        public OrderedKeys(Collection collection)
        {
            super(collection);
        }
    }
}
