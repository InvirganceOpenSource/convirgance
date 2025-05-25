/*
 * The MIT License
 *
 * Copyright 2025 Invirgance LLC
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
package com.invirgance.convirgance.output;

import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.target.Target;
import com.invirgance.convirgance.wiring.annotation.Wiring;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * Writes data in RFC 4180 compliant CSV format. String data will be quoted with
 * double quotes, quotes in data will be escaped by two double quotes, and
 * newlines are allowed in quoted data. Lines are always terminated with CRLF as
 * recommended by the specification.
 *
 * This class provides functionality to:
 * <ul>
 * <li>Write data with optional column headers</li>
 * <li>Handles string data with proper quote escaping</li>
 * <li>Supports configurable character encoding</li>
 * </ul>
 *
 * @author tadghh
 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC 4180 Specification</a>
 */
@Wiring
public class CSVOutput implements Output
{

    private String encoding = "UTF-8";
    private String[] headers;

    /**
     * Creates a new CSVOutput instance without predefined headers. Headers will
     * be automatically generated from the first record's keys if not set
     * explicitly.
     */
    public CSVOutput()
    {
    }

    /**
     * Creates a new CSVOutput with the provided headers.
     *
     * @param headers The columns to use when writing out CSV data
     */
    public CSVOutput(String... headers)
    {
        this.headers = headers;
    }
    
    /**
     * Creates a new CSVOutput with the provided headers.
     *
     * @param headers The columns to use when writing out CSV data
     * @param encoding The character encoding to use for the output
     */
    public CSVOutput(String[] headers, String encoding)
    {
        this.headers = headers;
        this.encoding = encoding;
    }

    /**
     * Sets the character encoding for the output stream.
     *
     * @param encoding The character encoding to use (e.g., "UTF-8",
     * "ISO-8859-1"). Must be a valid character encoding supported by the JVM.
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Returns the current character encoding used for the output stream.
     *
     * @return The current character encoding (defaults to "UTF-8")
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Set the headers to use when writing out CSV values
     *
     * @param columns The column headers.
     */
    public void setHeaders(String... columns)
    {
        this.headers = columns;
    }

    /**
     * Returns the current headers that will be used when writing CSV data. If
     * headers were not explicitly set, they will be generated from the keys
     * found in the first record when writing begins.
     *
     * @return The headers used for CSV output.
     */
    public String[] getHeaders()
    {
        return this.headers;
    }

    /**
     * Returns the <code>text/csv</code> MIME type
     *
     * @return the MIME type
     */
    @Override
    public String getContentType()
    {
        return "text/csv";
    }

    /**
     * Creates a new writer to output CSV data to the specified target.
     *
     * @param target The target writeable output stream
     * @return An OutputCursor instance for writing CSV records
     */
    @Override
    public OutputCursor write(Target target)
    {
        return new CSVOutputCursor(target, headers);
    }

    private class CSVOutputCursor implements OutputCursor
    {

        private boolean initialized = false;

        private final PrintWriter out;
        private String[] headers;

        public CSVOutputCursor(Target target, String[] headers)
        {
            this.headers = headers;
            this.out = new PrintWriter(target.getOutputStream(), false, Charset.forName(encoding));
        }

        @Override
        public void write(JSONObject record)
        {
            if (!initialized) initialize(record);

            stringify(record);
            out.print("\r\n");
        }

        @Override
        public void close()
        {
            if (out != null) out.close();
        }

        private void initialize(JSONObject firstRecord)
        {
            if (initialized) return;

            if (headers == null)
            {
                headers = detectHeaders(firstRecord);
                stringify(headers);
            }
            else
            {
                stringify(headers);
            }
            
            out.print("\r\n");
            initialized = true;
        }

        private String[] detectHeaders(JSONObject record)
        {
            return record.keySet().toArray(String[]::new);
        }

        private void stringify(String[] columns)
        {
            boolean first = true;

            for (String header : columns)
            {
                if (!first) out.append(',');

                first = false;
                out.append(escapeAndQuoteValue(header));
            }
        }
        
        private void stringify(JSONObject record)
        {
            boolean first = true;
            Object value;
            String processed;

            for (String header : headers)
            {
                if (!first) out.append(',');

                first = false;
                value = record.get(header);

                processed = value == null ? "" : escapeAndQuoteValue(value.toString());

                out.append(processed);
            }
        }

        private String escapeAndQuoteValue(String value)
        {
            boolean needsQuoting = false;
            String evaluate = value;

            needsQuoting |= evaluate.contains(",");
            needsQuoting |= evaluate.contains("\"");
            needsQuoting |= evaluate.contains("\n");
            needsQuoting |= evaluate.contains("\r");

            if (needsQuoting) return "\"" + evaluate.replace("\"", "\"\"") + "\"";

            return evaluate;
        }

    }
}
