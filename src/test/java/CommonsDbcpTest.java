import oracle.jdbc.OracleConnection;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.concurrent.Executors;

public class CommonsDbcpTest extends BaseTest {

    private BasicDataSource ds;

    @Before
    public void setUp() throws Exception {
        enableRoute(host);
    }

    @After
    public void tearDown() throws Exception {
        if (nc != null) {
            nc.destroy();
            nc = null;
        }
    }

    /**
     * This timeout doesn't work because {@link javax.sql.CommonDataSource#setLoginTimeout(int)} is not implemented
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testDataSourceLoginTimeout() throws Exception {
        nc = netcatListen(port);
        ds = createDataSource("localhost");

        ds.setLoginTimeout(2);

        con = ds.getConnection();
    }

    /**
     * This timeout works for login (connect) only
     */
    @Test(expected = SQLException.class)
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

    private static BasicDataSource createDataSource(String host) throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        ds.setUrl("jdbc:oracle:thin:@" + host + ":" + port + ":" + dbname);
        ds.setUsername(user);
        ds.setPassword(password);
        return ds;
    }
}
