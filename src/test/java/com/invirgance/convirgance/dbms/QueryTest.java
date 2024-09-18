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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jbanes
 */
public class QueryTest
{
    @Test
    public void testParseQueries()
    {
        assertEquals(0, new Query("select * from TABLE").getParameterNames().length);
        assertEquals(1, new Query("select * from TABLE where id = :id").getParameterNames().length);
        assertEquals(1, new Query("select * from TABLE where id=:id").getParameterNames().length);
        assertEquals(2, new Query("select * from TABLE where id = :id and text = 'Hello' and name = :name").getParameterNames().length);
        assertEquals(2, new Query("select * from TABLE where id=:id and text='Hello' and name=:name").getParameterNames().length);
        assertEquals(2, new Query("select * from TABLE where id = :id\nand text = 'Hello'\nand name = :name").getParameterNames().length);
        assertEquals(2, new Query("select * from TABLE where id=:id\nand text='Hello'\nand name=:name").getParameterNames().length);
        
        assertEquals("id", new Query("select * from TABLE where id = :id").getParameterNames()[0]);
        assertEquals("id", new Query("select * from TABLE where id=:id").getParameterNames()[0]);
        assertEquals("id", new Query("select * from TABLE where id = :id and text = 'Hello' and name = :name").getParameterNames()[0]);
        assertEquals("name", new Query("select * from TABLE where id = :id and text = 'Hello' and name = :name").getParameterNames()[1]);
        assertEquals("id", new Query("select * from TABLE where id=:id and text='Hello' and name=:name").getParameterNames()[0]);
        assertEquals("name", new Query("select * from TABLE where id=:id and text='Hello' and name=:name").getParameterNames()[1]);
        assertEquals("id", new Query("select * from TABLE where id = :id\nand text = 'Hello'\nand name = :name").getParameterNames()[0]);
        assertEquals("name", new Query("select * from TABLE where id = :id\nand text = 'Hello'\nand name = :name").getParameterNames()[1]);
        assertEquals("id", new Query("select * from TABLE where id=:id\nand text='Hello'\nand name=:name").getParameterNames()[0]);
        assertEquals("name", new Query("select * from TABLE where id=:id\nand text='Hello'\nand name=:name").getParameterNames()[1]);
        
        assertEquals(2, new Query("select * from TABLE where id = :id\nand text = 'Hello ''World'''\nand name = :name").getParameterNames().length);
        assertEquals(2, new Query("select * from TABLE where id=:id\nand text='Hello ''World'''\nand name=:name").getParameterNames().length);
        assertEquals("id", new Query("select * from TABLE where id = :id\nand text = 'Hello ''World'''\nand name = :name").getParameterNames()[0]);
        assertEquals("name", new Query("select * from TABLE where id=:id\nand text='Hello ''World'''\nand name=:name").getParameterNames()[1]);
        
        assertEquals(1, new Query("select * from TABLE where id = :id-name1").getParameterNames().length);
        assertEquals(1, new Query("select * from TABLE where id = :id_name1").getParameterNames().length);
        assertEquals("id-name1", new Query("select * from TABLE where id = :id-name1").getParameterNames()[0]);
        assertEquals("id_name1", new Query("select * from TABLE where id = :id_name1").getParameterNames()[0]);
        
        try
        {
            new Query("select * from TABLE where x = 'abc");
            fail("Expected error for unterminated string");
        }
        catch(ConvirganceException e)
        {
            assertEquals("Unterminated string in sql starting at position 30 in sql: [select * from TABLE where x = 'abc]", e.getMessage());
        }
        
        try
        {
            new Query("select * from TABLE", new JSONObject("{\"dupe\": true, \"DUPE\": false}}"));
            fail("Expected error for duplicate binding");
        }
        catch(ConvirganceException e)
        {
            assertEquals("Duplicate binding for bound name: DUPE", e.getMessage());
        }
    }
    
