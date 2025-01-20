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
package com.invirgance.convirgance.jbin;

import com.invirgance.convirgance.jbin.StringEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class StringEncoderTest
{
    @Test
    public void testLoop() throws Exception
    {
        StringEncoder encoder = new StringEncoder();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataout = new DataOutputStream(out);
        
        ByteArrayInputStream in;
        DataInputStream datain;
        
        int index = 0;
        int c;
        
        for(int i=0; i<2048; i++)
        {
            assertEquals(i & 0xFF, encoder.write(Integer.toString(i), dataout));
            assertEquals(Integer.toString(i), encoder.get(i & 0xFF));
        }
        
        in = new ByteArrayInputStream(out.toByteArray());
        datain = new DataInputStream(in);
        
        while((c = in.read()) >= 0)
        {
            assertEquals(StringEncoder.STRING_REGISTER_OPERATION, c);
            
            encoder.read(datain);
            
            assertEquals(index & 0xFF, encoder.get(Integer.toString(index)));
            assertEquals(Integer.toString(index), encoder.get(index & 0xFF));
            
            index++;
        }
    }
    
}
