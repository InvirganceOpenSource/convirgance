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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Primary interface for querying SQL-based database management 
 * systems (DBMS) like MySQL, PostgreSQL, SQL Server, Oracle, and others. Connection 
 * handling is automatic with a new connection being obtained from the DataSource for
 * each operation. A connection pool is recommended for query-heavy scenarios
 * like web application servers.
 * <br><br>
 * Application servers with JNDI registrations for the DataSource can use the
 * {@link #lookup(String)} API to pull the DataSource from the JNDI.
 * <br><br>
 * <code>var dbms = DMBS.lookup("jdbc/my-connection");</code>
 * <br><br>
 * 
 * @author jbanes
 */
public class DBMS
{
    private final DataSource source;
    private int fetchSize = Integer.parseInt(System.getProperty("convirgance.dbms.fetch.size", "1000"));
    
    /**
     * Creates a new instance of the DBMS with the specified DataSource. The
     * DataSource is the source of JDBC connections, thus the DataSource must 
     * be correctly configured before DBMS attempts to use it.
     *
     * @param source The DataSource used to obtain database connections.
     */
    public DBMS(DataSource source)
    {
        this.source = source;
    }

    /**
     * Returns the current fetch size. The default is 1000 records or the value
     * of the system property <code>convirgance.dbms.fetch.size</code> if set.
     * @return The number of records fetched per batch 
     */
    public int getFetchSize()
    {
        return fetchSize;
    }

    /**
     * Set the number of records to fetch per batch of the result set. Large 
     * numbers can negatively impact available memory, especially with wide
     * records. It can be advantageous to set a low number when querying wide
     * records.
     * 
     * @param fetchSize number of records per batch
     */
    public void setFetchSize(int fetchSize)
    {
        this.fetchSize = fetchSize;
    }
    
    /**
     * Attempts to retrieve a DataSource from the specified JNDI location. If
     * successful, returns a fully initialized DBMS instance. Returns null if the
     * lookup fails.
     * 
     * @param jndiPath a Java native directory path to a registered DataSource
     * @return an initialized DBMS instance if successful, null otherwise
     * @throws ConvirganceException if an error occurs while looking up the jndi path
     */
    public static DBMS lookup(String jndiPath)
    {
        Context context;
        DataSource source;
        
        try
        {
            context = new InitialContext();
            source = (DataSource)context.lookup(jndiPath);
            
            if(source != null) return new DBMS(source);
            
            // Tomcat prefixes java:/comp/env/ to database registrations
            source = (DataSource)context.lookup("java:/comp/env/" + jndiPath);
            
            if(source != null) return new DBMS(source);
            
            System.err.println("No DataSource configured at JNDI location " + jndiPath);
            
            return null;
        }
        catch(NamingException e)
        {
            throw new ConvirganceException(e);
        }
    }

    /**
     * Returns the current DataSource being used for database operations.
     * 
     * @return the DataSource object
     */
    public DataSource getSource()
    {
        return source;
    }
    
    /**
     * Executes the provided query and returns an Iterable stream of the
     * results.
     *
     * @param query object containing the SQL query string and bindings (if any)
     * to execute.
     * @return An {@link Iterable} of {@link JSONObject} records
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
                    
                    connection.setAutoCommit(false);
                    
                    statement = connection.prepareStatement(query.getDatabaseSQL(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
                    
                    statement.setFetchSize(fetchSize);
                    
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
    
    /**
     * Executes a transactional database operation using the provided
     * {@link AtomicOperation}. This method manages the lifecycle of the
     * {@link Connection}, including starting a transaction, committing it upon
     * success, and rolling it back in case of an exception.
     *
     * @param transaction the atomic operation to be executed. The operation
     * itself provides the logic for the database interaction.
     * @throws ConvirganceException If an error occurs during the operation.
     * including SQL errors or transaction failures, the exception is wrapped
     * and rethrown as a {@link ConvirganceException}.
     * @see QueryOperation
     * @see BatchOperation
     * @see TransactionOperation
     */
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
            try { connection.commit(); } catch(SQLException e) { e.printStackTrace(); }
            try { connection.setAutoCommit(true); } catch(SQLException e) { e.printStackTrace(); }
            
            connection.close();
        }
        
    }
}
