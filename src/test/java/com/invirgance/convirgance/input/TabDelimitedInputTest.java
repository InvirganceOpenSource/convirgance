/*
 * The MIT License
 *
 * Copyright 2025 Invirgance LLC.
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
package com.invirgance.convirgance.input;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.InputStreamSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;


/**
 *
 * @author timur
 */
public class TabDelimitedInputTest
{
    @Test
    public void testExample1()
    {
        TabDelimitedInput example1 = new TabDelimitedInput();
        
        int size;
        int total = 0;
        int count = 3;
        boolean empty = false;
        
        for(JSONObject record : example1.read(new InputStreamSource(getClass().getResourceAsStream("/input/tabdelimited/example1.txt"))))
        {
            System.out.println(total);
            assertEquals(count, record.size());
            
            size = record.size();
            
            for(int i=1; i<=size-1; i++)
            {
                assertEquals("Value " + i, record.get("Column " + i));
            }
            
            // Last item will be alternating blank or not blank
            if(empty) assertEquals("", record.get("Column " + size));
            else assertEquals("Value " + size, record.get("Column " + size));
            
            empty = !empty;
            
            if(!empty) count--;
            
            total++;
        }
        
        assertEquals(5, total);
    }
    
    @Test
    public void testChangingDelimiter()
    {
        TabDelimitedInput input = new TabDelimitedInput();
        
        try 
        {
            input.setDelimiter('|');
            
            fail("Expected exception on setDelimiter()");
        }
        catch (ConvirganceException e)
        {
            assertEquals("Cannot set delimiter for TabDelimitedInput class", e.getMessage());
        }   
    }
}
