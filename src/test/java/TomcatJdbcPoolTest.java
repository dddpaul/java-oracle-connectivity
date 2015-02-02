import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.*;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.concurrent.Executors;

import static oracle.jdbc.OracleConnection.*;

/**
 * This test requires running Oracle instance.
 */
public class TomcatJdbcPoolTest extends BaseTest {

    protected DataSource ds;

    /**
     * This method doesn't work. It blocks on borrowConnection(0, null, null) in
     * {@link org.apache.tomcat.jdbc.pool.ConnectionPool#init(org.apache.tomcat.jdbc.pool.PoolConfiguration)}
     */
    @Ignore
    @Test(expected = SQLRecoverableException.class)
    public void testDataSourceLoginTimeout() throws Exception {
        IpTables.drop(port);
        ds = createDataSource(host);

        // Implementation just sets pool's maxWait
        ds.setLoginTimeout(timeout);

        con = ds.getConnection();
    }

    /**
     * This timeout works for login (connect) only
     */
    @Test(expected = SQLRecoverableException.class)
    public void testDriverThinNetConnectTimeout() throws Exception {
        IpTables.drop(port);
        ds = createDataSource(host);

        ds.setConnectionProperties(CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT + "=" + String.valueOf(timeout * 1000));

        con = ds.getConnection();
    }

    /**
     * This timeout works for every query (by not for login)
     */
    @Test(expected = SQLRecoverableException.class)
    public void testConnectionNetworkTimeout() throws Exception {
        ds = createDataSource(host);

        try {
            con = ds.getConnection();
        } catch (SQLRecoverableException e) {
            log.error(e.getMessage());
        }
        assertNotNull(con);
        con.setNetworkTimeout(Executors.newSingleThreadExecutor(), timeout * 1000);

        IpTables.drop(port);
        executeQuery(con);
    }

    /**
     * This timeout works for every query (by not for login)
     */
    @Test(expected = SQLRecoverableException.class)
    public void testDriverThinReadTimeout() throws Exception {
        ds = createDataSource(host);
        ds.setConnectionProperties(CONNECTION_PROPERTY_THIN_READ_TIMEOUT + "=" + String.valueOf(timeout * 1000));

        try {
            con = ds.getConnection();
        } catch (SQLRecoverableException e) {
            log.error(e.getMessage());
        }
        assertNotNull(con);

        IpTables.drop(port);
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
