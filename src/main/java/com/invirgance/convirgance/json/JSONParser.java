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

import java.io.*;

/**
 * Supports parsing a Stream containing JSON, processing values as their relevant data type.
 *
 * @author jbanes
 */
public class JSONParser implements AutoCloseable
{

    private Reader reader;
    private int next = -1;

    /**
     * Creates a JSONParser with the provided stream reader. 
     *
     * @param reader A reader with a stream, ex an InputStreamReader.
     */
    public JSONParser(Reader reader)
    {
        this.reader = reader;

        if (!reader.markSupported())
        {
            this.reader = new BufferedReader(reader, 16 * 1024);
        }
    }

    /**
     * Creates a JSONParser that will parse the provided JSON formatted string.
     *
     * @param json The JSON string to parse.
     */
    public JSONParser(String json)
    {
        this(new StringReader(json));
    }

    private char next() throws IOException
    {
        int c;
        
        if(next >= 0) 
        {
            c = next;
            next = -1;
            
            return (char)c;
        }
        
        c = reader.read();
        
        if(c < 0) throw new IOException("Reached end of readable stream");
        
        return (char)c;
    }

    private char nextPrintable() throws IOException
    {
        int c;
        
        if(next >= 0 && !Character.isWhitespace((char)next)) 
        {
            c = next;
            next = -1;
            
            return (char)c;
        }
        
        while(reader.ready())
        {
            c = reader.read();
            
            if(!Character.isWhitespace((char)c)) return (char)c;
        }
        
        throw new IOException("Reached end of readable stream without finding a non-whitespace character");
    }

    private char parseUnicode() throws IOException
    {
        StringBuilder buffer = new StringBuilder();
        char c;

        for (int i = 0; i < 4; i++)
        {
            c = next();

            buffer.append(c);
        }

        return (char) Integer.parseInt(buffer.toString(), 16);
    }

    private char peek() throws IOException
    {
        next = reader.read();

        return (char) next;
    }

    private char peekPrintable() throws IOException
    {
        int c;
        
        if(next >= 0) return (char)next;
        
        while(reader.ready())
        {
            next = reader.read();
            
            if(!Character.isWhitespace((char)next)) return (char)next;
        }
        
        throw new IOException("Reached end of readable stream without finding a non-whitespace character");
    }

    /**
     * Attempts to parse the JSON 'null' literal from the current position in
     * the reader.
     *
     * @return null if the literal 'null' is successfully parsed.
     * @throws IOException if the next 4 characters do not exactly match 'null'.
     */
    public Object parseNull() throws IOException
    {
        if (nextPrintable() != 'n' || next() != 'u' || next() != 'l' || next() != 'l')
        {
            throw new IOException("Unexpected content. Expected null.");
        }

        return null;
    }

    /**
     * Attempts to parse 'true' or 'false' from the current position in the
     * reader.
     *
     * @return the parsed Boolean value.
     * @throws IOException The current reader position does not start with 't'
     * or 'f'.
     */
    public Boolean parseBoolean() throws IOException
    {
        StringBuilder buffer = new StringBuilder();
        char c = nextPrintable();
        int count;
        
        if(c == 't') count = 3;
        else if(c == 'f') count = 4;
        else throw new IOException("Expected t or f but found " + c);
        
        buffer.append(c);
        
        for(int i=0; i<count; i++) buffer.append(next());
        
        return Boolean.valueOf(buffer.toString());
    }

    /**
     * Parses a JSON number from the current reader position. Will parse: -
     * Scientific Notation - Integer - Double
     *
     * @return The parsed Double or Integer.
     * @throws IOException If the number's format is invalid or end of input is
     * reached.
     */
    public Number parseNumber() throws IOException
    {
        StringBuilder buffer = new StringBuilder();
        
        boolean digits = false;
        boolean floating = false;
        
        char c = peekPrintable();
        Long number;
        
        if(c == '-')
        {
            buffer.append(c);
            
            c = peek();
        }
        
        while(reader.ready())
        {   
            if(Character.isDigit(c))
            {
                buffer.append(c);
                
                digits = true;
                c = peek();
                
                continue;
            }
            
            if(c == '.')
            {
                if(!digits || floating) throw new IOException("Invalid number format: " + buffer + ".");
                
                buffer.append(c);
                
                floating = true;
                c = peek();
                
                continue;
            }
            
            if(c == 'e' || c == 'E')
            {
                if(!digits) throw new IOException("Invalid number format: " + buffer + ".");
                
                buffer.append(c);
                
                floating = true;
                c = peek();
                
                if(c != '+' && c != '-' && !Character.isDigit(c)) throw new IOException("Expected + or - but found " + c);
                
                buffer.append(c);
                
                c = peek();
                
                continue;
            }
            
            if(!digits) throw new IOException("Invalid number format: " + buffer);
            
            if(floating) return Double.valueOf(buffer.toString());
            
            number = Long.valueOf(buffer.toString());
            
            if(number == number.intValue()) return number.intValue();
            
            return number;
        }
        
        throw new IOException("Reached end of stream before parsing completed");
    }

