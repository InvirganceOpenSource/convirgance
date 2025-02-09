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
package com.invirgance.convirgance.input;

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.source.InputStreamSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class DelimitedInputTest
{
    
    @Test
    public void testEmpty()
    {
        DelimitedInput empty = new DelimitedInput(new String[] {"Column 1"}, '|');
        DelimitedInput header = new DelimitedInput();
        
        assertFalse(empty.read(new InputStreamSource(getClass().getResourceAsStream("/input/delimited/empty.txt"))).iterator().hasNext());
        assertFalse(header.read(new InputStreamSource(getClass().getResourceAsStream("/input/delimited/header.txt"))).iterator().hasNext());
    }
    
    @Test
    public void testExample1()
    {
        DelimitedInput example1 = new DelimitedInput();
        
        int size;
        int total = 0;
        int count = 3;
        boolean empty = false;
        
        example1.setNullable(false); // Disable parsing as nulls
        
        for(JSONObject record : example1.read(new InputStreamSource(getClass().getResourceAsStream("/input/delimited/example1.txt"))))
        {
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
        
        // Reset state
        total = 0;
        count = 3;
        empty = false;
        
        example1.setNullable(true); // Enable parsing as nulls
        
        for(JSONObject record : example1.read(new InputStreamSource(getClass().getResourceAsStream("/input/delimited/example1.txt"))))
        {
            assertEquals(count, record.size());
            
            size = record.size();
            
            for(int i=1; i<=size-1; i++)
            {
                assertEquals("Value " + i, record.get("Column " + i));
            }
            
            // Last item will be alternating blank or not blank
            if(empty) assertNull(record.get("Column " + size));
            else assertEquals("Value " + size, record.get("Column " + size));
            
            empty = !empty;
            
            if(!empty) count--;
            
            total++;
        }
        
        assertEquals(5, total);
    }
    
    @Test
    public void testParseLine()
    {
        String[] none = DelimitedInput.parseLine("", '|', false);
        String[] one = DelimitedInput.parseLine("Column 1", '|', false);
        String[] two = DelimitedInput.parseLine("Column 1|Column 2", '|', false);
        String[] three = DelimitedInput.parseLine("Column 1|Column 2|Column 3", '|', false);
        String[] trailing = DelimitedInput.parseLine("Column 1|Column 2|Column 3|", '|', false);
        String[] empty = DelimitedInput.parseLine("|||", '|', false);
        String[] nulls = DelimitedInput.parseLine("|||", '|', true);
       
        assertEquals(0, none.length);
        assertEquals(1, one.length);
        assertEquals(2, two.length);
        assertEquals(3, three.length);
        assertEquals(4, trailing.length);
        assertEquals(3, three.length);
        
        assertEquals("Column 1", one[0]);
        assertEquals("Column 1", two[0]);
        assertEquals("Column 2", two[1]);
        assertEquals("Column 1", three[0]);
        assertEquals("Column 2", three[1]);
        assertEquals("Column 3", three[2]);
        assertEquals("Column 1", trailing[0]);
        assertEquals("Column 2", trailing[1]);
        assertEquals("Column 3", trailing[2]);
        assertEquals("", trailing[3]);
        assertEquals("", empty[0]);
        assertEquals("", empty[1]);
        assertEquals("", empty[2]);
        assertNull(nulls[0]);
        assertNull(nulls[1]);
        assertNull(nulls[2]);
    }
    
}
