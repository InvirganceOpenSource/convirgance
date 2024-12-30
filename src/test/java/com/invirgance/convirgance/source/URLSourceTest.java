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
package com.invirgance.convirgance.source;

import com.invirgance.convirgance.input.JSONInput;
import com.invirgance.convirgance.output.JSONOutput;
import com.invirgance.convirgance.target.ByteArrayTarget;
import com.invirgance.convirgance.target.FileTarget;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tadghh
 */
public class URLSourceTest
{

    //TODO: 
    // - bad url test
    // - bad json content
    // - fail on bad url
    // - ?
    /**
     * Test of getInputStream method, of class URLSource.
     */
    @Test
    public void testGetInputStream()
    {
        System.out.println("getInputStream");
//        src/test/resources/urlsource/getinputstream/customer.json
        File file = new File("src/test/resources/urlsource/getinputstream/customer.json");

        FileTarget fTarget = new FileTarget(file);
        try
        {
            URL url = file.toURI().toURL();
           
            URLSource instance = new URLSource(url);
            
            // These methods have not been overwritten.
            assertTrue(instance.isReusable());
            assertFalse(instance.isUsed());
            
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            // Load in comparison data
            ByteArraySource target = new ByteArraySource(fileBytes);
            
            // Setup comparison targets
            ByteArrayTarget targetURL = new ByteArrayTarget();
            ByteArrayTarget targetTestByteSource = new ByteArrayTarget();
            
            // Our writer/output
            JSONOutput out = new JSONOutput();
            
            out.write(targetTestByteSource, new JSONInput().read(target));
            out.write(targetURL, new JSONInput().read(instance));
       
            assertArrayEquals(targetURL.getBytes(), targetTestByteSource.getBytes());
            
            //Content has been read. Ensure flags have not been flipped.
            assertTrue(instance.isReusable());
            assertFalse(instance.isUsed());
            
        }
        catch (IOException ex)
        {
            fail("Test failed, missing customer.json in the URLSource test resource folder.");
          
        }
    }

}
