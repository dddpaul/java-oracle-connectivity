import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class BaseTest extends Assert {
    protected static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    protected static final String host = "localhost";
    protected static final int port = 1521;
    protected static final String dbname = "xe";
    protected static final String user = "system";
    protected static final String password = "oracle";
    protected static final int timeout = 2; // seconds

    protected Connection con;
    protected Process nc, bane;

    @After
    public void tearDown() throws Exception {
        IpTables.removeTcpRule(port, IpTables.Target.DROP);
    }

    public void executeQuery(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT 1 FROM dual");
        ps.executeQuery();
        ps.close();
    }

    /**
     * ip route del <host>
     */
    public void enableRoute(String host) throws IOException {
        List<String> cmd = Arrays.asList("sudo", "ip", "route", "del", host);
        Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        waitForOutput(process);
    }

    /**
     * ip route add <host> dev lo
     */
    public void disableRoute(String host) throws IOException {
        List<String> cmd = Arrays.asList("sudo", "ip", "route", "add", host, "dev", "lo");
        Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        waitForOutput(process);
    }

    public Process netcatListen(int port) throws IOException, InterruptedException {
        return Listeners.isAvailable(port) ? Listeners.createNetCatListener(port) : null;
    }

    public String waitForOutput(Process process) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader b = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = b.readLine()) != null) {
            result.append(line).append(System.lineSeparator());
        }
        return result.toString();
    }
}
