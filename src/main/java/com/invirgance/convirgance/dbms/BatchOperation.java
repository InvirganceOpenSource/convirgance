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

import com.invirgance.convirgance.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Bulk insert or update a stream of records in a single transaction. This uses the JDBC
 * batching APIs for maximum performance.
 * <br><br>
 * Note that transaction atomicity may be violated in the case of large loads. By
 * default a commit is triggered every 1,000 records to prevent an overflow of
 * the transaction buffer. A failure after 1,000 records have been inserted may
 * leave the database in an inconsistent state and thus should be planned for.
 * 
 * @author jbanes
 */
public class BatchOperation implements AtomicOperation
{
    private Query query;
    private Iterable<JSONObject> records;
    private int commit = 10000;

    /**
     * Default constructor for BatchOperation. Initializes an empty
     * BatchOperation without a predefined query or records.
     */
    public BatchOperation()
    {
    }
    
    /**
     * Constructs a BatchOperation with the specified query. This query will be
     * used to perform batch operations.
     *
     * @param query The query to be executed in batch operations.
     */
    public BatchOperation(Query query)
    {
        this.query = query;
    }

    /**
     * This constructor allows specifying both the query to be executed and the
     * records to be processed with the query.
     *
     * @param query The query to be executed in batch operations.
     * @param records The records to be processed in the batch.
     */
    public BatchOperation(Query query, Iterable<JSONObject> records)
    {
        this.query = query;
        this.records = records;
    }

    /**
     * Gets the query to be executed across the records.
     * 
     * @return The batch SQL query. Null if the query has not yet been set.
     */
    public Query getQuery()
    {
        return query;
    }
    
    /**
     * Sets the batch SQL query that will be used by the operation
     * 
     * @param query The query.
     */
    public void setQuery(Query query)
    {
        this.query = query;
    }

    /**
     * Returns the stream of records that will be used by this BatchOperation 
     * during execution
     * 
     * @return The stream used for the operation. Null if the stream has not yet been set.
     */
    public Iterable<JSONObject> getRecords()
    {
        return records;
    }

    /**
     * Sets the records that will be inserted or updated during execution.
     * 
     * @param records The JSONObjects to use.
     */
    public void setRecords(Iterable<JSONObject> records)
    {
        this.records = records;
    }
    
    /**
     * Returns the current auto commit interval used when processing the 
     * transaction. A commit will be triggered after this number of inserts or
     * updates. The default commit interval is 1,000.
     * 
     * @return The auto commit interval
     */
    public int getAutoCommit()
    {
        return commit;
    }

    /**
     * Sets the auto commit interval used when processing the transaction. A 
     * commit will be triggered after this number of inserts or updates. Be
     * carefuly about setting too high of a number or the database may fail on a
     * full transaction log.
     * 
     * @param commit The auto commit interval
     */
    public void setAutoCommit(int commit)
    {
        this.commit = commit;
    }
    
    private String getSQL()
    {
        StringBuilder builder = new StringBuilder();
        String sql = query.getSQL();
        int start = 0;
        
        for(Query.Parameter parameter : query.getParameters())
        {
            builder.append(sql.substring(start, parameter.getStart()));
            builder.append("?");
            
            start = parameter.getStart() + parameter.getLength();
        }
        
        builder.append(sql.substring(start, sql.length()));
        
        return builder.toString();
    }
    
    private void populate(PreparedStatement statement, JSONObject record) throws SQLException
    {
        int index = 1;
        
        for(Query.Parameter parameter : query.getParameters())
        {
            statement.setObject(index++, record.get(parameter.getName()));
        }
        
        statement.addBatch();
    }
    
    /**
     * DO NOT CALL DIRECTLY. This is called by {@link DBMS} to executes the 
     * operation using the provided query for each record in the stream of data. 
     * If the stream is larger than the auto commit threshold, the transaction 
     * will be partially committed to prevent an overflow of the transaction 
     * log.
     * 
     * @param connection an active JDBC connection
     * @throws SQLException When an issue occurs while preparing the statement for the given records. Or while executing the batch operation.
     * @throws NullPointerException if the records have not been initialized
     * @throws NullPointerException if the batch SQL query has not been set
     * @see DBMS#update(AtomicOperation)
     */
    @Override
    public void execute(Connection connection) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement(getSQL());
        int index = 0;
        
        for(JSONObject record : records)
        {
            populate(statement, record);
            
            index++;
            
            // Perform a commit every 10000 records to prevent overflow of
            // transaction buffer
            if(index%commit == 0) statement.executeBatch();
        }
        
        statement.executeBatch();
        statement.close();
    }
    
}
