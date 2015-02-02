import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.concurrent.Executors;

import static oracle.jdbc.OracleConnection.*;

/**
 * This test requires running Oracle instance.
 */
public class CommonsDbcpTest extends BaseTest {

    private BasicDataSource ds;

    /**
     * This doesn't work because {@link javax.sql.CommonDataSource#setLoginTimeout(int)} is not implemented
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testDataSourceLoginTimeout() throws Exception {
        IpTables.addTcpRule(port, IpTables.Target.DROP);
        ds = createDataSource(host);

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

        IpTables.addTcpRule(port, IpTables.Target.DROP);
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

        IpTables.addTcpRule(port, IpTables.Target.DROP);
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
