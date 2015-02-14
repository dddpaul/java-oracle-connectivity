# java-oracle-connectivity
Check Oracle connectivity in Java.

This project requires running Oracle instance. The simplest way to run it is to use a docker container [wnameless/docker-oracle-xe-11g](https://github.com/wnameless/docker-oracle-xe-11g).

To satisfy dependencies grab [Oracle JDBC Thin and UCP](http://www.oracle.com/technetwork/database/features/jdbc/default-2280470.html) and execute:

```
mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.2 -Dpackaging=jar -Dfile=ojdbc7.jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.oracle -DartifactId=ucp -Dversion=12.1.0.2 -Dpackaging=jar -Dfile=ucp.jar -DgeneratePom=true
```

Tests have been lead us to a single conclusion — you must specify network timeouts on driver level. This code is suitable for [Tomcat JDBC Connection Pool](http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html):

``` java
// Get datasource some way
Datasource ds = createDataSource(host);

// Set connect (login) timeout
ds.setConnectionProperties(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT + "=3000");

// Set common network read timeout
ds.setConnectionProperties(OracleConnection.CONNECTION_PROPERTY_THIN_READ_TIMEOUT + "=3000");
```

The proper way to specify these timeouts in [JNDI Datasource configuration](http://tomcat.apache.org/tomcat-7.0-doc/jndi-datasource-examples-howto.html):

``` xml
<Resource name="jdbc/oracle"
    driverClassName="oracle.jdbc.OracleDriver"
    factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
    connectionProperties="oracle.net.CONNECT_TIMEOUT=3000;oracle.jdbc.ReadTimeout=3000"
    ...
    />
```

Links:

* [Setting Network Timeout for JDBC connection](http://stackoverflow.com/questions/18822552/setting-network-timeout-for-jdbc-connection)
* [How to use Java.sql.Connection.setNetworkTimeout?](http://stackoverflow.com/questions/10654547/how-to-use-java-sql-connection-setnetworktimeout)
* [Features Specific to JDBC Thin](http://docs.oracle.com/cd/B28359_01/java.111/b31224/jdbcthin.htm)
* [Understanding JDBC Internals & Timeout Configuration](http://www.cubrid.org/blog/dev-platform/understanding-jdbc-internals-and-timeout-configuration/)