    /**
     * Parses a JSON from the current reader position escaping unsafe characters when required.
     *
     * @return The parsed string.
     * @throws IOException When the string doesn't begin with a quote.
     * @throws IOException On invalid escape sequences.
     * @throws IOException If the end of the stream is reached before
     * encountering an end quote.
     */
    public String parseString() throws IOException
    {
        StringBuilder buffer = new StringBuilder();
        char c = nextPrintable();
        
        if(c != '"') throw new IOException("Expected \" but found " + c);
        
        while(reader.ready())
        {
            c = next();
            
            if(c == '"') return buffer.toString();
            
            if(c != '\\')
            {
                buffer.append((char)c);
                continue;
            }
            
            c = next();
            
            switch(c)
            {
                case '"':
                    buffer.append('"');
                    break;
                    
                case '\\':
                    buffer.append('\\');
                    break;
                    
                case '/':
                    buffer.append('/');
                    break;
                    
                case 'b':
                    buffer.append('\b');
                    break;
                    
                case 'f':
                    buffer.append('\f');
                    break;
                    
                case 'n':
                    buffer.append('\n');
                    break;
                    
                case 'r':
                    buffer.append('\r');
                    break;
                    
                case 't':
                    buffer.append('\t');
                    break;
                    
                case 'u':
                    buffer.append(parseUnicode());
                    break;
                    
                default:
                    throw new IOException("Unexpected string escape \\" + c);
            }
        }
        
        throw new IOException("Reached end of stream before parsing completed");
    }

    /**
     * Parses a JSON object from the current reader position, with ordering. 
     * Handles string parsing and character escaping.
     *
     * @return A ordered JSONObject based on the key value pairs encountered by
     * the readers stream.
     * @throws IOException If JSON is malformed or stream ends unexpectedly.
     */
    public JSONObject parseObject() throws IOException
    {
        JSONObject object = new JSONObject(true);
        String key;
        
        char c = nextPrintable();
        
        if(c != '{') throw new IOException("Expected {, but found " + c);
        
        while(reader.ready())
        {
            c = peekPrintable();
            
            switch(c)
            {
                case '"':
                    key = parseString();
                    break;
                    
                case '}':
                    nextPrintable();
                    return object;
                    
                default:
                    throw new IOException("Expected \" or }, but found " + c);
            }
            
            c = nextPrintable();
            
            if(c != ':') throw new IOException("Expected : but found " + c);
            
            object.put(key, parse());
            
            c = nextPrintable();
            
            if(c == '}')
            {
                return object;
            }
            
            if(c != ',')  throw new IOException("Expected , but found " + c + " (0x" + Integer.toHexString(c & 0xFF) + ")");
        }
        
        throw new IOException("Reached end of stream before parsing completed");
    }

    /**
     * Parses a JSON array from the current reader position. Looping through any
     * values in the array and parsing them to their relevant types and escaping characters when required.
     * Ex:
     *  '[
     *      "tag1", -> parseString()
     *      2 -> parseInt()
     * ]'
     * 
     * @return A JSONArray containing parsed values (if any).
     * @throws IOException If JSON is malformed or stream ends unexpectedly.
     */
    public JSONArray parseArray() throws IOException
    {
        JSONArray array = new JSONArray();
        String key;
        
        char c = nextPrintable();
        
        if(c != '[') throw new IOException("Expected [ but found " + c);
        
        while(reader.ready())
        {
            c = peekPrintable();
            
            if(c == ']')
            {
                nextPrintable();
                return array;
            }
            
            array.add(parse());
            
            c = nextPrintable();

            if(c == ']')
            {
                return array;
            }
            
            if(c != ',') throw new IOException("Expected , but found " + c);
        }
        
        throw new IOException("Reached end of stream before parsing completed");
    }

    /**
     * Parses the JSON and returns its relevant type.
     *
     * @return JSONObject for objects, JSONArray for arrays, String for strings,
     * Double or Int for numbers, Boolean for booleans, or null.
     * @throws IOException If JSON is malformed or stream ends unexpectedly.
     */
    public Object parse() throws IOException
    {
        char c = peekPrintable();
        
        if(c == '{') return parseObject();
        if(c == '[') return parseArray();
        if(c == '"') return parseString();
        if(c == 'n') return parseNull();
        
        if(c == 't' || c == 'f') return parseBoolean();
        if(c == '-' || Character.isDigit(c)) return parseNumber();
        
        //TODO: String, number, boolean, null
        
        throw new IOException("Unrecognized character: " + c);
    }

    @Override
    public void close() throws Exception
    {
        this.reader.close();
    }

}
