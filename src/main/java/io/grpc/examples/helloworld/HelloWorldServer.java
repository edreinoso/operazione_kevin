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

package io.grpc.examples.helloworld;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class HelloWorldServer {
  private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());

  private Server server;

  private void start(int myPort, int firstPort, int lastPort) throws IOException, InterruptedException {

    GreeterImpl gi = new GreeterImpl(myPort, firstPort, lastPort);
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
          HelloWorldServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });

    // hacky: wait for all to get setup:
    TimeUnit.SECONDS.sleep( 2 );

    ArrayList<ManagedChannel> arr = new ArrayList<>();
    for (int i = firstPort; i <= lastPort; ++i)
    {
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
    final HelloWorldServer server = new HelloWorldServer();

    int myport = Integer.parseInt(args[0]);
    int lowest_port = Integer.parseInt(args[1]);
    int highest_port = Integer.parseInt(args[2]);
    server.start(myport, lowest_port, highest_port);
    server.blockUntilShutdown();
  }

  static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    ConcurrentHashMap<Long, Long> HT;

    // leader node is the first one of the replicas (if we are not the leader)
    ArrayList<GreeterGrpc.GreeterBlockingStub> replicas;
    boolean leader;

    public GreeterImpl () {
      HT = new ConcurrentHashMap<>();
      leader = true;
    }

    public GreeterImpl (int myPort, int firstPort, int lastPort) {
      HT = new ConcurrentHashMap<>();
    }

    public void setReplicas(ArrayList<ManagedChannel> chns, boolean leader) {
      replicas = new ArrayList<>();
      for (ManagedChannel mc : chns)
      {
        replicas.add(GreeterGrpc.newBlockingStub(mc));
      }

      this.leader = leader;
    }

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public void sayHelloAgain(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Hello again " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public void setVal(KeyVal kv, StreamObserver<Val> responseObserver) {
      HT.put(kv.getK(), kv.getV());
      Val v = Val.newBuilder().setV(kv.getV()).build();

      if (!leader) {
        logger.info("starting 2pc");
        replicas.get(0).set2PCVal(kv);
      } else {
        coord2PC(kv);
      }

      responseObserver.onNext(v);
      responseObserver.onCompleted();
    }

    @Override
    public void getVal(Key k, StreamObserver<Val> responseObserver) {
      long ht_v = HT.get(k.getK());
      Val v = Val.newBuilder().setV(ht_v).build();

      responseObserver.onNext(v);
      responseObserver.onCompleted();
    }

    private Val coord2PC (KeyVal kv) {

      logger.info("coordinating a 2pc thing");
      for (GreeterGrpc.GreeterBlockingStub replica : replicas) {
        replica.prepare(kv);
      }
      for (GreeterGrpc.GreeterBlockingStub replica : replicas) {
        replica.commit(kv);
      }

      // After all committed, we also update our own HT:
      HT.put(kv.getK(), kv.getV());
      return Val.newBuilder().setV(kv.getV()).build();
    }

    // (Coordinator-only) Set Value for 2PC
    @Override
    public void set2PCVal (KeyVal kv, StreamObserver<Val> responseObserver) {
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
    @Override
    public void prepare (KeyVal kv, StreamObserver<Val> responseObserver) {
      if (leader) {
        // somebody is trolling us!
        responseObserver.onNext(Val.newBuilder().setV(0).build());
        responseObserver.onCompleted();
        return;
      }
      // Do nothing, just respond ok
      Val v = Val.newBuilder().setV(kv.getV()).build();

      responseObserver.onNext(v);
      responseObserver.onCompleted();
    }

    // (Replica-only) Commit
    @Override
    public void commit (KeyVal kv, StreamObserver<Val> responseObserver) {
      if (leader) {
        // somebody is trolling us!
        responseObserver.onNext(Val.newBuilder().setV(0).build());
        responseObserver.onCompleted();
        return;
      }
      logger.info("committing key = " + kv.getK() + " | val = " + kv.getV());
      HT.put(kv.getK(), kv.getV());
      Val v = Val.newBuilder().setV(kv.getV()).build();

      responseObserver.onNext(v);
      responseObserver.onCompleted();
    }

    @Override
    public void shutDown (HelloRequest req, StreamObserver<HelloReply> responseObserver) {

      HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();

      System.exit(0);
    }
  }
}
