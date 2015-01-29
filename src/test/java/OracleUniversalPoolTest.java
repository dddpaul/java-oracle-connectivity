import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.junit.*;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Properties;
import java.util.concurrent.Executors;

public class OracleUniversalPoolTest extends BaseTest {

    private PoolDataSource ds;

    /**
     * This timeout works for login (connect) only
     */
    @Ignore
    @Test(expected = SQLRecoverableException.class)
    public void testDataSourceLoginTimeout() throws Exception {
        nc = netcatListen(port);
        ds = createDataSource("localhost");

        // Seems like implementation does nothing
        ds.setLoginTimeout(2);

        // Blocks in {@link oracle.ucp.jdbc.PoolDataSourceImpl#getConnection(java.lang.String, java.lang.String)}
        con = ds.getConnection();
    }

    /**
     * This timeout works for login (connect) only
     */
    @Test(expected = SQLException.class)
    public void testDriverThinNetConnectTimeout() throws Exception {
        nc = netcatListen(port);
        ds = createDataSource("localhost");

        Properties props = new Properties();
        props.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, "2000");
        ds.setConnectionProperties(props);

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

        Properties props = new Properties();
        props.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_READ_TIMEOUT, "2000");
        ds.setConnectionProperties(props);

        con = ds.getConnection();
        assertNotNull(con);

        disableRoute(host);
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