    @Test
    public void testDatabaseSQL()
    {
        Query test;
        
        assertEquals("select * from TABLE", new Query("select * from TABLE").getDatabaseSQL());
        assertEquals("select * from TABLE where id = 12", new Query("select * from TABLE where id = :id", new JSONObject("{\"id\": 12}")).getDatabaseSQL());
//        assertEquals("select * from TABLE where name = n'Hello ''World'''", new Query("select * from TABLE where name = :name", new JSONObject("{\"name\": \"Hello 'World'\"}")).getDatabaseSQL());
        assertEquals("select * from TABLE where name = 'Hello ''World'''", new Query("select * from TABLE where name = :name", new JSONObject("{\"name\": \"Hello 'World'\"}")).getDatabaseSQL());
        
        test = new Query("select * from TABLE where date = :date");
        
        test.setBinding("date", new java.sql.Date(2024-1900, 5, 21));
        
        assertEquals(new java.sql.Date(2024-1900, 5, 21), test.getBinding("date"));
        assertEquals("select * from TABLE where date = '2024-06-21'", test.getDatabaseSQL());
        
        test.setBinding("date", new java.sql.Time(2, 14, 33));
        
        assertEquals(new java.sql.Time(2, 14, 33), test.getBinding("Date"));
        assertEquals("select * from TABLE where date = '02:14:33'", test.getDatabaseSQL());
        
        test.setBinding("date", new java.sql.Timestamp(2024-1900, 5, 21, 2, 14, 33, 0));
        
        assertEquals(new java.sql.Timestamp(2024-1900, 5, 21, 2, 14, 33, 0), test.getBinding("DATE"));
        assertEquals("select * from TABLE where date = '2024-06-21 02:14:33'", test.getDatabaseSQL());
        
        test.setBinding("DATE", new java.util.Date(2024-1900, 5, 21, 2, 14, 33));
        
        assertEquals(new java.util.Date(2024-1900, 5, 21, 2, 14, 33), test.getBinding("DATE"));
        assertEquals("select * from TABLE where date = '2024-06-21 02:14:33'", test.getDatabaseSQL());
    }
    
    @Test
    public void testMarkup()
    {
        Query query = new Query("select * from TABLE where name = n'Hello ''World''' and id=:id");
        Query.Markup[] markup = query.getMarkup();
        
        assertEquals(2, markup.length);
        assertEquals("Hello 'World'", ((Query.Text)markup[0]).getValue());
        assertEquals("id", ((Query.Parameter)markup[1]).getName());
        assertEquals(34, markup[0].getStart());
        assertEquals(59, markup[1].getStart());
        assertEquals(17, markup[0].getLength());
        assertEquals(3, markup[1].getLength());
        assertEquals("Text[Hello 'World',34,17]", markup[0].toString());
        assertEquals("Parameter[id,59,3]", markup[1].toString());
    }
    
    @Test
    public void testParameters()
    {
        Query query = new Query("select * from TABLE where name = n'Hello ''World''' and id=:id");
        Query.Parameter[] parameters = query.getParameters();
        
        assertEquals(1, parameters.length);
        assertEquals("id", ((Query.Parameter)parameters[0]).getName());
        assertEquals(59, parameters[0].getStart());
        assertEquals(3, parameters[0].getLength());
        assertEquals("Parameter[id,59,3]", parameters[0].toString());
    }
    
    @Test
    public void testDatabaseBindings()
    {
        Query query = new Query("insert into TABLE VALUES (:id, :name, :blob, :tag, :tag_secondary)");
        byte[] data = new byte[1024];
        
        query.setBinding("ID", 123);
        query.setBinding("NAME", "Bob");
        query.setBinding("BLOB", data);
        query.setBinding("TAG", null);
        
//        assertEquals("insert into TABLE VALUES (123, n'Bob', ?, null, null)", query.getDatabaseSQL());
        assertEquals("insert into TABLE VALUES (123, 'Bob', ?, null, null)", query.getDatabaseSQL());
        assertEquals(1, query.getDatabaseBindings().length);
        assertEquals(data, query.getDatabaseBindings()[0]);
        
        query.setBinding("BLOB", null);
        
//        assertEquals("insert into TABLE VALUES (123, n'Bob', null, null, null)", query.getDatabaseSQL());
        assertEquals("insert into TABLE VALUES (123, 'Bob', null, null, null)", query.getDatabaseSQL());
        assertEquals(0, query.getDatabaseBindings().length);
    }
    
    @Test
    public void testEmptyQuotes()
    {
        Query query = new Query("select * from TABLE where :zipcode = ''");
        
        query.setBinding("zipcode", "");
        
        assertEquals("select * from TABLE where '' = ''", query.getDatabaseSQL());
    }
}
