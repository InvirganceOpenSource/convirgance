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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a set of database operations, such as queries or updates, executed 
 * as a single transaction. The list of AtomicOperation objects will be executed
 * one at a time in the sequence provided. This allows for a long transaction to
 * be planned and then triggered as a single operation with all operations
 * succeeding or all operations rolling back.
 * 
 * @author jbanes
 */
public class TransactionOperation implements AtomicOperation
{
    private List<AtomicOperation> operations;

    /**
     * Creates an empty TransactionOperation
     */
    public TransactionOperation()
    {
        this(new AtomicOperation[0]);
    }

    /**
     * Creates a new TransactionOperation based on the supplied operations.
     * 
     * @param operations the operations to execute in the transaction
     */
    public TransactionOperation(AtomicOperation... operations)
    {
        setOperations(operations);
    }
    
    /**
     * Adds another operation to the transaction
     * 
     * @param operation the operation to add
     */
    public void add(AtomicOperation operation)
    {
        this.operations.add(operation);
    }
    
    /**
     * Set the list of operations to run for this transaction
     * 
     * @param operations a list of operations to run
     */
    public void setOperations(AtomicOperation... operations)
    {
        this.operations = new ArrayList<>(Arrays.asList(operations));
    }

    /**
     * Gets the list of operations planned for this transaction
     * 
     * @return An array of operations.
     */
    public AtomicOperation[] getOperations()
    {
        return operations.toArray(AtomicOperation[]::new);
    }
    
    /**
     * DO NOT CALL DIRECTLY. This is called by {@link DBMS} to execute the 
     * transaction. The transaction is automatically rolled back if an error
     * occurs.
     * 
     * @param connection the JDBC connection
     * @throws SQLException if an error occurs during the transaction
     * @see DBMS#update(AtomicOperation)
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
