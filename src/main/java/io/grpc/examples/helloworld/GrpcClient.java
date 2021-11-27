package io.grpc.examples.helloworld;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcClient {
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public GrpcClient(Channel channel) {
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
        MaybeVal v;
        try {
            v = blockingStub.getVal(Key.newBuilder().setK(key).build());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return - 1;
        }

        if (v.hasVal())
        {
            logger.info("Got result (" + key + ", " + v.getVal().getV() +")");
            return v.getVal().getV();
        }
        else
        {
            logger.info("Got result (" + key + ", Empty )");
            return -1;
        }
    }

    public static void write(ArrayList<GrpcClient> clients, long key, long val)
    {
        for (int i = 1; i < clients.size(); ++i)
        {
            clients.get(i).write(key, val);
        }
        clients.get(0).write(key, val);
    }

    public static long get(GrpcClient c, long key)
    {
        return c.get(key);
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 5) {
            System.err.println("Usage: [name [server_port_0, server_port_n]");
            System.err.println("");
            System.exit(1);
        }
        int first_server = Integer.parseInt(args[0]);
        int last_server = Integer.parseInt(args[1]);

        ArrayList<ManagedChannel> channels = new ArrayList<>();
        ArrayList<GrpcClient> clients = new ArrayList<>();
        for (int i = first_server; i <= last_server; ++i)
        {
            String target = "localhost:" + i;
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                    .usePlaintext()
                    .build();
            channels.add(channel);

            clients.add(new GrpcClient(channel));
        }
        String op_type_arg = args[2];
        int op_arg1 = Integer.parseInt(args[3]);
        int op_arg2 = Integer.parseInt(args[4]);
        try {
            if (op_type_arg.equals("put"))
            {
                write(clients, op_arg1, op_arg2);
            }
            else
            {
                get(clients.get(op_arg1), op_arg2);
            }
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
