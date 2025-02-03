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

import com.invirgance.convirgance.input.DelimitedInput;
import com.invirgance.convirgance.input.JSONInput;
import com.invirgance.convirgance.json.JSONObject;
import com.invirgance.convirgance.output.DelimitedOutput;
import com.invirgance.convirgance.source.ByteArraySource;
import com.invirgance.convirgance.source.FileSource;
import com.invirgance.convirgance.target.ByteArrayTarget;
import com.invirgance.convirgance.transform.CoerceStringsTransformer;
import java.io.File;
import java.util.Iterator;
import javax.sql.DataSource;
import org.hsqldb.jdbc.JDBCDataSource;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jbanes
 */
public class DBMSTest
{
    private static DataSource source;
    
    public DBMSTest()
    {
    }
    
    private static void delete(File file)
    {
        if(!file.isDirectory())
        {
            file.delete();
            return;
        }
        
        for(File child : file.listFiles())
        {
            delete(child);
        }
        
        file.delete();
    }
    
    @BeforeAll
    public static void setup()
    {
        JDBCDataSource source = new JDBCDataSource();
        DBMS dbms = new DBMS(source);
        
        delete(new File("target/unit-test-work/dbms/testdb"));
        new File("target/unit-test-work/dbms/testdb").mkdirs();
        
        source.setURL("jdbc:hsqldb:file:target/unit-test-work/dbms/testdb/");
        source.setUser("SA");
        source.setPassword("");
        
        dbms.update(new QueryOperation(new Query("create table CUSTOMER (\n" +
"                                 CUSTOMER_ID INTEGER,\n" +
"                                 DISCOUNT_CODE CHAR(1),\n" +
"                                 ZIP VARCHAR(10),\n" +
"                                 NAME VARCHAR(30),\n" +
"                                 ADDRESSLINE1 VARCHAR(30),\n" +
"                                 ADDRESSLINE2 VARCHAR(30),\n" +
"                                 CITY VARCHAR(25),\n" +
"                                 STATE CHAR(2),\n" +
"                                 PHONE CHAR(12),\n" +
"                                 FAX CHAR(12),\n" +
"                                 EMAIL VARCHAR(40),\n" +
"                                 CREDIT_LIMIT INTEGER\n" +
"                             )")));
        
        for(JSONObject record : new JSONInput().read(new FileSource(new File("src/test/resources/dbms/delimited/customer.json"))))
        {
            dbms.update(new QueryOperation(new Query("insert into CUSTOMER values (:CUSTOMER_ID, :DISCOUNT_CODE, :ZIP, :NAME, :ADDRESSLINE1, :ADDRESSLINE2, :CITY, :STATE, :PHONE, :FAX, :EMAIL, :CREDIT_LIMIT)", record)));
        }
        
        DBMSTest.source = source;
    }

    @Test
    public void testSimpleQuery()
    {
        DBMS dbms = new DBMS(source);
        
        for(JSONObject record : dbms.query(new Query("select * from CUSTOMER")))
        {
            System.out.println(record);
        }
    }
    
    @Test
    public void testRepeatedQuery()
    {
        DBMS dbms = new DBMS(source);
        Iterable<JSONObject> results = dbms.query(new Query("select * from CUSTOMER"));
        
        System.out.println("============= Test 1 ===============");
        
        for(JSONObject record : results)
        {
            System.out.println(record);
        }
        
        System.out.println("============= Test 2 ===============");
        
        for(JSONObject record : results)
        {
            System.out.println(record);
        }
        
        System.out.println("============= Test 3 ===============");
        
        for(JSONObject record : results)
        {
            System.out.println(record);
        }
    }
    
    @Test
    public void testChain()
    {
        DBMS dbms = new DBMS(source);
        Iterable<JSONObject> results = dbms.query(new Query("select * from CUSTOMER"));
        int count = 0;
        
        DelimitedInput input = new DelimitedInput();
        DelimitedOutput output = new DelimitedOutput();
        ByteArrayTarget target = new ByteArrayTarget();
        
        CoerceStringsTransformer transformer = new CoerceStringsTransformer();
        Iterator<JSONObject> iterator = results.iterator();
        JSONObject expected;
        
        // Make sure ZIP is not parsed as a number
        transformer.setExcluded(new String[]{"ZIP"});
        
        // Execute the query and write the results
        output.write(target, results);
        
        // Read back the results and make sure they match
        for(JSONObject record : transformer.transform(input.read(new ByteArraySource(target.getBytes()))))
        {
            expected = iterator.next();

            assertEquals(expected, record);
            assertEquals(expected.hashCode(), record.hashCode());
            
            count++;
        }
        
        assertFalse(iterator.hasNext());
        assertEquals(13, count);
    }
}
