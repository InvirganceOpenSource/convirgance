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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jbanes
 */
public class JSONWriterTest
{

    @Test
    public void testClosable() throws Exception
    {
        final JSONObject object = new JSONObject();

        StringWriter writer = new StringWriter()
        {
            @Override
            public void close() throws IOException
            {
                object.put("closed", true);
            }
        };

        assertFalse(object.getBoolean("closed", false));

        try (JSONWriter json = new JSONWriter(writer))
        {
            assertEquals(writer, json.getWriter());
        }

        assertTrue(object.getBoolean("closed", false));
    }

    @Test
    public void testWriteString() throws Exception
    {
        assertEquals("\"\"", new JSONWriter(new StringWriter()).write("").getWriter().toString());
        assertEquals("\"Hello World!\"", new JSONWriter(new StringWriter()).write("Hello World!").getWriter().toString());
        assertEquals("\"Hello\\nWorld!\"", new JSONWriter(new StringWriter()).write("Hello\nWorld!").getWriter().toString());
        assertEquals("\"Hello\\r\\nWorld!\"", new JSONWriter(new StringWriter()).write("Hello\r\nWorld!").getWriter().toString());
        assertEquals("\"Hello\\tWorld!\"", new JSONWriter(new StringWriter()).write("Hello\tWorld!").getWriter().toString());
        assertEquals("\"\\\"Hello\\\" World!\"", new JSONWriter(new StringWriter()).write("\"Hello\" World!").getWriter().toString());
        assertEquals("\"Hello\\\\World!\"", new JSONWriter(new StringWriter()).write("Hello\\World!").getWriter().toString());
        assertEquals("\"Hello\\bWorld!\"", new JSONWriter(new StringWriter()).write("Hello\bWorld!").getWriter().toString());
        assertEquals("\"Hello\\fWorld!\"", new JSONWriter(new StringWriter()).write("Hello\fWorld!").getWriter().toString());
        assertEquals("\"Hello\\u0007World!\"", new JSONWriter(new StringWriter()).write("Hello\u0007World!").getWriter().toString());
    }

    @Test
    public void testWriteNumber() throws Exception
    {
        assertEquals("0", new JSONWriter(new StringWriter()).write(0).getWriter().toString());
        assertEquals("0.0", new JSONWriter(new StringWriter()).write(0.0).getWriter().toString());
        assertEquals("-0.0", new JSONWriter(new StringWriter()).write(-0.0).getWriter().toString());

        assertEquals("0", new JSONWriter(new StringWriter()).write(Byte.decode("0")).getWriter().toString());
        assertEquals("0", new JSONWriter(new StringWriter()).write(Short.decode("0")).getWriter().toString());
        assertEquals("0", new JSONWriter(new StringWriter()).write(Integer.decode("0")).getWriter().toString());
        assertEquals("0", new JSONWriter(new StringWriter()).write(Long.decode("0")).getWriter().toString());
        assertEquals("0.0", new JSONWriter(new StringWriter()).write(Float.valueOf("0.0")).getWriter().toString());
        assertEquals("0.0", new JSONWriter(new StringWriter()).write(Double.valueOf("0.0")).getWriter().toString());
        assertEquals("0", new JSONWriter(new StringWriter()).write(new BigInteger("0")).getWriter().toString());
        assertEquals("0.0", new JSONWriter(new StringWriter()).write(new BigDecimal("0.0")).getWriter().toString());

        assertEquals("-1", new JSONWriter(new StringWriter()).write(-1).getWriter().toString());
        assertEquals("-123", new JSONWriter(new StringWriter()).write(-123).getWriter().toString());
        assertEquals("-1.23", new JSONWriter(new StringWriter()).write(-1.23).getWriter().toString());
        assertEquals("-123.45", new JSONWriter(new StringWriter()).write(-123.45).getWriter().toString());

        assertEquals("1", new JSONWriter(new StringWriter()).write(1).getWriter().toString());
        assertEquals("123", new JSONWriter(new StringWriter()).write(123).getWriter().toString());
        assertEquals("1.23", new JSONWriter(new StringWriter()).write(1.23).getWriter().toString());
        assertEquals("123.45", new JSONWriter(new StringWriter()).write(123.45).getWriter().toString());
    }

