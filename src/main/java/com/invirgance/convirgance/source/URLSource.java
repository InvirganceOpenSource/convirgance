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
import java.net.URL;
import java.net.URLConnection;

/**
 * Allows streaming in data from a URL.
 *
 * @author tadghh
 */
public class URLSource implements Source
{

    private final URL url;
    private InputStream currentStream;

    /**
     * The URL to source data from.
     *
     * @param url The URL pointing to some data.
     */
    public URLSource(URL url)
    {
        this.url = url;
    }

    @Override
    public InputStream getInputStream()
    {
        URLConnection connection;
        
        try
        {
            connection = url.openConnection();
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
