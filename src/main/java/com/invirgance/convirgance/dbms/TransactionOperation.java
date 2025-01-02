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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a set of database operations, such as queries or updates, executed as a single transaction against the same Database. 
 * Ensures atomicity, meaning that either all operations succeed, or none are applied.
 * @author jbanes
 */
public class TransactionOperation implements AtomicOperation
{
    private List<AtomicOperation> operations;

    /**
     * Creates an empty TransactionOperation.
     */
    public TransactionOperation()
    {
        this(new AtomicOperation[0]);
    }

    /**
     * Creates a new TransactionOperation based on the supplied operations.
     * @param operations The operations for this transaction.
     */
    public TransactionOperation(AtomicOperation... operations)
    {
        setOperations(operations);
    }
    
    /**
     * Adds another operation to the transaction.
     * @param operation The operation to add.
     */
    public void add(AtomicOperation operation)
    {
        this.operations.add(operation);
    }
    
    /**
     * Set the operations to run for this transaction.
     * @param operations The operations to run.
     */
    public void setOperations(AtomicOperation... operations)
    {
        this.operations = Arrays.asList(operations);
    }

    /**
     * Gets the operations planned for this transaction.
     * @return An array of operations.
     */
    public AtomicOperation[] getOperations()
    {
        return operations.toArray(AtomicOperation[]::new);
    }
    
    /**
     * Executes all the operations planned for this transaction against the provided connection. 
     * If any operation fails the Database/DataSource will remain unchanged.
     * @param connection The Database/DataSource connection.
     * @throws SQLException Thrown when an issue occurs while executing one the operations.
     */
    @Override
    public void execute(Connection connection) throws SQLException
    {
        for(AtomicOperation operation : operations)
        {
            operation.execute(connection);
        }
    }
    
}
