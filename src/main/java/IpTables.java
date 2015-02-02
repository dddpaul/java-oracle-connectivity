import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class IpTables {

    public static enum Target {
        DROP,
        REJECT
    }

    public static enum Proto {
        TCP,
        UDP
    }

    public static void addTcpRule(int port, Target target) throws IOException {
        addRule(Proto.TCP, port, target);
    }

    public static void removeTcpRule(int port, Target target) throws IOException {
        removeRule(Proto.TCP, port, target);
    }

    public static void addRule(Proto proto, int port, Target target) throws IOException {
        iptables(proto, port, target, true);
    }

    public static void removeRule(Proto proto, int port, Target target) throws IOException {
        iptables(proto, port, target, false);
    }

    public static void iptables(Proto proto, int port, Target target, boolean add) throws IOException {
        String action = add ? "-A" : "-D";
        List<String> cmd = Arrays.asList("sudo", "iptables", action, "INPUT", "-p", proto.toString(), "--dport", String.valueOf(port), "-j", target.toString());
        Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        waitForOutput(process);
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
