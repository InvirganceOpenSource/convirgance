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
 * Finds common JSON records that appear in multiple sorted {@code Iterable} streams.
 * <p>
 * This class is useful for scenarios where large datasets are being processed, and we need to
 * identify records that share specific key-value pairs across multiple sources. The streams must
 * be sorted in order for the intersection logic to work correctly.
 * </p>
 *
 * <p>Example use case:</p>
 * <pre>
 *     List&lt;Iterable&lt;JSONObject&gt;&gt; streams = Arrays.asList(hospitalA, hospitalB);
 *     IntersectionIterable intersection = new IntersectionIterable(new String[]{"patient_id"}, streams);
 * 
 *     for (JSONObject common : intersection) 
 *     {
 *         System.out.println(common);
 *     }
 * </pre>
 * <p><b>Note:</b> The input streams must be pre-sorted based on the given keys. If they are not sorted, 
 * the behavior of this class is undefined.</p>
 * @author tadghh
 */
public class IntersectionIterable implements Iterable<JSONObject> 
{
    private List<Iterable<JSONObject>> streams;
    private String[] keys;

    /**
     * Constructs an IntersectionIterable that identifies common JSON records appearing across all provided streams.
     * The streams must contain sorted JSON data based on the specified keys.
     *
     * @param keys The keys used to determine record equivalence.
     * @param streams The sorted streams of {@code JSONObject} records to be compared.
     */
    public IntersectionIterable(String[] keys, Iterable<JSONObject>... streams) 
    {
        this(keys, Arrays.asList(streams));
    }

    /**
     * Constructs an IntersectionIterable that identifies common JSON records appearing across all provided streams.
     * The streams must contain sorted JSON data based on the specified keys.
     *
     * @param keys The keys used to determine record equivalence.
     * @param streams The sorted streams of {@code JSONObject} records to be compared.
     */    
    public IntersectionIterable(String[] keys, List<Iterable<JSONObject>> streams) 
    {
        this.keys = keys;
        this.streams = streams;
    }

    /**
     * Returns an iterator that streams through each source returning common records found in all streams.
     * 
     * @return An iterator that contains the common records.
     */
    @Override
    public Iterator<JSONObject> iterator() 
    {
        return new Iterator<JSONObject>() {
            private final CoerciveComparator comparator = new CoerciveComparator();
            private final List<Iterator<JSONObject>> iterators = new ArrayList<>();
            private final List<JSONObject> heads = new ArrayList<>();
            private JSONObject matching;
            
            {
                Iterator<JSONObject> streamIterator;
                
                for(Iterable<JSONObject> stream : streams) 
                {
                    streamIterator = stream.iterator();
                    iterators.add(streamIterator);
                    
                    if(streamIterator.hasNext()) 
                    {
                        heads.add(streamIterator.next());
                    }
                    else 
                    {
                        heads.clear();
                        break;
                    }
                }
                
                findNextMatch();
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
                findNextMatch();
                return result;
            }
            
            private void findNextMatch() 
            {
                matching = null;
                
                while(!heads.isEmpty()) 
                {
                    if(findMatch()) 
                    {
                        matching = heads.get(0);
              
                        for(int i = 0; i < iterators.size(); i++) 
                        {
                            if(iterators.get(i).hasNext()) 
                            {
                                heads.set(i, iterators.get(i).next());
                            } 
                            else 
                            {
                                heads.clear();
                                return;
                            }
                        }
                        
                        return;
                    }
                }
            }
            
            private boolean findMatch()
            {
                boolean matching = true;

                // The index of the iterator that has the smallest head value.
                int smallestIterator;
                int comparison;
                Object smallest;
                Object largest;
                Object evaluated;

                JSONObject next;
                
                for(String key : keys)
                {
                    smallest = heads.get(0).get(key);
                    largest = smallest;
                    smallestIterator = 0;
                    
                    // Comparing the current records across the streams.
                    for(int i = 1; i < heads.size(); i++)
                    {
                        evaluated = heads.get(i).get(key);
                        comparison = comparator.compare(evaluated, smallest);

                        if(comparison < 0)
                        {
                            smallest = evaluated;
                            smallestIterator = i;
                            matching = false;
                        }
                        else if(comparison > 0)
                        {
                            matching = false;
                            
                            // The comparison value is larger than our smallest, lets leverage this...
                            if(comparator.compare(evaluated, largest) > 0) largest = evaluated;
                        }
                    }

                    if(!matching)
                    {
                        // We can advance the smallest iterator directly to the `largest` (largest head record among the streams)
                        while(comparator.compare(smallest, largest) < 0)
                        {
                            if(!iterators.get(smallestIterator).hasNext())
                            {
                                heads.clear();
                                return matching;
                            }
                            
                            next = iterators.get(smallestIterator).next();
                            heads.set(smallestIterator, next);
                            smallest = next.get(key);
                        }
                            
                        break;
                    }
                }
                
                return matching;
            }
        };
    }
}
