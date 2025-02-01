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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Encapsulates a {@link Query} to ensure parameters are properly bound
 * before execution. This class implements {@link AtomicOperation} to perform
 * database operations within a transaction.
 *
 * @author jbanes
 */
public class QueryOperation implements AtomicOperation
{
    private Query query;

    /**
     * Creates a new QueryOperation without a query. This constructor exists 
     * for tools like IoC containers that will call {@link #setQuery(Query)} 
     * to set the query independent of the constructor.
     */
    public QueryOperation()
    {
    }
    
    /**
     * Creates a new QueryOperation wrapping the provided query.
     * 
     * @param query the query to wrap
     */
    public QueryOperation(Query query)
    {
        this.query = query;
    }

    /**
     * Get the wrapped query.
     * 
     * @return the query or null if no query has been set
     */
    public Query getQuery()
    {
        return query;
    }

    /**
     * Set the query to be wrapped in a transaction
     * 
     * @param query the SQL query
     */
    public void setQuery(Query query)
    {
        this.query = query;
    }
    
    /**
     * DO NOT CALL DIRECTLY. This is called by {@link DBMS} to execute the 
     * query as part of a transaction.
     *
     * @param connection an active JDBC connection
     * @throws SQLException if an error occurs will executing the query
     * @throws NullPointerException if the query has not been set
     * @see DBMS#update(AtomicOperation)
     */
    @Override
    public void execute(Connection connection) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement(query.getDatabaseSQL());
        Object[] bindings = query.getDatabaseBindings();
        
        for(int i=1; i<=bindings.length; i++)
        {
            statement.setObject(i, bindings[i-1]);
        }
        
        statement.execute();
    }
}