    @Test
    public void testWriteNull() throws Exception
    {
        assertEquals("null", new JSONWriter(new StringWriter()).writeNull().getWriter().toString());
    }

    @Test
    public void testWriteJSONObject() throws Exception
    {
        JSONObject test = new JSONObject();
        JSONObject object = new JSONObject();

        assertEquals("{}", new JSONWriter(new StringWriter()).write(test).getWriter().toString());
        assertEquals("{}", new JSONWriter(new StringWriter(), 4).write(test).getWriter().toString());

        test.put("message", "Hello world!");

        assertEquals("{\"message\":\"Hello world!\"}", new JSONWriter(new StringWriter()).write(test).getWriter().toString());
        assertEquals("{\"message\":\"Hello world!\"}", new JSONWriter(new StringWriter()).write(test).getWriter().toString());

        test.put("count", 5);

        assertEquals("{\"count\":5,\"message\":\"Hello world!\"}", new JSONWriter(new StringWriter()).write(test).getWriter().toString());
        assertEquals("{\n    \"count\": 5,\n    \"message\": \"Hello world!\"\n}", new JSONWriter(new StringWriter(), 4).write(test).getWriter().toString());

        test.put("empty", new JSONObject());

        assertEquals("{\"count\":5,\"message\":\"Hello world!\",\"empty\":{}}", new JSONWriter(new StringWriter()).write(test).getWriter().toString());
        assertEquals("{\n    \"count\": 5,\n    \"message\": \"Hello world!\",\n    \"empty\": {}\n}", new JSONWriter(new StringWriter(), 4).write(test).getWriter().toString());

        object.put("message", "Hello world!");
        test.put("object", object);

        assertEquals("{\"count\":5,\"message\":\"Hello world!\",\"empty\":{},\"object\":{\"message\":\"Hello world!\"}}", new JSONWriter(new StringWriter()).write(test).getWriter().toString());
        assertEquals("{\n    \"count\": 5,\n    \"message\": \"Hello world!\",\n    \"empty\": {},\n    \"object\": {\n        \"message\": \"Hello world!\"\n    }\n}", new JSONWriter(new StringWriter(), 4).write(test).getWriter().toString());

        object.put("count", 2);

        assertEquals("{\"count\":5,\"message\":\"Hello world!\",\"empty\":{},\"object\":{\"count\":2,\"message\":\"Hello world!\"}}", new JSONWriter(new StringWriter()).write(test).getWriter().toString());
        assertEquals("{\n    \"count\": 5,\n    \"message\": \"Hello world!\",\n    \"empty\": {},\n    \"object\": {\n        \"count\": 2,\n        \"message\": \"Hello world!\"\n    }\n}", new JSONWriter(new StringWriter(), 4).write(test).getWriter().toString());

    }

    @Test
    public void testWriteArray() throws Exception
    {
        JSONArray test = new JSONArray();
        JSONArray array = new JSONArray();

        assertEquals("[]", new JSONWriter().write(test).toString());
        assertEquals("[]", new JSONWriter(4).write(test).toString());

        test.add("Hello world!");

        assertEquals("[\"Hello world!\"]", new JSONWriter().write(test).toString());
        assertEquals("[\n    \"Hello world!\"\n]", new JSONWriter(4).write(test).toString());

        test.add(5);

        assertEquals("[\"Hello world!\",5]", new JSONWriter().write(test).toString());
        assertEquals("[\n    \"Hello world!\",\n    5\n]", new JSONWriter(4).write(test).toString());

        test.add(new JSONArray());

        assertEquals("[\"Hello world!\",5,[]]", new JSONWriter().write(test).toString());
        assertEquals("[\n    \"Hello world!\",\n    5,\n    []\n]", new JSONWriter(4).write(test).toString());

        array.add(1);
        array.add(2);
        array.add(3);
        test.add(array);

        assertEquals("[\"Hello world!\",5,[],[1,2,3]]", new JSONWriter().write(test).toString());
        assertEquals("[\n    \"Hello world!\",\n    5,\n    [],\n    [\n        1,\n        2,\n        3\n    ]\n]", new JSONWriter(4).write(test).toString());
    }

