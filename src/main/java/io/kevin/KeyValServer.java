/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kevin;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
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

    private void start(int myPort, int firstPort, int lastPort) throws IOException, InterruptedException {

        keyValImpl gi = new keyValImpl(myPort, firstPort, lastPort);
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
        TimeUnit.SECONDS.sleep(2);

        ArrayList<ManagedChannel> arr = new ArrayList<>();
        for (int i = firstPort; i <= lastPort; ++i) {
            // skip ourselves
            if (i == myPort) continue;

            String target = "localhost:" + String.valueOf(i);
            arr.add(ManagedChannelBuilder.forTarget(target).usePlaintext().build());
        }

        gi.setReplicas(arr, myPort == firstPort);

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

        int myport = Integer.parseInt(args[0]);
        int lowest_port = Integer.parseInt(args[1]);
        int highest_port = Integer.parseInt(args[2]);
        server.start(myport, lowest_port, highest_port);
        server.blockUntilShutdown();
    }

    static class keyValImpl extends KeyValServiceGrpc.KeyValServiceImplBase {

        ConcurrentHashMap<Long, Long> HT;
        LinkedBlockingQueue<KeyVal> log;
        Lock mtx;

        // leader node is the first one of the replicas (if we are not the leader)
        ArrayList<KeyValServiceGrpc.KeyValServiceBlockingStub> replicas;
        boolean leader;

        public keyValImpl() {
            HT = new ConcurrentHashMap<>();
            log = new LinkedBlockingQueue<>();
            mtx = new ReentrantLock();
            leader = true;
        }

        public keyValImpl(int myPort, int firstPort, int lastPort) {
            HT = new ConcurrentHashMap<>();
            log = new LinkedBlockingQueue<>();
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
            log.add(kv);

            if (leader)
                coord2PC(kv);
            mtx.unlock();
            Val v = Val.newBuilder().setV(kv.getV()).build();

            responseObserver.onNext(v);
            responseObserver.onCompleted();
        }

        @Override
        public void getVal(Key k, StreamObserver<MaybeVal> responseObserver) {
            mtx.lock();
            MaybeVal v;
            if (HT.containsKey(k.getK())) {
                long ht_v = HT.get(k.getK());
                v = MaybeVal.newBuilder().setVal(Val.newBuilder().setV(ht_v)).build();
            } else {
                v = MaybeVal.newBuilder().build();
            }

            mtx.unlock();

            responseObserver.onNext(v);
            responseObserver.onCompleted();
        }

        private Val coord2PC(KeyVal kv) {
            logger.info("Coordinating a synchronization");
            ArrayList<KeyVal> arg = new ArrayList<>();
            while (!log.isEmpty()) {
                try {
                    arg.add(log.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (KeyValServiceGrpc.KeyValServiceBlockingStub replica : replicas) {
                replica.commit(KVList.newBuilder().addAllKvs(arg).build());
            }

            // After all committed, we also update our own HT:
            for (KeyVal arg_kv : arg) {
                HT.put(arg_kv.getK(), arg_kv.getV());
            }

            logger.info("Finished coordinating the synchronization");

            return Val.newBuilder().setV(kv.getV()).build();
        }

        // (Coordinator-only) Set Value for 2PC
        @Override
        public void set2PCVal(KeyVal kv, StreamObserver<Val> responseObserver) {
            if (!leader) {
                // somebody is trolling us!
                responseObserver.onNext(Val.newBuilder().setV(0).build());
                responseObserver.onCompleted();
                return;
            }

            Val v = coord2PC(kv);

            responseObserver.onNext(v);
            responseObserver.onCompleted();
        }

        // (Replica-only) Prepare
//    @Override
//    public void prepare (KeyVal kv, StreamObserver<Val> responseObserver) {
//      if (leader) {
//        assert(false);
//        responseObserver.onNext(Val.newBuilder().setV(0).build());
//        responseObserver.onCompleted();
//        return;
//      }
//      // Do nothing, just respond ok
//      Val v = Val.newBuilder().setV(kv.getV()).build();
//
//      responseObserver.onNext(v);
//      responseObserver.onCompleted();
//    }

        // (Replica-only) Commit
        @Override
        public void commit(KVList values, StreamObserver<Val> responseObserver) {
            //logger.info("committing key = " + kv.getK() + " | val = " + kv.getV());
            mtx.lock();
            for (KeyVal kv : values.getKvsList()) {
                HT.put(kv.getK(), kv.getV());
            }

            mtx.unlock();
            responseObserver.onNext(Val.newBuilder().setV(1).build());
            responseObserver.onCompleted();
        }
    }
}
