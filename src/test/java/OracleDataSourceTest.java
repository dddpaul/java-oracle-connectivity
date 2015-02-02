import oracle.jdbc.pool.OracleDataSource;
import org.junit.*;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Properties;
import java.util.concurrent.Executors;

import static oracle.jdbc.OracleConnection.*;

/**
 * This test requires running Oracle instance.
 */
public class OracleDataSourceTest extends BaseTest {

    private OracleDataSource ds;

    /**
     * This timeout works for login (connect) only
     */
    @Test(expected = SQLRecoverableException.class)
    public void testDataSourceLoginTimeout() throws Exception {
        IpTables.drop(port);
        ds = createDataSource(host);

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

        Properties properties = new Properties();
        properties.setProperty(CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, String.valueOf(timeout * 1000));
        ds.setConnectionProperties(properties);

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

        Properties properties = new Properties();
        properties.setProperty(CONNECTION_PROPERTY_THIN_READ_TIMEOUT, String.valueOf(timeout * 1000));
        ds.setConnectionProperties(properties);

        try {
            con = ds.getConnection();
        } catch (SQLRecoverableException e) {
            log.error(e.getMessage());
        }
        assertNotNull(con);

        IpTables.drop(port);
        executeQuery(con);
    }

    public static OracleDataSource createDataSource(String host) throws SQLException {
        OracleDataSource ds = new OracleDataSource();
        ds.setURL("jdbc:oracle:thin:@" + host + ":" + port + ":" + dbname);
        ds.setUser(user);
        ds.setPassword(password);
        return ds;
    }
}
