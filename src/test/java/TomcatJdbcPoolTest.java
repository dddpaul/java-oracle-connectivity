import oracle.jdbc.OracleConnection;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.*;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.concurrent.Executors;

public class TomcatJdbcPoolTest extends BaseTest {

    protected DataSource ds;

    /**
     * This timeout doesn't work
     */
    @Ignore
    @Test(expected = SQLRecoverableException.class)
    public void testDataSourceLoginTimeout() throws Exception {
        nc = netcatListen(port);
        ds = createDataSource("localhost");

        // Implementation just sets pool's maxWait
        ds.setLoginTimeout(2);

        // blocks on borrowConnection(0, null, null) in {@link org.apache.tomcat.jdbc.pool.ConnectionPool#init(org.apache.tomcat.jdbc.pool.PoolConfiguration)}
        con = ds.getConnection();
    }

    /**
     * This timeout works for login (connect) only
     */
    @Test(expected = SQLRecoverableException.class)
    public void testDriverThinNetConnectTimeout() throws Exception {
        nc = netcatListen(port);
        ds = createDataSource("localhost");
        ds.setConnectionProperties(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT + "=2000");

        con = ds.getConnection();
    }

    /**
     * This timeout works for every query (by not for login)
     */
    @Test(expected = SQLRecoverableException.class)
    public void testConnectionNetworkTimeout() throws Exception {
        ds = createDataSource(host);

        con = ds.getConnection();
        assertNotNull(con);
        con.setNetworkTimeout(Executors.newSingleThreadExecutor(), 2000);

        disableRoute(host);
        executeQuery(con);
    }

    /**
     * This timeout works for every query (by not for login)
     */
    @Test(expected = SQLRecoverableException.class)
    public void testDriverThinReadTimeout() throws Exception {
        ds = createDataSource(host);
        ds.setConnectionProperties(OracleConnection.CONNECTION_PROPERTY_THIN_READ_TIMEOUT + "=2000");

        con = ds.getConnection();
        assertNotNull(con);

        disableRoute(host);
        executeQuery(con);
    }

    private static DataSource createDataSource(String host) throws SQLException {
        DataSource ds = new DataSource();
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        ds.setUrl("jdbc:oracle:thin:@" + host + ":" + port + ":" + dbname);
        ds.setUsername(user);
        ds.setPassword(password);
        return ds;
    }
}
