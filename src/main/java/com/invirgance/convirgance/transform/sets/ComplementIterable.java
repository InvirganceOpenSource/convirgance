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
package com.invirgance.convirgance.transform.sets;

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.transform.filter.CoerciveComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author tadghh
 */
public class ComplementIterable implements Iterable<JSONObject> 
{
    private List<Iterable<JSONObject>> streams;
    private String[] keys;

    /**
     * Constructs an ComplementIterable that identifies unique JSON records from a target stream that don't appear in the other stream(s).
     * The streams must contain sorted JSON data based on the specified keys.
     *
     * @param keys The keys used to determine record equivalence.
     * @param streams The sorted streams of {@code JSONObject} records to be compared.
     */
    public ComplementIterable(String[] keys, Iterable<JSONObject>... streams) 
    {
        this(keys, Arrays.asList(streams));
    }

    /**
     * Constructs an ComplementIterable that identifies unique JSON records from a target stream that don't appear in the other stream(s).
     * The streams must contain sorted JSON data based on the specified keys.
     *
     * @param keys The keys used to determine record equivalence.
     * @param streams The sorted streams of {@code JSONObject} records to be compared.
     */    
    public ComplementIterable(String[] keys, List<Iterable<JSONObject>> streams) 
    {
        this.keys = keys;
        this.streams = streams;
    }

    /**
     * Returns an iterator that streams through each source returning records found in the target stream but not in the complement stream(s).
     * 
     * @return An iterator that contains the complement records.
     */
    @Override
    public Iterator<JSONObject> iterator() 
    {
        return new ComplementIterator();
    }
    
    private class ComplementIterator implements Iterator<JSONObject> 
    {
        private final CoerciveComparator comparator = new CoerciveComparator();
        private final List<Iterator<JSONObject>> iterators = new ArrayList<>();
        private final List<JSONObject> heads = new ArrayList<>();
        private JSONObject matching;

        public ComplementIterator() 
        {
            Iterator<JSONObject> current;

            for(Iterable<JSONObject> stream : streams) 
            {
                current = stream.iterator();
                iterators.add(current);

                if(current.hasNext()) heads.add(current.next());
                else break;
            }

            checkUnique();
        }

        @Override
        public boolean hasNext() 
        {
            return matching != null;
        }

        @Override
        public JSONObject next() 
        {
            JSONObject result = matching;
            checkUnique();
            return result;
        }
       
        private int checkCompare(JSONObject main, JSONObject compare)
        {
            int comparison = 0;

            for(String key : keys) 
            {
                comparison = comparator.compare(compare.get(key), main.get(key));

                if(comparison != 0) break;  
            }

            return comparison;
        }
       
        private void checkUnique() 
        {
            matching = null;
            
            while(!heads.isEmpty()) 
            {
                if(findUnique()) matching = heads.get(0);
                
                if(iterators.get(0).hasNext()) 
                {
                    heads.set(0, iterators.get(0).next());
                    if(matching != null) return;
                } 
                else 
                {
                    heads.clear();
                    return;
                }
            }
        }

        private boolean findUnique() 
        {
            int comparison;
                    
            Iterator<JSONObject> compareIterator;
            JSONObject compare;
            JSONObject main;
            
            if(heads.isEmpty()) return false;
            
            main = heads.get(0);

            for(int i = 1; i < heads.size(); i++) 
            {
                compare = heads.get(i);
                compareIterator = iterators.get(i);

                while(true) 
                {
                    comparison = checkCompare(main,compare);
                    
                    if(comparison == 0) 
                    {
                       return false;
                    } 
                    else if(comparison > 0) 
                    {
                       break;
                    } 
                    else
                    {
                        if (!compareIterator.hasNext()) break;
                        compare = compareIterator.next();
                        heads.set(i, compare);
                    }
                }
            }

           return true;
        }
    }
}
