import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class BaseTest extends Assert {

    protected static final String host = "localhost";
    protected static final int port = 1521;
    protected static final String dbname = "xe";
    protected static final String user = "system";
    protected static final String password = "oracle";

    private static ExecutorService executor;

    protected Connection con;
    protected Process nc, bane;
    protected Future<Socket> future;

    @BeforeClass
    public static void init() {
        executor = Executors.newSingleThreadExecutor();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        if (bane != null) {
            bane.destroy();
            bane = null;
        }
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

    public Future<Socket> listen(int port) throws IOException {
        return Listeners.isAvailable(port) ? executor.submit(Listeners.createListener(port)) : null;
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
