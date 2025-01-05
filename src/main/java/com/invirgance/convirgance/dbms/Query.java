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
 * Used for creating queries to be used against some DataSource. 
 * Use in conjunction with {@link QueryOperation} when modifying the DataSource in some way.
 * @author jbanes
 */
public class Query
{
    private String sql;
    private JSONObject bindings;
    private ArrayList<Parameter> parameters;
    private ArrayList<Markup> markup;

    /**
     * Creates a new Query based on the provided string.
     * @param sql The SQL query.
     */
    public Query(String sql)
    {
        this(sql, new JSONObject());
    }

    /**
     * Constructs a new Query object by parsing the provided SQL string and
     * binding values from the given {@link JSONObject} to the keys found in the
     * SQL string. This constructor initializes the SQL query, parses parameter
     * placeholders, and associates the provided bindings to their corresponding
     * placeholders in the query.
     *
     * @param sql The SQL query string, which may include named placeholders in
     * the format `:KEY` to be replaced by values from the bindings.
     * @param bindings A {@link JSONObject} containing key-value pairs where the
     * keys correspond to the placeholders in the SQL query and the values are
     * the data to be bound.
     */
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
                if(start+1 == i) markup.add(new Text("", start, (i-start+1)));
                else markup.add(new Text(sql.substring(start+1, i-1).replace("''", "'"), start, (i-start+1)));
                
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

    /**
     * Gets the current SQL query before parameters have been bound (if there are any).
     * 
     * @return The current SQL query.
     */
    public String getSQL()
    {
        return sql;
    }
    
    /**
     * A new JSONOBject based on the bindings from the current JSONOBject used to create the query.
     * 
     * @return A new JSONOBject copied from the JSONOBject in use.
     */
    public JSONObject getBindings()
    {
        // Create a copy to prevent manipulation
        return new JSONObject(this.bindings); 
    }
    
    /**
     * Get the binding value used for the provided parameter name.
     * 
     * @param parameter The parameter associated to a binding.
     * @return The value bound to the provided parameter as its Type (Object, Boolean, String, etc).
     */
    public Object getBinding(String parameter)
    {
        return this.bindings.get(parameter.toLowerCase());
    }
    

    /**
     * Binds a value to a named parameter in the query, overriding the existing
     * value if the parameter already exists. Useful for dynamically updating or
     * adding bindings in a query after it has been initialized.
     *
     * @param parameter The name of the parameter to override or add (Case-insensitive).
     * 
     * @param value The new value to associate with the parameter.
     *
     */
    public void setBinding(String parameter, Object value)
    {
        this.bindings.put(parameter.toLowerCase(), value);
    }
    
    /**
     * Binds multiple values from a {@link JSONObject} to the parameters in the query.
     * 
     * @param bindings A {@link JSONObject} containing key-value pairs to bind to query parameters.
     * @throws ConvirganceException If the current bindings contain a parameter found in the provided bindings.
     */
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
    
    /**
     * Gets the markup representation of the query.
     * @return The markup values.
     */
    public Markup[] getMarkup()
    {
        return markup.toArray(Markup[]::new);
    }
    
    /**
     * An array of {@link Parameter} objects representing the current
     * query parameters. Each {@link Parameter} includes details such as the
     * name, its index in the query, and the length of the parameter name.
     * 
     * @return An array of parameters with info relating to query details.
     */
    public Parameter[] getParameters()
    {
        return parameters.toArray(Parameter[]::new);
    }
    
    /**
     * An array containing the names of the parameters used for binding values.
     * 
     * @return An array of the parameters names.
     */
    public String[] getParameterNames()
    {
        ArrayList<String> list = new ArrayList<>();
        
        for(Parameter parameter : parameters)
        {
            if(!list.contains(parameter.name)) list.add(parameter.name);
        }
        
        return list.toArray(String[]::new);
    }
    
    /**
     * Returns the SQL query with parameter names replaced by their bound values from the JSONObject.
     *
     * @return The SQL query ready for execution.
     */
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
         * Returns the parameter name.
         * @return The name.
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
