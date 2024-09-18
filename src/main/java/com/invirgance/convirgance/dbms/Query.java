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
package com.invirgance.convirgance.dbms;

import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jbanes
 */
public class Query
{
    private String sql;
    private JSONObject bindings;
    private ArrayList<Parameter> parameters;
    private ArrayList<Markup> markup;

    public Query(String sql)
    {
        this(sql, new JSONObject());
    }

    public Query(String sql, JSONObject bindings)
    {
        this.sql = sql;
        this.bindings = new JSONObject();
        this.parameters = new ArrayList<>();
        this.markup = new ArrayList<>();
        
        parseParameters();
        setBindings(bindings);
    }
    
    private int countString(int start)
    {
        char c;
        
        for(int i=start+1; i<sql.length(); i++)
        {
            c = sql.charAt(i);
            
            if(c == '\'' && sql.length() > i+1 && sql.charAt(i+1) == '\'')
            {
                i++;
                continue;
            }
            
            if(c == '\'')
            {
                markup.add(new Text(sql.substring(start+1, i-1).replace("''", "'"), start, (i-start+1)));
                
                return (i-start);
            }
        }
        
        throw new ConvirganceException("Unterminated string in sql starting at position " + start + " in sql: [" + sql + "]");
    }
    
    private void parseParameters()
    {
        StringBuilder buffer = new StringBuilder();
        Parameter parameter;
        char c;
        
        for(int i=0; i<sql.length(); i++)
        {
            c = sql.charAt(i);
            
            if(buffer.length() > 0)
            {
                if(Character.isLetterOrDigit(c) || c == '_' || c == '-')
                {
                    buffer.append(c);
                    continue;
                }
                else
                {
                    if(buffer.charAt(0) == ':' && buffer.length() > 1)
                    {
                        parameter = new Parameter(buffer.substring(1), i-buffer.length(), buffer.length());
                        
                        parameters.add(parameter);
                        markup.add(parameter);
                    }
                    
                    buffer.setLength(0);
                }
            }
            
            if(c == ':' || Character.isLetterOrDigit(c))
            {
                buffer.append(c);
            }
            else if(c == '\'')
            {
                i += countString(i);
            }
        }
        
        if(buffer.length() > 1 && buffer.charAt(0) == ':')
        {
            parameter = new Parameter(buffer.substring(1), sql.length()-buffer.length(), buffer.length());
                
            parameters.add(parameter);
            markup.add(parameter);
        }
    }
    
    private String encodeString(String value)
    {
        return "'" + value.replace("'", "''") + "'";
// TODO: Detect and use nvarchar when appropriate
//        return "n'" + value.replace("'", "''") + "'";
    }
    
    private String encodeDate(Date date)
    {
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();
        
        return "'" + year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day + "'";
    }
    
    private String encodeTime(Date date)
    {
        int hour = date.getHours();
        int minutes = date.getMinutes();
        int seconds = date.getSeconds();
        
        return "'" + (hour < 10 ? "0" : "") + hour + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds + "'";
    }
    
    private String encodeDateTime(Date date)
    {
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();
        
        int hour = date.getHours();
        int minutes = date.getMinutes();
        int seconds = date.getSeconds();
        
        String result = year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
        String time = (hour < 10 ? "0" : "") + hour + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        
        return "'" + result + " " + time + "'";
    }
    
    private String encodeValue(Object value)
    {
        if(value == null) return "null";
        if(value instanceof String && ((String)value).length() < 256) return encodeString((String)value);
        if(value instanceof Number) return value.toString();
        if(value instanceof Boolean) return value.toString().toUpperCase();
        if(value instanceof java.sql.Date) return encodeDate((Date)value);
        if(value instanceof java.sql.Time) return encodeTime((Date)value);
        if(value instanceof java.sql.Timestamp) return encodeDateTime((Date)value);
        if(value instanceof Date) return encodeDateTime((Date)value);
        if(value instanceof Calendar) return encodeDateTime(((Calendar)value).getTime());

        return "?";
    }
    
