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
import com.invirgance.convirgance.jbin.BinaryEncoder;
import com.invirgance.convirgance.jbin.KeyEncoder;
import com.invirgance.convirgance.jbin.KeyStreamEncoder;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.target.Target;
import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * Encodes data into JBIN and writes it to a target, with optional compression.
 * 
 * @author jbanes
 */
public class JBINOutput implements Output
{

    private boolean compressed;

    /**
     * Creates a new JBINOutput with compression set to false.
     */
    public JBINOutput()
    {
        this(false); // TODO: Need a multithreaded GZipping to speed up compressed data. Then we can make compressed the default.
    }

    /**
     * Creates a new JBINOutput, that will use compression depending on the provided value.
     *
     * @param compressed If compression should be used.
     */
    public JBINOutput(boolean compressed)
    {
        this.compressed = compressed;
    }

    /**
     * Returns if compression is used when writing.
     *
     * @return True if compression is enabled, false otherwise
     */
    public boolean isCompressed()
    {
        return compressed;
    }

    /**
     * Enable or disable the usage of compression when writing to a target.
     *
     * @param compressed True to enable compression, false to disable it.
     */
    public void setCompressed(boolean compressed)
    {
        this.compressed = compressed;
    }

    @Override
    public OutputCursor write(Target target)
    {
        return new JBINOutputCursor(target, compressed);
    }

    private class JBINOutputCursor implements OutputCursor
    {

        private final DataOutputStream out;
        private final KeyEncoder keys;
        private final BinaryEncoder json;

        private int count;

        public JBINOutputCursor(Target target, boolean compressed)
        {
            OutputStream out;

            try
            {
                out = target.getOutputStream();

                out.write(0xFF);
                out.write(0xFF);
                out.write('B');
                out.write('S');
                out.write('O');
                out.write('N');
                out.write(0x01); // Version 1
                out.write(getFlags(compressed)); // Flags

                this.out = new DataOutputStream(compressed ? new GZIPOutputStream(out, 4 * 1024 * 1024) : new BufferedOutputStream(out, 4 * 1024 * 1024));
                this.keys = new KeyStreamEncoder();
                this.json = new BinaryEncoder(keys);
            }
            catch (IOException e)
            {
                throw new ConvirganceException(e);
            }
        }

        private int getFlags(boolean compressed)
        {
            int flags = 0;

            if (compressed)
            {
                flags |= 0x01;
            }

            return flags;
        }

        @Override
        public void write(JSONObject record)
        {
            try
            {
                this.json.write(record, out);

                count++;
            }
            catch (IOException e)
            {
                throw new ConvirganceException(e);
            }
        }

        @Override
        public void close() throws Exception
        {
            this.out.write(BinaryEncoder.TYPE_EOF);
            this.out.close();
        }
    }

}
