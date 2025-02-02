/*
 * The MIT License
 *
 * Copyright 2024 tadghh.
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
* Provides classes for reading, writing, and manipulating JSON data. This package 
* supports standard JSON data types and formatting according to the JSON 
* specification.
* <br><br>
* The implementation is similar to the official <code>org.json</code> package
* with the key improvement that the implementation plugs into the Java
* Collections Framework (JCF). {@link JSONObject} implements {@link Map} and 
* {@link JSONArray} implements {@link List}. This provides greater flexibility 
* when working with parsed JSON data and makes the objects compatible with any 
* APIs or frameworks that understands Maps and Lists.
*/
package com.invirgance.convirgance.json;

import java.util.List;
import java.util.Map;