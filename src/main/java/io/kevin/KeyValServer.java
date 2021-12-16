package io.kevin;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class KeyValServer {
    private static final Logger logger = Logger.getLogger(KeyValServer.class.getName());

    private Server server;

    private void start(TopoConf conf) throws IOException, InterruptedException {

        keyValImpl gi = new keyValImpl();
        int myPort = conf.port_from_target(conf.get_targets().get(conf.get_my_idx()));
        server = ServerBuilder.forPort(myPort)
                .addService(gi)
                .build()
                .start();
        logger.info("Server started, listening on " + myPort);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    KeyValServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });

        // hacky: wait for all to get setup:
        TimeUnit.SECONDS.sleep(4);

        ArrayList<ManagedChannel> arr = new ArrayList<>();
        for (int i = 0; i < conf.get_targets().size(); ++i) {
            if (i == conf.get_my_idx())
                continue;
            String target = conf.get_targets().get(i);
            arr.add(ManagedChannelBuilder.forTarget(target).usePlaintext().build());
        }
        gi.setReplicas(arr, conf.get_my_idx() == 0);
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final KeyValServer server = new KeyValServer();

        String topo_file_name = args[0];
        int my_idx = Integer.parseInt(args[1]);

        TopoConf conf;
        try {
            conf = new TopoConf(topo_file_name, my_idx);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        server.start(conf);
        server.blockUntilShutdown();
    }

    static class keyValImpl extends KeyValServiceGrpc.KeyValServiceImplBase {

        ConcurrentHashMap<Long, Long> HT;
        ConcurrentHashMap<Long, LinkedBlockingQueue<KeyVal>> batch_logs;
        ConcurrentHashMap<Long, Boolean> committed;
        long current_id = 0;
        Lock mtx;

        // leader node is the first one of the replicas (if we are not the leader)
        ArrayList<KeyValServiceGrpc.KeyValServiceBlockingStub> replicas;
        boolean leader;

        private synchronized long get_next_id() {
            return current_id++;
        }

        public keyValImpl() {
            HT = new ConcurrentHashMap<>();
            batch_logs = new ConcurrentHashMap<>();
            committed = new ConcurrentHashMap<>();
            mtx = new ReentrantLock();
        }

        public void setReplicas(ArrayList<ManagedChannel> chns, boolean leader) {
            replicas = new ArrayList<>();
            for (ManagedChannel mc : chns) {
                replicas.add(KeyValServiceGrpc.newBlockingStub(mc));
            }

            this.leader = leader;
        }

        @Override
        public void setVal(KeyVal kv, StreamObserver<Val> responseObserver) {
            mtx.lock();
            if (!batch_logs.containsKey(kv.getId())) {
                batch_logs.put(kv.getId(), new LinkedBlockingQueue<>());
            }
            if (!committed.containsKey(kv.getId())) {
                committed.put(kv.getId(), false);
            }
            batch_logs.get(kv.getId()).add(kv);

            mtx.unlock();
            Val v = Val.newBuilder().setV(kv.getV()).build();

            responseObserver.onNext(v);
            responseObserver.onCompleted();
        }

        @Override
        public void getId(Key k, StreamObserver<Val> responseObserver) {
            Val v = Val.newBuilder().build();

            if (leader) {
                v = Val.newBuilder().setV(get_next_id()).build();
            }

            responseObserver.onNext(v);
            responseObserver.onCompleted();
        }

        @Override
        public void setCommit(Val id, StreamObserver<Val> responseObserver) {
            logger.info("setCommit request");
            if (leader) {
                logger.info("we are leaders");
            }

            long cl_id = id.getV();
            mtx.lock();
            committed.put(cl_id, true);

            mtx.unlock();

            responseObserver.onNext(Val.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void getVal(KeyList kl, StreamObserver<MaybeValList> responseObserver) {
            logger.info("getVal received");

            MaybeValList.Builder mvl = MaybeValList.newBuilder();
            if (!leader) {
                System.err.println("Non-coordinator received get -- impossible case");

                responseObserver.onNext(mvl.build());
                responseObserver.onCompleted();
                return;
            }

            mtx.lock();

            coord2PC(kl.getId());

            for (Key k : kl.getKList()) {
                if (HT.containsKey(k.getK())) {
                    long ht_v = HT.get(k.getK());
                    mvl.addVal(MaybeVal.newBuilder().setVal(Val.newBuilder().setV(ht_v)).build());
                } else {
                    mvl.addVal(MaybeVal.newBuilder().build());
                }
            }

            mtx.unlock();

            responseObserver.onNext(mvl.build());
            responseObserver.onCompleted();
        }

        private void coord2PC(long id) {
            logger.info("Coordinating a synchronization");
            ArrayList<KeyVal> arg = new ArrayList<>();
            ArrayList<Long> ids = new ArrayList<>();

            for (Map.Entry<Long, LinkedBlockingQueue<KeyVal>> e : batch_logs.entrySet()) {
                if (!committed.containsKey(e.getKey()) || !committed.get(e.getKey())) {
                    // skip uncommitted batches
                    continue;
                }
                arg.addAll(e.getValue());
                ids.add(e.getKey());
            }

            for (KeyValServiceGrpc.KeyValServiceBlockingStub replica : replicas) {
                replica.commit(KVList.newBuilder().addAllKvs(arg).build());
            }

            // After all committed, we also update our own HT:
            for (KeyVal arg_kv : arg) {
                HT.put(arg_kv.getK(), arg_kv.getV());
            }

            // TODO: try to avoid removing this while blocked
            for (Long iD : ids) {
                batch_logs.remove(iD);
                committed.remove(iD);
            }

            logger.info("Finished coordinating the synchronization");
        }

        // (Replica-only) Commit
        @Override
        public void commit(KVList values, StreamObserver<Val> responseObserver) {
            //logger.info("committing key = " + kv.getK() + " | val = " + kv.getV());
            mtx.lock();
            for (KeyVal kv : values.getKvsList()) {
                HT.put(kv.getK(), kv.getV());
            }

            ArrayList<Long> ids = new ArrayList<>();

            // TODO: try to avoid removing this while blocked
            for (Map.Entry<Long, LinkedBlockingQueue<KeyVal>> e : batch_logs.entrySet()) {
                if (committed.containsKey(e.getKey())) {
                    ids.add(e.getKey());
                }
            }

            for (Long iD : ids) {
                batch_logs.remove(iD);
                committed.remove(iD);
            }

            mtx.unlock();
            responseObserver.onNext(Val.newBuilder().setV(1).build());
            responseObserver.onCompleted();
        }
    }
}
