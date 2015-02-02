import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BaseTest extends Assert {

    protected static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    protected static final String host = "localhost";
    protected static final int port = 1521;
    protected static final String dbname = "xe";
    protected static final String user = "system";
    protected static final String password = "oracle";
    protected static final int timeout = 2; // seconds

    protected Connection con;

    @Before
    public void setUp() throws Exception {
        IpTables.allow(port);
    }

    @After
    public void tearDown() throws Exception {
        IpTables.allow(port);
    }

    public void executeQuery(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT 1 FROM dual");
        ps.executeQuery();
        ps.close();
    }
}