    private boolean isInjected(Object value)
    {
        if(value == null) return true;
        if(value instanceof String && ((String)value).length() < 256) return true;
        if(value instanceof Number) return true;
        if(value instanceof Boolean) return true;
        if(value instanceof java.sql.Date) return true;
        if(value instanceof java.sql.Time) return true;
        if(value instanceof java.sql.Timestamp) return true;
        if(value instanceof Date) return true;
        if(value instanceof Calendar) return true;

        return false;
    }

    public String getSQL()
    {
        return sql;
    }
    
    public JSONObject getBindings()
    {
        // Create a copy to prevent manipulation
        return new JSONObject(this.bindings); 
    }
    
    public Object getBinding(String parameter)
    {
        return this.bindings.get(parameter.toLowerCase());
    }
    
    public void setBinding(String parameter, Object value)
    {
        this.bindings.put(parameter.toLowerCase(), value);
    }
    
    public void setBindings(JSONObject bindings)
    {   
        for(String parameter : bindings.keySet())
        {
            if(this.bindings.containsKey(parameter.toLowerCase()))
            {
                throw new ConvirganceException("Duplicate binding for bound name: " + parameter);
            }
            
            this.bindings.put(parameter.toLowerCase(), bindings.get(parameter));
        }
    }
    
    public Markup[] getMarkup()
    {
        return markup.toArray(Markup[]::new);
    }
    
    public Parameter[] getParameters()
    {
        return parameters.toArray(Parameter[]::new);
    }
    
    public String[] getParameterNames()
    {
        ArrayList<String> list = new ArrayList<>();
        
        for(Parameter parameter : parameters)
        {
            if(!list.contains(parameter.name)) list.add(parameter.name);
        }
        
        return list.toArray(String[]::new);
    }
    
    public String getDatabaseSQL()
    {
        StringBuilder builder = new StringBuilder();
        int start = 0;
        
        for(Parameter parameter : parameters)
        {
            builder.append(sql.substring(start, parameter.getStart()));
            builder.append(encodeValue(bindings.get(parameter.getName().toLowerCase())));
            
            start = parameter.getStart() + parameter.getLength();
        }
        
        builder.append(sql.substring(start, sql.length()));
        
        return builder.toString();
    }
    
    /**
     * List of values that cannot be injected and need to be bound at the 
     * driver level. These values will replace any ? values in the database
     * query.
     * 
     * @see getDatabaseSQL
     * @return List of bind values
     */
    public Object[] getDatabaseBindings()
    {
        ArrayList list = new ArrayList();
        Object value;
        
        for(Parameter parameter : parameters)
        {
            value = getBinding(parameter.getName());
            
            if(!isInjected(value)) list.add(value);
        }
        
        return list.toArray(Object[]::new);
    }
    
    public static class Parameter extends Markup
    {
        private String name;

        private Parameter(String name, int start, int length)
        {
            super(start, length);
            
            this.name = name;
        }

        /**
         * 
         * @return 
         */
        public String getName()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return "Parameter[" + name + "," + getStart() + "," + getLength() + "]";
        }
    }
    
    public static class Text extends Markup
    {
        private String value;

        private Text(String value, int start, int length)
        {
            super(start, length);
            
            this.value = value;
        }

        /**
         * Decodes the underlying string value
         * 
         * @return decoded string value
         */
        public String getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return "Text[" + value + "," + getStart() + "," + getLength() + "]";
        }
    }
    
    public static class Markup
    {
        private final int start;
        private final int length;

        private Markup(int start, int length)
        {
            this.start = start;
            this.length = length;
        }
        
        /**
         * The starting position of the token in the SQL
         * 
         * @return Position of the token
         */
        public int getStart()
        {
            return start;
        }

        /**
         * This is the length of the token in SQL. This will not necessarily
         * match the length of the underlying value due to SQL encoding.
         * 
         * @return The length of the token to replace in the underlying SQL
         */
        public int getLength()
        {
            return length;
        }

        @Override
        public String toString()
        {
            return "Markup[" + start + "," + length + "]";
        }
    }
}
