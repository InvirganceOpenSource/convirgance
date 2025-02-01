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
import java.util.Iterator;

/**
 * An interface for applying lazy transformations to JSON objects during iteration.
 * This enables efficient streaming transformations of data without loading entire
 * collections into memory.
 * 
 * <p>Transformations can include data cleaning, filtering, enrichment, or any other
 * modifications to JSON objects. The transformations are applied on-the-fly as
 * elements are accessed through the iterator.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
* // Create a transformer that adds a timestamp to each record
 * Transformer timestampTransformer = new Transformer() {
 *     public Iterator&lt;JSONObject&gt; transform(Iterator&lt;JSONObject&gt; iterator) {
 *         return new Iterator&lt;JSONObject&gt;() {
 *             public JSONObject next() {
 *                 JSONObject obj = iterator.next();
 *                 obj.put("timestamp", System.currentTimeMillis());
 *                 return obj;
 *             }
 *             // ... implement other Iterator methods
 *         };
 *     }
 * };
 * </pre>
 * 
 * @author jbanes
 */
public interface Transformer
{
    /**
     * Provides a lazy transformation mechanism for an {@link Iterable} of JSON objects.
     * The transformation is applied to each element only when it is accessed through
     * the iterator.
     * 
     * <p>This default implementation wraps the iterator-based transformation,
     * preserving the lazy evaluation semantics.</p>
     *
     * @param iterable The source collection of JSON objects to transform.
     * @return An {@link Iterable} that will apply transformations to elements when iterated.
     * @throws ConvirganceException If an error occurs during transformation.
     */
    public default Iterable<JSONObject> transform(final Iterable<JSONObject> iterable) throws ConvirganceException
    {
        return new Iterable<JSONObject>() {
            
            @Override
            public Iterator<JSONObject> iterator()
            {
                return transform(iterable.iterator());
            }
        };
    }
    
    /**
     * Creates an iterator that transforms JSON objects as they are accessed.
     * Implementations should maintain lazy evaluation by only transforming
     * elements when they are requested through the iterator.
     * 
     * <p>The returned iterator should follow standard Iterator contract:</p>
     * <ul>
     * <li>Only transform elements when next() is called</li>
     * <li>Throw NoSuchElementException if next() is called when hasNext() is false</li>
     * <li>Maintain consistent state between hasNext() and next() calls</li>
     * </ul>
     *
     * @param iterator The source iterator providing JSON objects to transform.
     * @return An iterator that applies transformations to elements as they are accessed.
     */
    public Iterator<JSONObject> transform(Iterator<JSONObject> iterator);
}
