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
package com.invirgance.convirgance.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 *
 *
 * @author jbanes
 */
public class JSONWriter implements AutoCloseable
{

    private final Writer writer;
    private int indent;
    private int level;

    public JSONWriter()
    {
        this(new StringWriter());
    }

    /**
     * Creates a new JSONWriter that will use the provided indentation when
     * writing.
     *
     * @param indent The indentation to use when writing.
     */
    public JSONWriter(int indent)
    {
        this(new StringWriter(), indent);
    }

    /**
     * Creates a JSONWriter using another Writer object. Example writing to
     * memory, string, network.
     *
     * @param writer The writer that should be used.
     */
    public JSONWriter(Writer writer)
    {
        this(writer, 0);
    }

    /**
     * Creates a JSONWriter using another Writer object. Example writing to
     * memory, string, network. Uses the provided indent when writing content.
     *
     * @param writer The writer that should be used.
     * @param indent The indentation to use when writing.
     */
    public JSONWriter(Writer writer, int indent)
    {
        this.writer = writer;
        this.indent = indent;
    }

    /**
     * Returns the current writer.
     *
     * @return This objects current writer.
     */
    public Writer getWriter()
    {
        return writer;
    }

    /**
     * Returns the current indent being used when writing.
     *
     * @return The indentation.
     */
    public int getIndent()
    {
        return indent;
    }

    /**
     * Sets the indentation to use when writing.
     *
     * @param indent The indentation.
     */
    public void setIndent(int indent)
    {
        this.indent = indent;
    }

    /**
     *
     * @param c
     * @return
     */
    private String encodeUnicodeHex(char c)
    {
        StringBuilder buffer = new StringBuilder("\\u");
        String hex = Integer.toHexString(c);

        while (buffer.length() + hex.length() < 6)
        {
            buffer.append('0');
        }

        buffer.append(hex);

        return buffer.toString();
    }

    /**
     *
     * @param level
     * @throws IOException
     */
    private void indent(int level) throws IOException
    {
        for (int i = 0; i < level; i++)
        {
            for (int j = 0; j < indent; j++)
            {
                writer.write(' ');
            }
        }
    }

    /**
     *
     * @return @throws IOException
     */
    public JSONWriter writeNull() throws IOException
    {
        writer.write("null");

        return this;
    }

    /**
     *
     * @param value
     * @return
     * @throws IOException
     */
    public JSONWriter write(boolean value) throws IOException
    {
        writer.write(String.valueOf(value));

        return this;
    }

    /**
     *
     * @param string
     * @return
     * @throws IOException
     */
    public JSONWriter write(String string) throws IOException
    {
        char c;

        writer.write('"');

        for (int i = 0; i < string.length(); i++)
        {
            c = string.charAt(i);

            if (c >= 32 && c != '"' && c != '\\')
            {
                writer.write(c);
                continue;
            }

            switch (c)
            {
                case '\"':
                    writer.write("\\\"");
                    break;

                case '\\':
                    writer.write("\\\\");
                    break;

                case '\b':
                    writer.write("\\b");
                    break;

                case '\f':
                    writer.write("\\f");
                    break;

                case '\n':
                    writer.write("\\n");
                    break;

                case '\r':
                    writer.write("\\r");
                    break;

                case '\t':
                    writer.write("\\t");
                    break;

                default:
                    writer.write(encodeUnicodeHex(c));
            }
        }

        writer.write('"');

        return this;
    }

    /**
     *
     * @param number
     * @return
     * @throws IOException
     */
    public JSONWriter write(Number number) throws IOException
    {
        writer.write(number.toString());

        return this;
    }

    /**
     *
     * @param object
     * @return
     * @throws IOException
     */
    public JSONWriter write(JSONObject object) throws IOException
    {
        int count = 0;

        writer.write('{');

        if (indent > 0 && !object.isEmpty())
        {
            writer.write('\n');
            level++;
        }

        for (String key : object.keySet())
        {
            if (count > 0)
            {
                writer.write(",");

                if (indent > 0)
                {
                    writer.write("\n");
                }
            }

            indent(level);

            write(key);
            writer.write(':');

            if (indent > 0)
            {
                writer.write(' ');
            }

            write(object.get(key));

            count++;
        }

        if (indent > 0 && !object.isEmpty())
        {
            writer.write('\n');
            level--;

            indent(level);
        }

        writer.write('}');

        return this;
    }

    /**
     *
     * @param array
     * @return
     * @throws IOException
     */
    public JSONWriter write(JSONArray array) throws IOException
    {
        int count = 0;

        writer.write('[');

        if (indent > 0 && !array.isEmpty())
        {
            writer.write('\n');
            level++;
        }

        for (Object value : array)
        {
            if (count > 0)
            {
                writer.write(",");

                if (indent > 0)
                {
                    writer.write("\n");
                }
            }

            indent(level);
            write(value);

            count++;
        }

        if (indent > 0 && !array.isEmpty())
        {
            writer.write('\n');
            level--;

            indent(level);
        }

        writer.write(']');

        return this;
    }

    /**
     *
     * @param object
     * @return
     * @throws IOException
     */
    public JSONWriter write(Object object) throws IOException
    {
        if (object == null)
        {
            return writeNull();
        }
        else if (object instanceof Boolean)
        {
            return write((boolean) object);
        }
        else if (object instanceof String)
        {
            return write((String) object);
        }
        else if (object instanceof Number)
        {
            return write((Number) object);
        }
        else if (object instanceof JSONObject)
        {
            return write((JSONObject) object);
        }
        else if (object instanceof JSONArray)
        {
            return write((JSONArray) object);
        }
        else
        {
            throw new IOException("Unrecognized object type " + object.getClass().getName());
        }
    }

    @Override
    public void close() throws Exception
    {
        this.writer.close();
    }

    @Override
    public String toString()
    {
        if (writer instanceof StringWriter)
        {
            return writer.toString();
        }

        return super.toString();
    }

}
