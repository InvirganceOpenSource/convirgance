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
package com.invirgance.convirgance.output;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.target.Target;

/**
 *
 * @author jbanes
 */
public interface Output
{
    public OutputCursor write(Target target);
    
    default public void write(Target target, Iterable<JSONObject> records)
    {
        try(OutputCursor cursor = write(target))
        {
            for(var record : records)
            {
                cursor.write(record);
            }
        }
        catch(Exception e) { throw new ConvirganceException(e); }
    }
    
    /**
     * Provides the MIME type for the data format produced by this 
     * implementation. If not set by the implementation, "application/octet"
     * will be returned.
     * 
     * @return The MIME type for the data format
     */
    default public String getContentType()
    {
        return "application/octet";
    }
}
