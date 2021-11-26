package io.grpc.examples.helloworld;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class HelloWorldClient {
    private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    /**
     * Construct client for accessing HelloWorld server using the existing channel.
     */
    public HelloWorldClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void write(long key, long val) {
        // set (key,val)
        logger.info("Setting (" + key + "," + val + ")");
        try {
            blockingStub.setVal(KeyVal.newBuilder().setK(key).setV(val).build());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
        }
    }

    public long get(long key) {
        // get (k)
        logger.info("Getting (" + key + ")");
        Val v;
        try {
            v = blockingStub.getVal(Key.newBuilder().setK(key).build());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return - 1;
        }

        return v.getV();
    }

    public static void write(ArrayList<HelloWorldClient> clients, long key, long val)
    {
        for (int i = 1; i < clients.size(); ++i)
        {
            clients.get(i).write(key, val);
        }
        clients.get(0).write(key, val);
    }

    public static long get(HelloWorldClient c, long key)
    {
        return c.get(key);
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 2) {
            System.err.println("Usage: [name [server_port_0, server_port_n]");
            System.err.println("");
            System.exit(1);
        }

        int first_server = Integer.parseInt(args[0]);
        int last_server = Integer.parseInt(args[1]);

        ArrayList<ManagedChannel> channels = new ArrayList<>();
        ArrayList<HelloWorldClient> clients = new ArrayList<>();
        for (int i = first_server; i <= last_server; ++i)
        {
            String target = "localhost:" + i;
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                    .usePlaintext()
                    .build();
            channels.add(channel);

            clients.add(new HelloWorldClient(channel));
        }

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        try {
            write(clients, 1, 2);

            get(clients.get(0), 1);

        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            for (ManagedChannel c : channels)
            {
                c.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            }
        }
    }
}
