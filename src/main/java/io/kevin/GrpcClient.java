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

    static private long uid;

    private int operation_number;

    public GrpcClient(Channel channel, int uid) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = KeyValServiceGrpc.newBlockingStub(channel);
        this.operation_number = 0;
    }

    public long get_uid() {
        return uid;
    }

    public void set_uid(long id) {
        uid = id;
    }

    public int get_operation_number() {
        return this.operation_number;
    }

    public void increment_operation_number() {
        this.operation_number++;
    }

    public void write(long key, long val, long uid, int operation_number) {
        // set (key,val)
        logger.info("Setting (" + key + "," + val + ") --- Server: " + uid + " --- Operation number: " + operation_number);
        try {
            blockingStub.setVal(KeyVal.newBuilder().setK(key).setV(val).setId(uid).build());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
        }
    }

    public long get_id() {
        logger.info("Getting Id: ");
        Val id = blockingStub.getId(Key.newBuilder().build());

        return id.getV();
    }

    public void get(KeyList kl, int operation_number) {
        //logger.info("Getting (" + kl.toString() + ") --- Server: " + uid + " --- Operation number:" + operation_number);
        MaybeValList mvl;

        try {
            mvl = blockingStub.getVal(kl);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }

        for (int i = 0; i < kl.getKList().size(); ++i)
        {
            MaybeVal mv = mvl.getValList().get(i);
            Key key = kl.getKList().get(i);
            if (mv.hasVal()) {
                logger.info("Got result (" + key.getK() + ", " + mv.getVal().getV() + ") --- Server: " + uid + " --- Operation number: " + operation_number);
                System.out.println(mv.getVal().getV());
            } else {
                logger.info("Got result (" + key.getK() + ", Empty ) --- Server: " + uid + " --- Operation number:" + operation_number);
            }
        }
    }

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

    public static void get(GrpcClient c, KeyList kl) {
        c.increment_operation_number();
        c.get(kl, c.get_operation_number());
    }

    static long get_id_from_leader(ArrayList<ManagedChannel> channels, ArrayList<GrpcClient> clients) throws Exception {
        return clients.get(0).get_id();
    }

    enum operation_type
    {
        GET,
        PUT,
        SLEEP,
        NOOP
    }

    // function that parses the file
    static void handle_query_file(String query_file, ArrayList<ManagedChannel> channels, ArrayList<GrpcClient> clients) throws Exception {
        File file_obj = new File(query_file);
        Scanner reader = new Scanner(file_obj);
        while (reader.hasNextLine()) {
            String query = reader.nextLine();
            String[] operations = query.split(";");
            operation_type operation_t = operation_type.NOOP;

            for (String operation : operations)
            {
                String[] words = operation.split(" ");
                String op_type_arg = words[0];
                int op_arg1 = Integer.parseInt(words[1]);
                int op_arg2 = Integer.parseInt(words[2]);
                int delayTime = Integer.parseInt(words[3]); // out of index

                KeyList.Builder kl = KeyList.newBuilder();
                int server_num = -1;

                try {
                    if (op_type_arg.equals("sleep")) {
                        try {
                            //System.out.println("sleeping operation");
                            operation_t = operation_type.SLEEP;
                            TimeUnit.SECONDS.sleep(op_arg1);

                        } catch (InterruptedException e) {
                            logger.log(Level.WARNING, "Error in time");
                        }
                    }
                    else if (op_type_arg.equals("put")) {
                        operation_t = operation_type.PUT;
                        write(clients, op_arg1, op_arg2, delayTime);
                    } else {
                        // getting return
                        kl.addK(Key.newBuilder().setK(op_arg2));
                        server_num = op_arg1;
                        operation_t = operation_type.GET;
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

                switch(operation_t)
                {
                    case PUT:
                        // We had put operations -> we need to commit at the end and notify coordinator we have pushed our full batch of operations
                        clients.get(0).blockingStub.setCommit(Val.newBuilder().setV(clients.get(0).get_uid()).build());
                        break;
                    case GET:
                        get(clients.get(server_num), kl.build());
                        break;
                    case SLEEP:
                    default:
                        break;
                }
            }
        }

        reader.close();
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 5) {
            System.err.println("Usage: [name local/remote host [server_port_0, server_port_n] file_name]");
            System.err.println("");
            System.exit(1);
        }
        String locality = args[0];
        String host = args[1];
        int first_server = Integer.parseInt(args[2]);
        int last_server = Integer.parseInt(args[3]);

        ArrayList<ManagedChannel> channels = new ArrayList<>();
        ArrayList<GrpcClient> clients = new ArrayList<>();
        for (int i = first_server; i <= last_server; ++i) {
            String target = host + ":" + i;
            if(!locality.equals("local"))
            {
                target = host + i + ":9100";
            }
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                    .usePlaintext()
                    .build();
            channels.add(channel);

            clients.add(new GrpcClient(channel, i));
        }
        String file_name = args[4];
        clients.get(0).set_uid(get_id_from_leader(channels, clients));
        handle_query_file(file_name, channels, clients);

        for (ManagedChannel c : channels) {
            c.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
