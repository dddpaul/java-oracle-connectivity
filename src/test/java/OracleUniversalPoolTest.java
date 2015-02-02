import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.junit.*;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Properties;
import java.util.concurrent.Executors;

import static oracle.jdbc.OracleConnection.*;

/**
 * This test requires running Oracle instance.
 */
public class OracleUniversalPoolTest extends BaseTest {

    private PoolDataSource ds;

    /**
     * This method doesn't work. It blocks in
     * {@link oracle.ucp.jdbc.PoolDataSourceImpl#getConnection(java.lang.String, java.lang.String)}
     */
    @Ignore
    @Test(expected = SQLRecoverableException.class)
    public void testDataSourceLoginTimeout() throws Exception {
        IpTables.addTcpRule(port, IpTables.Target.DROP);
        ds = createDataSource(host);

        // Seems like implementation does nothing
        ds.setLoginTimeout(2);

        con = ds.getConnection();
    }

    /**
     * This timeout works for login (connect) only
     */
    @Test(expected = SQLException.class)
    public void testDriverThinNetConnectTimeout() throws Exception {
        IpTables.addTcpRule(port, IpTables.Target.DROP);
        ds = createDataSource(host);

        Properties props = new Properties();
        props.setProperty(CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, String.valueOf(timeout * 1000));
        ds.setConnectionProperties(props);

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

        IpTables.addTcpRule(port, IpTables.Target.DROP);
        executeQuery(con);
    }

    /**
     * This timeout works for every query (by not for login)
     */
    @Test(expected = SQLRecoverableException.class)
    public void testDriverThinReadTimeout() throws Exception {
        ds = createDataSource(host);

        Properties props = new Properties();
        props.setProperty(CONNECTION_PROPERTY_THIN_READ_TIMEOUT, String.valueOf(timeout * 1000));
        ds.setConnectionProperties(props);

        try {
            con = ds.getConnection();
        } catch (SQLRecoverableException e) {
            log.error(e.getMessage());
        }
        assertNotNull(con);

        IpTables.addTcpRule(port, IpTables.Target.DROP);
        executeQuery(con);
    }

    private static PoolDataSource createDataSource(String host) throws SQLException {
        PoolDataSource ds = PoolDataSourceFactory.getPoolDataSource();
        ds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
        ds.setURL("jdbc:oracle:thin:@" + host + ":" + port + ":" + dbname);
        ds.setUser(user);
        ds.setPassword(password);
        return ds;
    }
}
