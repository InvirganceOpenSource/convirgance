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
import com.invirgance.convirgance.source.Source;
import com.invirgance.convirgance.transform.date.DateISOStringTransformer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A single SQL query for select, insert, and update operations. Can be passed
 * directly to {@link DBMS} when selecting data from the database. For inserts,
 * updates, and administrative calls use a {@link QueryOperation} to perform
 * the query in an atomic transaction.
 * <br><br>
 * Query provides named binding rather than the classic question mark 
 * binding. For example:
 * <br><br>
 * <code>select * from MY_TABLE where item = :item</code>
 * <br><br>
 * This allows values to be bound by name and even entire records to be bound
 * by key/value pair.
 * 
 * @author jbanes
 */
public class Query implements AtomicOperation
{
    private String sql;
    private JSONObject bindings;
    private ArrayList<Parameter> parameters;
    private ArrayList<Markup> markup;

    /**
     * Creates a new Query based on the provided SQL query
     * @param sql the SQL query
     */
    public Query(String sql)
    {
        this(sql, new JSONObject());
    }

    /**
     * Constructs a new Query object by parsing the provided SQL string and
     * binding values from the given {@link JSONObject} to the keys found in the
     * SQL string. Bind values must be in named form with a colon in front of the
     * name. For example:
     * <br><br>
     * <code>select * from MY_TABLE where item = :keyName</code>
     *
     * @param sql The SQL query which may include named placeholders in
     * the format <code>:key</code> to be replaced by values from the bindings
     * @param bindings A {@link JSONObject} containing key-value pairs where the
     * keys correspond to the placeholders in the SQL query and the values are
     * the data to be bound
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
    
    /**
     * Creates a new Query based on a SQL query read from the provided Source
     * 
     * @param sql a source to read the SQL query from
     */
    public Query(Source sql)
    {
        this(sql.readString());
    }
    
    /**
     * Constructs a new Query object by parsing a SQL string loaded from 
     * the provided Source and binding values from the given {@link JSONObject} 
     * to the keys found in the SQL string. Bind values must be in named form 
     * with a colon in front of the name. For example:
     * <br><br>
     * <code>select * from MY_TABLE where item = :keyName</code>
     *
     * @param sql The SQL query which may include named placeholders in
     * the format <code>:key</code> to be replaced by values from the bindings
     * @param bindings A {@link JSONObject} containing key-value pairs where the
     * keys correspond to the placeholders in the SQL query and the values are
     * the data to be bound
     */
    public Query(Source sql, JSONObject bindings)
    {
        this(sql.readString(), bindings);
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
        return "'" + DateISOStringTransformer.formatISO(date) + "'";
    }
    
    private String encodeList(List array)
    {
        StringBuilder buffer = new StringBuilder("(");
        
        for(Object value : array)
        {
            if(buffer.length() > 1) buffer.append(", ");
            
            buffer.append(encodeValue(value));
        }
        
        buffer.append(")");
        
        return buffer.toString();
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
        if(value instanceof List && isInjected((List)value)) return encodeList((List)value);

        return "?";
    }
    
    private boolean isInjected(List array)
    {
        for(Object value : array)
        {
            if(!isInjected(value)) return false;
        }
        
        return true;
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
        if(value instanceof List) return isInjected((List)value);

        return false;
    }

    /**
     * Get the original SQL query wrapped by this object
     * 
     * @return the original SQL query
     */
    public String getSQL()
    {
        return sql;
    }
    
    /**
     * Returns a JSONObject containing all key/value pair bindings set for this query
     * 
     * @return key/value pairs of set bindings
     */
    public JSONObject getBindings()
    {
        // Create a copy to prevent manipulation
        return new JSONObject(this.bindings); 
    }
    
    /**
     * Get the binding value set for the provided parameter name
     * 
     * @param parameter the parameter name for the desired binding
     * @return the value bound to the provided parameter. Null if no binding has been set.
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
     * Binds multiple values from a {@link JSONObject} to the parameters in the query
     * 
     * @param bindings a {@link JSONObject} containing key-value pairs to bind to query parameters
     * @throws ConvirganceException if the object contains a binding that has already been set
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
     * Exposes the parsing of the SQL query for named bindings. Markup will
     * include identified parameters and quoted strings in the SQL.
     * @return The markup values.
     */
    public Markup[] getMarkup()
    {
        return markup.toArray(Markup[]::new);
    }
    
    /**
     * Exposes the {@link Parameter} objects parsed from the SQL query. Each 
     * {@link Parameter} includes details such as the name, its index in the 
     * query, and the length of the parameter name.
     * 
     * @return An array of parameters with info relating to query details.
     */
    public Parameter[] getParameters()
    {
        return parameters.toArray(Parameter[]::new);
    }
    
    /**
     * An array containing the names of the bind parameters identified in the SQL
     * query.
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
     * Returns the modified SQL query that will be passed to the database for 
     * execution. Wherever possible, parameter values will be safely injected
     * into the SQL string to encourage higher-performance query plans. Values
     * that cannot be injected will be transformed into ? bind format with
     * offset values provided by {@link #getDatabaseBindings()}.
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

    /**
     * Convenience implementation of {@link AtomicOperation#execute(Connection)}
     * that initializes a QueryOperation with this Query and calls it.
     * 
     * @param connection a JDBC connection
     * @throws SQLException to stop the transaction and rollback the changes
     * @see QueryOperation
     */
    @Override
    public void execute(Connection connection) throws SQLException
    {
        new QueryOperation(this).execute(connection);
    }
    
    /**
     * Represents the location of a parameter in the SQL query. This markup is
     * used to identify the text needing replacement when generating a query to 
     * pass to the database.
     */
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
         * 
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
    
    /**
     * Represents a string identified in the SQL query. This can include both
     * single-quoted strings and database identifiers in double quotes.
     */
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
    
    /**
     * Base class for all markup of the SQL query. Represents a start location
     * and length.
     */
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
