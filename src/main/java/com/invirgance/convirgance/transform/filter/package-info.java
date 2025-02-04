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
 * Provides transformers to filter data from streams based upon common 
 * logic like equals, great than, less than, etc.
 * 
 * <p>Key features of this package:</p>
 * <ul>
 * <li>Supports logical operations such as AND, OR, and NOT.</li>
 * <li>Provides comparison filters for greater than, less than, and equality checks.</li>
 * <li>Designed to be composable for complex filtering scenarios.</li>
 * </ul>
 *
 * <p>Common filters include:</p>
 * <ul>
 * <li>{@link com.invirgance.convirgance.transform.filter.AndFilter} - Combines multiple filters using logical AND.</li>
 * <li>{@link com.invirgance.convirgance.transform.filter.OrFilter} - Combines multiple filters using logical OR.</li>
 * <li>{@link com.invirgance.convirgance.transform.filter.NotFilter} - Negates a given filter.</li>
 * <li>{@link com.invirgance.convirgance.transform.filter.EqualsFilter} - Checks for equality between values.</li>
 * <li>{@link com.invirgance.convirgance.transform.filter.GreaterThanFilter} - Filters values greater than a threshold.</li>
 * <li>{@link com.invirgance.convirgance.transform.filter.LessThanFilter} - Filters values less than a threshold.</li>
 * <li>{@link com.invirgance.convirgance.transform.filter.ComparatorFilter} - Applies custom comparisons using a comparator.</li>
 * </ul>
 * 
 * @see com.invirgance.convirgance.transform.filter.Filter
 * @since 1.0.0
 */
package com.invirgance.convirgance.transform.filter;
