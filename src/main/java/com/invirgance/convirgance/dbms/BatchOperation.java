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
 * Provides a way to execute a query using a batch of JSON objects. 
 * Batching operations together is more efficient than executing separate queries for each operation.
 * This class facilitates executing bulk operations in a single query, improving performance.
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
     * @param records The records (as an Iterable of JSONObjects) to be
     * processed in the batch.
     */
    public BatchOperation(Query query, Iterable<JSONObject> records)
    {
        this.query = query;
        this.records = records;
    }

    /**
     * Gets the query to be executed across the records.
     * @return The query.
     * @throws NullPointerException If the query has not been initialized.
     */
    public Query getQuery()
    {
        return query;
    }
    
    /**
     * Sets the query that each record will use when the operation is executed.
     * @param query The query.
     */
    public void setQuery(Query query)
    {
        this.query = query;
    }

    /**
     * Returns the records that will be used by this BatchOperation during execution.
     * @return The JSONObjects used for the operation.
     * @throws NullPointerException If the records have not been initialized.
     */
    public Iterable<JSONObject> getRecords()
    {
        return records;
    }

    /**
     * Sets the records that will be used with to create the query during execution.
     * @param records The JSONObjects to use.
     */
    public void setRecords(Iterable<JSONObject> records)
    {
        this.records = records;
    }
    
    /**
     * Returns the current auto commit interval used when processing the transactions. 
     * After this many transactions are processed, the current batch will be executed.
     * @return The auto commit interval.
     */
    public int getAutoCommit()
    {
        return commit;
    }

    /**
     * Sets the auto commit interval to use when executing the operation. 
     * After this many transactions are processed, the current batch will be executed before continuing. 
     * @param commit The auto commit interval.
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
     * Executes the operation, using the provided query for each record in the dataset. 
     * An auto commit value is to execute the batch operation after a specified amount of records.
     * 
     * @param connection The connection to a DataSource.
     * @throws SQLException When an issue occurs while preparing the statement for the given records. Or while executing the batch operation.
     * @throws NullPointerException If the records have not been initialized.
     * @throws NullPointerException If the query has not been set.
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
