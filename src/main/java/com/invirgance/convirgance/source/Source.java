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

import com.invirgance.convirgance.ConvirganceException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstracts away access to an on-demand InputStream over a given resource. Most 
 * implementations are able to provide repeated access to the underlying resource.
 * However, not all resources can be reused and multiple calls to 
 * {@link #getInputStream()} will fail if {@link #isReusable()} returns false.
 * 
 * @author jbanes
 */
public interface Source
{
    /**
     * Returns an {@link InputStream} over the underlying resource
     * 
     * @return an InputStream
     */
    public InputStream getInputStream();
    
    /**
     * Returns true if the source is reusable. The default implementation returns
     * true, so this must be overridden if your implementation is not reusable.
     * 
     * @return true
     */
    default public boolean isReusable()
    {
        return true;
    }
    
    /**
     * Returns true if the InputStream has already been accessed and another attempt
     * to access {@link #getInputStream()} will fail. The default implementation returns
     * false, so this must be overridden if your implementation is not reusable.
     * 
     * @return false
     */
    default public boolean isUsed()
    {
        return false;
    }
    
    /**
     * Convenience method for loading the contents of the source as a UTF-8 
     * string. This is useful for small files like configuration files, SQL 
     * files, and other small reads. Do not use for large reads or there may 
     * be significant performance problems or failures.
     * 
     * @return a string of the source contents
     */
    default public String readString()
    {
        return readString("UTF-8");
    }
    
    /**
     * Convenience method for loading the contents of the source as a string, 
     * using the specified encoding. This is useful for small files like 
     * configuration files, SQL files, and other small reads. Do not use for
     * large reads or there may be significant performance problems or 
     * failures.
     * 
     * @param encoding the character set encoding to interpret the data as
     * @return a string of the source contents
     */
    default public String readString(String encoding)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        
        int count;
        
        try(InputStream in = getInputStream())
        {
            while((count = in.read(data)) > 0) out.write(data, 0, count);
            
            return new String(out.toByteArray(), encoding);
        }
        catch(IOException e)
        {
            throw new ConvirganceException(e);
        }
    }
}
