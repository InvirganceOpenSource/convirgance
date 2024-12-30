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

import com.invirgance.convirgance.ConvirganceException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Base64;

/**
 * Allows streaming in JSON from a URL
 * @author tadghh
 */
public class URLPathSource implements Source
{
    private final java.net.URI uri;
    private InputStream currentStream;
    
    /**
     * The URL/URI to source JSON from, supporting base64 encoded data:application/json URIs
     * @param uri 
     */
    public URLPathSource(URI uri)
    {
          this.uri = uri;
    }
    
    @Override
    public InputStream getInputStream()
    {
        try
        {
         if ("data".equals(uri.getScheme())) 
         {
            String data = uri.getSchemeSpecificPart();
            String[] parts = data.split(",", 2);
            
            if (parts.length != 2) 
            {
                throw new IOException("Invalid data URI format");
            }
            
            String encodedData = parts[1];
            byte[] decodedData = Base64.getDecoder().decode(encodedData);
            currentStream = new ByteArraySource(decodedData).getInputStream();
            return currentStream;
        }
        
        URLConnection connection = uri.toURL().openConnection();
        currentStream = connection.getInputStream();
        return currentStream;
        }
        catch (MalformedURLException ex)
        {
            throw new ConvirganceException(ex);
        }
        catch (IOException ex)
        {
            throw new ConvirganceException(ex);
        }

    }
}
