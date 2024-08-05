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
package com.invirgance.convirgance.source;

import java.io.InputStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class ClasspathSourceTest
{

    @Test
    public void testUsage() throws Exception
    {
        String expected = "This is a test";
        StringBuffer buffer = new StringBuffer();
        int c;
        
        ClasspathSource source = new ClasspathSource("/source/bytearray/test.txt");
        
        // Ensure the source is configured as expected
        assertTrue(source.isReusable());
        assertFalse(source.isUsed());
        
        try(InputStream in = source.getInputStream())
        {
            while((c = in.read()) >= 0)
            {
                buffer.append((char)c);
            }
            
            assertEquals(expected, buffer.toString());
        }
        
        // Ensure that nothing has changed through use
        assertTrue(source.isReusable());
        assertFalse(source.isUsed());
        
        // Verify that the source can be reused
        buffer.setLength(0);
        
        try(InputStream in = source.getInputStream())
        {
            while((c = in.read()) >= 0)
            {
                buffer.append((char)c);
            }
            
            assertEquals(expected, buffer.toString());
        }
        
        // Ensure once again that nothing has changed through use
        assertTrue(source.isReusable());
        assertFalse(source.isUsed());
    }
    
}
