import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;
import org.junit.*;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Properties;
import java.util.concurrent.Executors;

public class OracleDataSourceTest extends BaseTest {

    private OracleDataSource ds;

    /**
     * This timeout works for login (connect) only
     */
    @Test
    public void testDataSourceLoginTimeout() throws Exception {
        future = listen(port);
        ds = createDataSource("localhost");

        ds.setLoginTimeout(2);

        con = ds.getConnection();
    }

    /**
     * This timeout works for login (connect) only
     */
    @Ignore
    @Test(expected = SQLRecoverableException.class)
    public void testDriverThinNetConnectTimeout() throws Exception {
        future = listen(port);
        ds = createDataSource("localhost");

        Properties properties = new Properties();
        properties.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, "2000");
        ds.setConnectionProperties(properties);

        con = ds.getConnection();
    }

    /**
     * This timeout works for every query (by not for login)
     */
    @Ignore
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
    @Ignore
    @Test(expected = SQLRecoverableException.class)
    public void testDriverThinReadTimeout() throws Exception {
        ds = createDataSource(host);

        Properties properties = new Properties();
        properties.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_READ_TIMEOUT, "2000");
        ds.setConnectionProperties(properties);

        con = ds.getConnection();
        assertNotNull(con);

        disableRoute(host);
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