    @Test
    public void testWriteObject() throws Exception
    {
        assertEquals("null", new JSONWriter().write((Object) null).toString());
        assertEquals("\"Hello world!\"", new JSONWriter().write((Object) "Hello world!").toString());
        assertEquals("1337", new JSONWriter().write((Object) 1337).toString());
        assertEquals("{}", new JSONWriter().write((Object) new JSONObject()).toString());
        assertEquals("[]", new JSONWriter().write((Object) new JSONArray()).toString());
    }
    
    @Test
    public void testWriteDate() throws Exception 
    {
        Object[][] dates = {
            { new Date(), null },
            { new Date(0L), "1970-01-01" },
            { new Date(32503680000000L), "3000-01-01" },
            { new Date(-62167219200000L), "0000-01-01" },
            { Date.from(Instant.parse("2024-02-18T12:30:45.123Z")), "2024-02-18T12:30:45.123Z" },
            { Date.from(Instant.parse("2025-02-19T00:00:00Z")), "2025-02-19" },
            { Date.from(Instant.parse("2025-02-19T14:45:30Z")), "2025-02-19T14:45:30Z" },
            { Date.from(Instant.parse("2024-02-19T15:30:00Z")), "2024-02-19T15:30:00Z" },               
            { Date.from(Instant.parse("2024-02-19T15:30:00.1Z")), "2024-02-19T15:30:00.100Z" },         
            { Date.from(Instant.parse("2024-02-19T15:30:00.01Z")), "2024-02-19T15:30:00.010Z" },       
            { Date.from(Instant.parse("2024-02-19T15:30:00.001Z")), "2024-02-19T15:30:00.001Z" },       
            { Date.from(Instant.parse("2024-02-19T15:30:00.111111Z")), "2024-02-19T15:30:00.111Z" },   
            { Date.from(Instant.parse("2024-02-19T15:30:00.111111111Z")), "2024-02-19T15:30:00.111Z" }, 
            { Date.from(Instant.parse("2024-02-19T15:30:00.999Z")), "2024-02-19T15:30:00.999Z" },       
            { Date.from(Instant.parse("2024-02-19T15:30:00.9999Z")), "2024-02-19T15:30:00.999Z" },      
            { Date.from(Instant.parse("1900-01-01T00:00:00Z")), "1900-01-01" },
            { Date.from(Instant.parse("2079-06-06T23:59:00Z")), "2079-06-06T23:59:00Z" }
        };

        Date test;
        String expected;
        JSONWriter writer;
        String result;
        Instant instant;
        
        for(Object[] date : dates) 
        {
            test = (Date) date[0];
            expected = (String) date[1];
                       
            writer = new JSONWriter();

            if(expected == null) 
            {
                instant = test.toInstant();
                
                if(instant.atZone(ZoneOffset.UTC).toLocalTime().equals(java.time.LocalTime.MIDNIGHT)) 
                {
                    expected = instant.atZone(ZoneOffset.UTC).toLocalDate().toString(); 
                } 
                else 
                {
                    expected = instant.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT); 
                }
            }

            writer.write(test);
            result = writer.toString().replace("\"", ""); // Remove quotes for comparison
            assertEquals(expected, result, "Failed for date: " + test);
        }
    }


}
