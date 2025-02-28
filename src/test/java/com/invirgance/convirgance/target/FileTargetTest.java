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
package com.invirgance.convirgance.target;

import com.invirgance.convirgance.source.FileSource;
import java.io.File;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class FileTargetTest
{
    @Test
    public void testWriteHello()
    {
        File file = new File("target/unit-test-work/target/file/hello.txt");
        
        file.delete();
        
        assertFalse(file.exists());
        
        new FileTarget(file).writeString("Hello world!");
        
        assertTrue(file.exists());
        assertEquals(12, file.length());
        assertEquals("Hello world!", new FileSource(file).readString());
    }

    @Test
    public void testWriteBytes()
    {
        File file = new File("target/unit-test-work/target/file/bytes.bin");
        
        file.delete();
        
        assertFalse(file.exists());
        
        new FileTarget(file).write(new byte[]{0x01, 0x02, 0x03, 0x04});
        
        assertTrue(file.exists());
        assertEquals(4, file.length());
    }
    
}
