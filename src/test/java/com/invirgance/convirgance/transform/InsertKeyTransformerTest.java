/*
 * The MIT License
 *
 * Copyright 2025 jbanes.
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
package com.invirgance.convirgance.transform;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class InsertKeyTransformerTest
{
    @Test
    public void testValue()
    {
        JSONObject record = new JSONObject();
        InsertKeyTransformer transformer = new InsertKeyTransformer("hello", "world");
        
        assertEquals(0, record.size());
        
        record = transformer.transform(record);
        
        assertEquals(1, record.size());
        assertEquals("world", record.get("hello"));
    }
    
    @Test
    public void testGenerator()
    {
        JSONObject record = new JSONObject();
        InsertKeyTransformer transformer = new InsertKeyTransformer("hello", new ValueGenerator<String>() {

            @Override
            public String generate(JSONObject record) throws ConvirganceException
            {
                assertEquals(0, record.size());
                
                return "world";
            }
        });
        
        assertEquals(0, record.size());
        
        record = transformer.transform(record);
        
        assertEquals(1, record.size());
        assertEquals("world", record.get("hello"));
    }
}
