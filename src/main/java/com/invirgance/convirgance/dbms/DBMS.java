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

import com.invirgance.convirgance.CloseableIterator;
import com.invirgance.convirgance.ConvirganceException;
import com.invirgance.convirgance.json.JSONObject;
import java.sql.*;
import java.util.Iterator;
import javax.sql.DataSource;

/**
 * Provides a standardized way to interact with a source database. Handling connections and committing data.
 * This class abstracts the database connection management and transaction handling for more efficient and 
 * consistent interaction with the database.
 * 
 * 
 * If you need to create custom query implementations, refer to the {@link AtomicOperation} interface to define 
 * your own batch or transaction operations.
 * @author jbanes
 */
public class DBMS
{
    private final DataSource source;
    
    /**
     * Creates a new instance of the DBMS with the specified DataSource. The
     * DataSource provides a connection pool or means to acquire database
     * connections. Methods/Operations will be executed against this connection.
     *
     * @param source The DataSource used to obtain database connections.
     */
    public DBMS(DataSource source)
    {
        this.source = source;
    }

    /**
     * @return Returns the current DataSource being used for database operations.
     */
    public DataSource getSource()
    {
        return source;
    }
    
    /**
     * Executes a query that returns an iterable of JSONObjects from the
     * results. This method is useful for comparing the resulting iterable
     * against another source or for processing the results dynamically in some
     * way.
     *
     * @param query object containing the SQL query string and bindings (if any)
     * to execute.
     * @return An {@link Iterable} of {@link JSONObject}, allowing iteration
     * over the query results.
     * @throws ConvirganceException If any SQL-related errors occur while
     * preparing or executing the query, or when attempting to retrieve the
     * results.
     *
     */
    public Iterable<JSONObject> query(Query query) throws ConvirganceException
    {
        return new Iterable<JSONObject>() {

            @Override
            public Iterator<JSONObject> iterator()
            {
                Connection connection;
                PreparedStatement statement;
                ResultSet set;
                int index = 1;

                try
                {
                    connection = source.getConnection();
                    statement = connection.prepareStatement(query.getDatabaseSQL());
                    
                    for(Object binding : query.getDatabaseBindings())
                    {
                        statement.setObject(index++, binding);
                    }
                    
                    set = statement.executeQuery();

                    return new SQLCursorIterator(connection, statement, set);
                }
                catch(SQLException e)
                {
                    throw new ConvirganceException(e);
                }
            }
        };
    }
    
    public void update(AtomicOperation transaction) throws ConvirganceException
    {
        Connection connection = null;
        
        try
        {
            connection = source.getConnection();
            
            connection.beginRequest();
            connection.setAutoCommit(false);
            
            transaction.execute(connection);
            
            connection.commit();
        }
        catch(Exception e)
        {
            try
            {
                connection.rollback();
            }
            catch(SQLException ex) { ex.printStackTrace(); }
            
            throw new ConvirganceException(e);
        }
        finally
        {
            if(connection != null)
            {
                try
                {
                    connection.setAutoCommit(true);
                    connection.endRequest();
                    connection.close();
                }
                catch(SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
    
    private static class SQLCursorIterator implements CloseableIterator<JSONObject>
    {
        private final Connection connection;
        private final Statement statement;
        private final ResultSet set;
        
        private boolean next;
        private String[] columns;

        public SQLCursorIterator(Connection connection, Statement statement, ResultSet set) throws SQLException
        {
            this.connection = connection;
            this.statement = statement;
            this.set = set;
            this.next = set.next();
            
            set.setFetchSize(1000);
        }

        @Override
        public boolean hasNext()
        {
            return next;
        }
        
        private void loadColumns(ResultSetMetaData meta) throws SQLException
        {
            columns = new String[meta.getColumnCount()];

            for(int i=0; i<columns.length; i++)
            {
                columns[i] = meta.getColumnLabel(i+1);
            }
        }

        @Override
        public JSONObject next()
        {
            JSONObject result = new JSONObject(true);
            
            try
            {
                if(columns == null) loadColumns(set.getMetaData());

                for(int i=0; i<columns.length; i++)
                {
                    result.put(columns[i], set.getObject(i+1));
                }
                
                this.next = set.next();
                
                if(!next) close();
                
                return result;
            }
            catch(SQLException e)
            {
                throw new ConvirganceException(e);
            }
        }

        @Override
        public void close() throws SQLException
        {
            try { set.close(); } catch(SQLException e) { e.printStackTrace(); }
            try { statement.close(); } catch(SQLException e) { e.printStackTrace(); }
            
            connection.close();
        }
        
    }
}
