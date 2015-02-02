import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Requires installed bane gem.
 * gem install bane
 */
public class Bane {

    public static enum Responder {
        CloseAfterPause,
        CloseImmediately,
        DelugeResponse,
        DelugeResponseForEachLine,
        EchoResponse,
        FixedResponse,
        FixedResponseForEachLine,
        HttpRefuseAllCredentials,
        NeverRespond,
        NewlineResponse,
        NewlineResponseForEachLine,
        RandomResponse,
        RandomResponseForEachLine,
        SlowResponse,
        SlowResponseForEachLine,
        TimeoutInListenQueue
    }

    public static Process neverRespond(final int port) throws IOException, InterruptedException {
        return bane(port, Responder.NeverRespond);
    }

    public static Process bane(final int port, Responder responder) throws IOException, InterruptedException {
        if(!Listeners.isAvailable(port)) {
            return null;
        }
        List<String> cmd = Arrays.asList("bane", String.valueOf(port), responder.toString());
        return new ProcessBuilder(cmd).redirectErrorStream(true).start();
    }
}
