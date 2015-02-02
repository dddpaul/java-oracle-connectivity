import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

public class IpTables {

    private static final Logger log = LogManager.getLogger( MethodHandles.lookup().lookupClass() );

    public static enum Target {
        DROP,
        REJECT
    }

    public static enum Proto {
        TCP,
        UDP
    }

    public static void drop(int port) {
        addTcpRule(port, Target.DROP);
    }

    public static void reject(int port) {
        addTcpRule(port, Target.REJECT);
    }

    public static void allow(int port) {
        for (Target target : Target.values()) {
            removeTcpRule(port, target);
        }
    }

    public static void addTcpRule(int port, Target target) {
        addRule(Proto.TCP, port, target);
    }

    public static void removeTcpRule(int port, Target target) {
        removeRule(Proto.TCP, port, target);
    }

    public static void addRule(Proto proto, int port, Target target) {
        iptables(proto, port, target, true);
    }

    public static void removeRule(Proto proto, int port, Target target) {
        iptables(proto, port, target, false);
    }

    public static void iptables(Proto proto, int port, Target target, boolean add) {
        try {
            String action = add ? "-A" : "-D";
            List<String> cmd = Arrays.asList( "sudo", "iptables", action, "INPUT", "-p", proto.toString(), "--dport", String.valueOf( port ), "-j", target.toString() );
            Process process = new ProcessBuilder( cmd ).redirectErrorStream( true ).start();
            if(process.waitFor() > 0 ) {
                log.warn( waitForOutput( process ) );
            }
        } catch( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public static String waitForOutput(Process process) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader b = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = b.readLine()) != null) {
            result.append(line).append(System.lineSeparator());
        }
        return result.toString();
    }
}
