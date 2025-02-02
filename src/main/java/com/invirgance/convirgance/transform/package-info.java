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

/**
 * A library transformers for manipulating streams of data
 * 
 * <p>Key features of this package:</p>
 * <ul>
 * <li>Lazy evaluation - transformations are only applied when data is accessed.</li>
 * <li>Memory efficient - processes data as streams without loading entire collections.</li>
 * <li>Composable - transformers can be chained together for complex operations.</li>
 * </ul>
 * 
 * <p>Common transformers include:</p>
 * <ul>
 * <li>{@link com.invirgance.convirgance.transform.CoerceStringsTransformer} - Converts string values to appropriate data types.</li>
 * <li>{@link com.invirgance.convirgance.transform.InsertKeyTransformer} - Adds or updates fields in JSON objects.</li>
 * <li>{@link com.invirgance.convirgance.transform.SortedGroupByTransformer} - Groups pre-sorted data by common fields.</li>
 * <li>{@link com.invirgance.convirgance.transform.UnsortedGroupByTransformer} - Groups unsorted data by common fields.</li>
 * </ul>
 * 
 * @see com.invirgance.convirgance.transform.Transformer
 * @since 1.0.0
 */
package com.invirgance.convirgance.transform;
