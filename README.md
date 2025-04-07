# Convirgance

![Version](https://img.shields.io/badge/Version-1.2.3-blue) ![License](https://img.shields.io/badge/License-MIT-green) ![Repository](https://img.shields.io/badge/Platform-Java-gold) ![Repository](https://img.shields.io/badge/Repository-Maven%20Central-red)

Convirgance is a modern, streamlined approach to database access. Unlike traditional
ORM frameworks that map your database into Java objects, Convirgance gives you direct
control over your SQL queries, returning results as a stream of `Map` objects.

Not only does this reduce coding by as much as 60%, the stream of data can
be transformed, filtered, and manipulated before being serialized to nearly
any data format. Web Services, ETL, configuration databases, OLAP, and many other
use cases can be completed in minutes rather than hours or even days.

## Why Convirgance?

Traditional ORMs force you to map your database to Java objects, adding complexity and overhead. Convirgance takes a different approach:

- **Direct SQL Queries**: No need to write Data Access Objects. Write or generate the SQL you want to get back the data you need.
- **Stream Processing**: Handle large datasets in small memory footprints without GC pauses, cache thrashing, or memory pressure.
- **Format Freedom**: Read from a database, output to CSV, or transform JSON - streams can be read from any format and written to any format.
- **SQL-Like Operations**: Filters and transformers provide advanced concepts like WHERE-clause and GROUP BY features, allowing powerful manipulation of data.
- **High Performance**: SQL queries produce better query plans, data streams have lower latency, and CPU cache utilization is higher.

## Installation

Add the following dependency to your Maven `pom.xml` file:

```xml
<dependency>
    <groupId>com.invirgance</groupId>
    <artifactId>convirgance</artifactId>
    <version>1.2.3</version>
</dependency>
```

## Example

Here is an example demonstrating the simplicity of the
Convirgance approach.

In the few lines of code below we are querying the `CUSTOMER` table
in the database and writing the results to a CSV file.

```java
// Query the database
DBMS database = new DBMS(source);
Query query = new Query("select name, devices, pets from CUSTOMER");
Iterable<JSONObject> results = database.query(query);

// Specify the target file
FileTarget target = new FileTarget("example.csv");

// Write the stream to a CSV file
new CSVOutput().write(target, results);
```

The resulting `example.csv` file can be opened in a program like Excel to view
the exported data:

| name | devices | pets |
| ---- | ------- | ---- |
| John | 3       | 1    |
| Bob  | 1       | 2    |
| Kyle | 1       | 10   |
| ...  | ...     | ...  |

Output formats can be easily swapped. For example, we can replace the last
line to output JSON instead:

```java
new JSONOutput().write(new FileTarget("example.json", results));
```


## Documentation

📑 High Level Documentation: https://docs.invirgance.com/convirgance/

📚 JavaDocs: https://docs.invirgance.com/javadocs/convirgance/


## License

Convirgance is available under the MIT License. See [License](LICENSE.md) for more details.
