package io.kevin;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcClient {
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());

    private final KeyValServiceGrpc.KeyValServiceBlockingStub blockingStub;

    private int uid;

    private int operation_number;

    public GrpcClient(Channel channel, int uid) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = KeyValServiceGrpc.newBlockingStub(channel);
        this.uid = uid;
        this.operation_number = 0;
    }

    public int get_uid() {
        return this.uid;
    }

    public int get_operation_number() {
        return this.operation_number;
    }

    public void increment_operation_number() {
        this.operation_number++;
    }

    public void write(long key, long val, int uid, int operation_number) {
        // set (key,val)
        logger.info("Setting (" + key + "," + val + ") --- Server: " + uid + " --- Operation number: " + operation_number);
        try {
            blockingStub.setVal(KeyVal.newBuilder().setK(key).setV(val).build());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
        }
    }
    public long get(long key, int uid, int operation_number) {
        // get (k)
        try {
            //System.out.println("time for delay getting operation");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Error in time");
        }
        
        logger.info("Getting (" + key + ") --- Server: " + uid + " --- Operation number:" + operation_number);
        MaybeVal v;


        try {
            v = blockingStub.getVal(Key.newBuilder().setK(key).build());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return -1;
        }

        if (v.hasVal()) {
            logger.info("Got result (" + key + ", " + v.getVal().getV() + ") --- Server: " + uid + " --- Operation number: " + operation_number);
            //saveToFile("", ops_output_N.txt); // differentiate among files 
            System.out.println(v.getVal().getV());
            return v.getVal().getV();
        } else {
            logger.info("Got result (" + key + ", Empty ) --- Server: " + uid + " --- Operation number:" + operation_number);
            return -1;
        }
    }


    // this function
    public static void write(ArrayList<GrpcClient> clients, long key, long val, long delayTime) {
        for (int i = 1; i < clients.size(); ++i) {
            GrpcClient client = clients.get(i);
            client.increment_operation_number();
            client.write(key, val, client.get_uid(), clients.get(i).get_operation_number());
        }
        try {
            //System.out.println("delayTime: " + delayTime);
            TimeUnit.SECONDS.sleep(delayTime);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Error in time");
        }
        // write to coordinator
        clients.get(0).increment_operation_number();
        clients.get(0).write(key, val, clients.get(0).get_uid(), clients.get(0).get_operation_number());
    }
    // this function
    public static long get(GrpcClient c, long key) {
        c.increment_operation_number();
        return c.get(key, c.get_uid(), c.get_operation_number());
    }

    // function that parses the file
    static void handle_query_file(String query_file, ArrayList<ManagedChannel> channels, ArrayList<GrpcClient> clients) throws Exception {
        File file_obj = new File(query_file);
        Scanner reader = new Scanner(file_obj);
        while (reader.hasNextLine()) {
            String query = reader.nextLine();
            String[] words = query.split(" ");
            String op_type_arg = words[0];
            int op_arg1 = Integer.parseInt(words[1]);
            int op_arg2 = Integer.parseInt(words[2]);
            int delayTime = Integer.parseInt(words[3]); // out of index

            try {
                if (op_type_arg.equals("sleep")) {
                    try {
                        //System.out.println("sleeping operation");
                        TimeUnit.SECONDS.sleep(op_arg1);
                    } catch (InterruptedException e) {
                        logger.log(Level.WARNING, "Error in time");
                    }
                }
                else if (op_type_arg.equals("put")) {
                    write(clients, op_arg1, op_arg2, delayTime);
                } else {
                    // getting return
                    get(clients.get(op_arg1), op_arg2);

                    // separate java application
                    // call handle_query_file()
                    // shell(./build/install/sometest/bin/key-val-client localhost 9100 9102 ops.txt)
                }
            } catch (Exception e) {
                // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
                // resources the channel should be shut down when it will no longer be used. If it may be used
                // again leave it running.
                for (ManagedChannel c : channels) {
                    c.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                }

                reader.close();
                return;
            }
        }

        reader.close();
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 4) {
            System.err.println("Usage: [name host [server_port_0, server_port_n] file_name]");
            System.err.println("");
            System.exit(1);
        }
        String host = args[0];
        int first_server = Integer.parseInt(args[1]);
        int last_server = Integer.parseInt(args[2]);

        ArrayList<ManagedChannel> channels = new ArrayList<>();
        ArrayList<GrpcClient> clients = new ArrayList<>();
        for (int i = first_server; i <= last_server; ++i) {
            String target = host + ":" + i;
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                    .usePlaintext()
                    .build();
            channels.add(channel);

            clients.add(new GrpcClient(channel, i));
        }
        String file_name = args[3];
        handle_query_file(file_name, channels, clients);

        for (ManagedChannel c : channels) {
            c.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
