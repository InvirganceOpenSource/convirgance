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

import com.invirgance.convirgance.source.ByteArraySource;
import java.io.InputStreamReader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class JSONParserTest
{
    
    @Test
    public void testParseNull() throws Exception
    {
        assertNull(new JSONParser("null").parseNull());
        assertNull(new JSONParser("\t  null  \t").parseNull());
    }
    
    @Test
    public void testParseBoolean() throws Exception
    {
        assertTrue(new JSONParser("true").parseBoolean());
        assertFalse(new JSONParser("false").parseBoolean());
        
        assertTrue((Boolean)new JSONParser("true").parse());
        assertFalse((Boolean)new JSONParser("false").parse());
        
        assertTrue((Boolean)new JSONParser("    \ttrue").parse());
        assertFalse((Boolean)new JSONParser("    \tfalse").parse());
    }
    
    @Test
    public void testParseNumber() throws Exception
    {
        assertEquals(0, new JSONParser("0").parseNumber());
        assertEquals(1, new JSONParser("1").parseNumber());
        assertEquals(-1, new JSONParser("-1").parseNumber());
        assertEquals(1234567, new JSONParser("1234567").parseNumber());
        assertEquals(-1234567, new JSONParser("-1234567").parseNumber());
        assertEquals(2147483649l, new JSONParser("2147483649").parseNumber());
        assertEquals(-2147483649l, new JSONParser("-2147483649").parseNumber());
        
        assertEquals(0, new JSONParser("0").parse());
        assertEquals(1, new JSONParser("1").parse());
        assertEquals(-1, new JSONParser("-1").parse());
        assertEquals(1234567, new JSONParser("1234567").parse());
        assertEquals(-1234567, new JSONParser("-1234567").parse());
        assertEquals(2147483649l, new JSONParser("2147483649").parse());
        assertEquals(-2147483649l, new JSONParser("-2147483649").parse());
        
        assertEquals(0.0, new JSONParser("0.0").parseNumber());
        assertEquals(1.0, new JSONParser("1.0").parseNumber());
        assertEquals(-1.0, new JSONParser("-1.0").parseNumber());
        assertEquals(1.23, new JSONParser("1.23").parseNumber());
        assertEquals(-1.23, new JSONParser("-1.23").parseNumber());
        
        assertEquals(6E5, new JSONParser("6E5").parse());
        assertEquals(6E+5, new JSONParser("6E+5").parse());
        assertEquals(6E-5, new JSONParser("6E-5").parse());
        assertEquals(-6E5, new JSONParser("-6E5").parse());
        assertEquals(-6E+5, new JSONParser("-6E+5").parse());
        assertEquals(-6E-5, new JSONParser("-6E-5").parse());
        assertEquals(-62E-51, new JSONParser("-62e-51").parse());
        assertEquals(-62E+51, new JSONParser("-62e+51").parse());
        assertEquals(-62E51, new JSONParser("-62e51").parse());
    }

    @Test
    public void testParseString() throws Exception
    {
        assertEquals("Hello world!", new JSONParser("\"Hello world!\"").parseString());
        assertEquals("\"Hello\" world!", new JSONParser("\"\\\"Hello\\\" world!\"").parseString());
        assertEquals("Hello\bworld!", new JSONParser("\"Hello\\bworld!\"").parseString());
        assertEquals("Hello\fworld!", new JSONParser("\"Hello\\fworld!\"").parseString());
        assertEquals("Hello\nworld!", new JSONParser("\"Hello\\nworld!\"").parseString());
        assertEquals("Hello\rworld!", new JSONParser("\"Hello\\rworld!\"").parseString());
        assertEquals("Hello\tworld!", new JSONParser("\"Hello\\tworld!\"").parseString());
        assertEquals("Hello\u0007world!", new JSONParser("\"Hello\\u0007world!\"").parseString());
        assertEquals("Hello\\world!", new JSONParser("\"Hello\\\\world!\"").parseString());
        assertEquals("Hello/world!", new JSONParser("\"Hello\\/world!\"").parseString());
        
        assertEquals("Hello world!", new JSONParser("\"Hello world!\"").parse());
        assertEquals("Hello world!", new JSONParser("   \r\n\t\"Hello world!\"").parse());
    }
    
    @Test
    public void testParseObject() throws Exception
    {
        JSONObject record;
        ByteArraySource source;
        
        assertEquals(0, new JSONParser("{}").parseObject().size());
        
        record = new JSONParser("{\"message\": \"Hello world!\"}").parseObject();
        
        assertEquals(1, record.size());
        assertEquals("Hello world!", record.get("message"));
        
        record = new JSONParser("{\"message\": \"Hello world!\", \"count\": 5}").parseObject();
        
        assertEquals(2, record.size());
        assertEquals("Hello world!", record.get("message"));
        assertEquals(5, record.get("count"));
        
        record = new JSONParser("{\n" +
        "    \"databases\": [\n" +
        "        {\n" +
        "            \"name\": \"TestDatabase\",\n" +
        "            \"location\": \"/Users/jbanes/Development/divirgance/target/testing/system/initialization/TestDatabase\",\n" +
        "            \"type\": \"com.invirgance.divirgance.derby.DerbyAnalyticDatabase\"\n" +
        "        }\n" +
        "    ]\n" +
        "}\n" +
        "").parseObject();
        
        assertEquals(1, record.getJSONArray("databases").size());
        
        // Testing edge condition in parseNumber that causes an early stream termination
        source = new ByteArraySource("{\"id\":12}".getBytes("UTF-8"));
        record = new JSONParser(new InputStreamReader(source.getInputStream())).parseObject();

        assertEquals(1, record.size());
        assertEquals(12, record.get("id"));
    }
    
    @Test
    public void testParseArray() throws Exception
    {
        JSONArray array;
        
        assertEquals(0, new JSONParser("[]").parseArray().size());
        
        array = new JSONParser("[\"Hello world!\"]").parseArray();
        
        assertEquals(1, array.size());
        assertEquals("Hello world!", array.get(0));
        
        array = new JSONParser("[\"Hello world!\", 5]").parseArray();
        
        assertEquals(2, array.size());
        assertEquals("Hello world!", array.get(0));
        assertEquals(5, array.get(1));
    }
    
}
