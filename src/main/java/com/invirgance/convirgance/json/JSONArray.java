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
 *
 * @author jbanes
 * @param <T> Optional typing to apply to the array
 */
public class JSONArray<T> implements List<T>
{

    private final ArrayList<T> list;

    public JSONArray()
    {
        this.list = new ArrayList<>();
    }

    /**
     * Creates a JSONArray from a List with a shallow copy of its elements.
     *
     * @param list The source List to copy elements from.
     */
    public JSONArray(List<T> list)
    {
        this.list = new ArrayList<>(list);
    }

    /**
     *
     * @param json
     */
    public JSONArray(String json)
    {
        try
        {
            this.list = new JSONParser(json).parseArray().list;
        }
        catch (IOException e)
        {
            throw new ConvirganceException(e);
        }
    }

    @Override
    public int size()
    {
        return this.list.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(Object object)
    {
        return this.list.contains(object);
    }

    @Override
    public Iterator<T> iterator()
    {
        return this.list.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return this.list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array)
    {
        return this.list.toArray(array);
    }

    @Override
    public boolean add(T object)
    {
        return this.list.add(object);
    }

    @Override
    public boolean remove(Object object)
    {
        return this.list.remove(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        return this.list.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        return this.list.addAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        return this.list.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        return this.list.retainAll(collection);
    }

    @Override
    public void clear()
    {
        this.list.clear();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c)
    {
        return this.list.addAll(index, c);
    }

    @Override
    public T get(int index)
    {
        return this.list.get(index);
    }

    /**
     *
     * @param index
     * @return
     */
    public boolean getBoolean(int index)
    {
        Object value = this.list.get(index);

        if (value == null)
        {
            throw new ConvirganceException("Index " + index + " is null and therefore can't be converted to a boolean");
        }
        if (value instanceof Boolean)
        {
            return ((Boolean) value);
        }
        if (value instanceof String)
        {
            return Boolean.parseBoolean(value.toString());
        }

        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for index " + index + " cannot be converted to a boolean");
    }

    /**
     *
     * @param index
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(int index, boolean defaultValue)
    {
        Object value = this.list.get(index);

        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Boolean)
        {
            return ((Boolean) value);
        }
        if (value instanceof String)
        {
            return Boolean.parseBoolean(value.toString());
        }

        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for index " + index + " cannot be converted to a boolean");
    }

    /**
     *
     * @param index
     * @return
     */
    public JSONArray getJSONArray(int index)
    {
        Object value = this.list.get(index);

        if (value == null)
        {
            return null;
        }
        if (value instanceof JSONArray)
        {
            return ((JSONArray) value);
        }

        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for index " + index + " cannot be converted to a JSONArray");
    }

    /**
     *
     * @param index
     * @param defaultValue
     * @return
     */
    public JSONArray getJSONArray(int index, JSONArray defaultValue)
    {
        Object value = this.list.get(index);

        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof JSONArray)
        {
            return ((JSONArray) value);
        }

        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for index " + index + " cannot be converted to a JSONArray");
    }

    /**
     *
     * @param index
     * @return
     */
    public JSONObject getJSONObject(int index)
    {
        Object value = this.list.get(index);

        if (value == null)
        {
            return null;
        }
        if (value instanceof JSONObject)
        {
            return ((JSONObject) value);
        }

        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for index " + index + " cannot be converted to a JSONObject");
    }

    /**
     *
     * @param index
     * @param defaultValue
     * @return
     */
    public JSONObject getJSONObject(int index, JSONObject defaultValue)
    {
        Object value = this.list.get(index);

        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof JSONObject)
        {
            return ((JSONObject) value);
        }

        throw new ConvirganceException("Class type of " + value.getClass().getName() + " for index " + index + " cannot be converted to a JSONObject");
    }

    /**
     *
     * @param index
     * @return
     */
    public Object getString(int index)
    {
        Object value = this.list.get(index);

        if (value == null)
        {
            return null;
        }

        return value.toString();
    }

    /**
     *
     * @param index
     * @param defaultValue
     * @return
     */
    public Object getString(int index, String defaultValue)
    {
        Object value = this.list.get(index);

        if (value == null)
        {
            return defaultValue;
        }

        return value.toString();
    }

    @Override
    public T set(int index, T element)
    {
        return this.list.set(index, element);
    }

    @Override
    public void add(int index, T element)
    {
        this.list.add(index, element);
    }

    @Override
    public T remove(int index)
    {
        return this.list.remove(index);
    }

    @Override
    public int indexOf(Object o)
    {
        return this.list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return this.list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator()
    {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index)
    {
        return this.list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        return new JSONArray<>(this.list.subList(fromIndex, toIndex));
    }

    @Override
    public String toString()
    {
        try
        {
            return new JSONWriter().write(this).toString();
        }
        catch (IOException e)
        {
            throw new ConvirganceException(e);
        }
    }

    /**
     *
     * @param indent
     * @return
     */
    public String toString(int indent)
    {
        try
        {
            return new JSONWriter(indent).write(this).toString();
        }
        catch (IOException e)
        {
            throw new ConvirganceException(e);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        JSONArray other;
        Iterator iteratorLeft;
        Iterator iteratorRight;
        Object left;
        Object right;

        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof JSONArray))
        {
            return false;
        }

        other = (JSONArray) obj;

        if (other.size() != size())
        {
            return false;
        }

        iteratorLeft = iterator();
        iteratorRight = other.iterator();

        while (iteratorLeft.hasNext())
        {
            left = iteratorLeft.next();
            right = iteratorRight.next();

            if (left == null && right == null)
            {
                continue;
            }
            if (left == null)
            {
                return false;
            }
            if (right == null)
            {
                return false;
            }

            if (!left.getClass().equals(right.getClass()))
            {
                return false;
            }
            if (!left.equals(right))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 0xC011EC7;

        for (Object value : this)
        {
            if (value == null)
            {
                continue;
            }

            hash += value.hashCode();
        }

        return hash + size();
    }
}
