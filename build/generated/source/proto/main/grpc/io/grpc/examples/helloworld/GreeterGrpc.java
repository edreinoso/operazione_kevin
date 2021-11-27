package io.grpc.examples.helloworld;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * The greeting service definition.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.42.0)",
    comments = "Source: helloworld.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GreeterGrpc {

  private GreeterGrpc() {}

  public static final String SERVICE_NAME = "helloworld.Greeter";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.grpc.examples.helloworld.Key,
      io.grpc.examples.helloworld.MaybeVal> getGetValMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetVal",
      requestType = io.grpc.examples.helloworld.Key.class,
      responseType = io.grpc.examples.helloworld.MaybeVal.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.grpc.examples.helloworld.Key,
      io.grpc.examples.helloworld.MaybeVal> getGetValMethod() {
    io.grpc.MethodDescriptor<io.grpc.examples.helloworld.Key, io.grpc.examples.helloworld.MaybeVal> getGetValMethod;
    if ((getGetValMethod = GreeterGrpc.getGetValMethod) == null) {
      synchronized (GreeterGrpc.class) {
        if ((getGetValMethod = GreeterGrpc.getGetValMethod) == null) {
          GreeterGrpc.getGetValMethod = getGetValMethod =
              io.grpc.MethodDescriptor.<io.grpc.examples.helloworld.Key, io.grpc.examples.helloworld.MaybeVal>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetVal"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.Key.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.MaybeVal.getDefaultInstance()))
              .setSchemaDescriptor(new GreeterMethodDescriptorSupplier("GetVal"))
              .build();
        }
      }
    }
    return getGetValMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.grpc.examples.helloworld.KeyVal,
      io.grpc.examples.helloworld.Val> getSetValMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetVal",
      requestType = io.grpc.examples.helloworld.KeyVal.class,
      responseType = io.grpc.examples.helloworld.Val.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.grpc.examples.helloworld.KeyVal,
      io.grpc.examples.helloworld.Val> getSetValMethod() {
    io.grpc.MethodDescriptor<io.grpc.examples.helloworld.KeyVal, io.grpc.examples.helloworld.Val> getSetValMethod;
    if ((getSetValMethod = GreeterGrpc.getSetValMethod) == null) {
      synchronized (GreeterGrpc.class) {
        if ((getSetValMethod = GreeterGrpc.getSetValMethod) == null) {
          GreeterGrpc.getSetValMethod = getSetValMethod =
              io.grpc.MethodDescriptor.<io.grpc.examples.helloworld.KeyVal, io.grpc.examples.helloworld.Val>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SetVal"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.KeyVal.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.Val.getDefaultInstance()))
              .setSchemaDescriptor(new GreeterMethodDescriptorSupplier("SetVal"))
              .build();
        }
      }
    }
    return getSetValMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.grpc.examples.helloworld.KeyVal,
      io.grpc.examples.helloworld.Val> getSet2PCValMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Set2PCVal",
      requestType = io.grpc.examples.helloworld.KeyVal.class,
      responseType = io.grpc.examples.helloworld.Val.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.grpc.examples.helloworld.KeyVal,
      io.grpc.examples.helloworld.Val> getSet2PCValMethod() {
    io.grpc.MethodDescriptor<io.grpc.examples.helloworld.KeyVal, io.grpc.examples.helloworld.Val> getSet2PCValMethod;
    if ((getSet2PCValMethod = GreeterGrpc.getSet2PCValMethod) == null) {
      synchronized (GreeterGrpc.class) {
        if ((getSet2PCValMethod = GreeterGrpc.getSet2PCValMethod) == null) {
          GreeterGrpc.getSet2PCValMethod = getSet2PCValMethod =
              io.grpc.MethodDescriptor.<io.grpc.examples.helloworld.KeyVal, io.grpc.examples.helloworld.Val>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Set2PCVal"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.KeyVal.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.Val.getDefaultInstance()))
              .setSchemaDescriptor(new GreeterMethodDescriptorSupplier("Set2PCVal"))
              .build();
        }
      }
    }
    return getSet2PCValMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.grpc.examples.helloworld.KVList,
      io.grpc.examples.helloworld.Val> getCommitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Commit",
      requestType = io.grpc.examples.helloworld.KVList.class,
      responseType = io.grpc.examples.helloworld.Val.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.grpc.examples.helloworld.KVList,
      io.grpc.examples.helloworld.Val> getCommitMethod() {
    io.grpc.MethodDescriptor<io.grpc.examples.helloworld.KVList, io.grpc.examples.helloworld.Val> getCommitMethod;
    if ((getCommitMethod = GreeterGrpc.getCommitMethod) == null) {
      synchronized (GreeterGrpc.class) {
        if ((getCommitMethod = GreeterGrpc.getCommitMethod) == null) {
          GreeterGrpc.getCommitMethod = getCommitMethod =
              io.grpc.MethodDescriptor.<io.grpc.examples.helloworld.KVList, io.grpc.examples.helloworld.Val>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Commit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.KVList.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.Val.getDefaultInstance()))
              .setSchemaDescriptor(new GreeterMethodDescriptorSupplier("Commit"))
              .build();
        }
      }
    }
    return getCommitMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.grpc.examples.helloworld.HelloRequest,
      io.grpc.examples.helloworld.HelloReply> getShutDownMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ShutDown",
      requestType = io.grpc.examples.helloworld.HelloRequest.class,
      responseType = io.grpc.examples.helloworld.HelloReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.grpc.examples.helloworld.HelloRequest,
      io.grpc.examples.helloworld.HelloReply> getShutDownMethod() {
    io.grpc.MethodDescriptor<io.grpc.examples.helloworld.HelloRequest, io.grpc.examples.helloworld.HelloReply> getShutDownMethod;
    if ((getShutDownMethod = GreeterGrpc.getShutDownMethod) == null) {
      synchronized (GreeterGrpc.class) {
        if ((getShutDownMethod = GreeterGrpc.getShutDownMethod) == null) {
          GreeterGrpc.getShutDownMethod = getShutDownMethod =
              io.grpc.MethodDescriptor.<io.grpc.examples.helloworld.HelloRequest, io.grpc.examples.helloworld.HelloReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ShutDown"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.HelloRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.examples.helloworld.HelloReply.getDefaultInstance()))
              .setSchemaDescriptor(new GreeterMethodDescriptorSupplier("ShutDown"))
              .build();
        }
      }
    }
    return getShutDownMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GreeterStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GreeterStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GreeterStub>() {
        @java.lang.Override
        public GreeterStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GreeterStub(channel, callOptions);
        }
      };
    return GreeterStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GreeterBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GreeterBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GreeterBlockingStub>() {
        @java.lang.Override
        public GreeterBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GreeterBlockingStub(channel, callOptions);
        }
      };
    return GreeterBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GreeterFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GreeterFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GreeterFutureStub>() {
        @java.lang.Override
        public GreeterFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GreeterFutureStub(channel, callOptions);
        }
      };
    return GreeterFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static abstract class GreeterImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Get Value for Key
     * </pre>
     */
    public void getVal(io.grpc.examples.helloworld.Key request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.MaybeVal> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetValMethod(), responseObserver);
    }

    /**
     * <pre>
     * Set Value for Key
     * </pre>
     */
    public void setVal(io.grpc.examples.helloworld.KeyVal request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Val> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSetValMethod(), responseObserver);
    }

    /**
     * <pre>
     * (Coordinator-only) Set Value for 2PC
     * </pre>
     */
    public void set2PCVal(io.grpc.examples.helloworld.KeyVal request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Val> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSet2PCValMethod(), responseObserver);
    }

    /**
     * <pre>
     * (Replica-only) Commit
     * </pre>
     */
    public void commit(io.grpc.examples.helloworld.KVList request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Val> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCommitMethod(), responseObserver);
    }

    /**
     * <pre>
     * Shut Down
     * </pre>
     */
    public void shutDown(io.grpc.examples.helloworld.HelloRequest request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getShutDownMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetValMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.grpc.examples.helloworld.Key,
                io.grpc.examples.helloworld.MaybeVal>(
                  this, METHODID_GET_VAL)))
          .addMethod(
            getSetValMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.grpc.examples.helloworld.KeyVal,
                io.grpc.examples.helloworld.Val>(
                  this, METHODID_SET_VAL)))
          .addMethod(
            getSet2PCValMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.grpc.examples.helloworld.KeyVal,
                io.grpc.examples.helloworld.Val>(
                  this, METHODID_SET2PCVAL)))
          .addMethod(
            getCommitMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.grpc.examples.helloworld.KVList,
                io.grpc.examples.helloworld.Val>(
                  this, METHODID_COMMIT)))
          .addMethod(
            getShutDownMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.grpc.examples.helloworld.HelloRequest,
                io.grpc.examples.helloworld.HelloReply>(
                  this, METHODID_SHUT_DOWN)))
          .build();
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class GreeterStub extends io.grpc.stub.AbstractAsyncStub<GreeterStub> {
    private GreeterStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreeterStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GreeterStub(channel, callOptions);
    }

    /**
     * <pre>
     * Get Value for Key
     * </pre>
     */
    public void getVal(io.grpc.examples.helloworld.Key request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.MaybeVal> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetValMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Set Value for Key
     * </pre>
     */
    public void setVal(io.grpc.examples.helloworld.KeyVal request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Val> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSetValMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * (Coordinator-only) Set Value for 2PC
     * </pre>
     */
    public void set2PCVal(io.grpc.examples.helloworld.KeyVal request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Val> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSet2PCValMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * (Replica-only) Commit
     * </pre>
     */
    public void commit(io.grpc.examples.helloworld.KVList request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Val> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCommitMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Shut Down
     * </pre>
     */
    public void shutDown(io.grpc.examples.helloworld.HelloRequest request,
        io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getShutDownMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class GreeterBlockingStub extends io.grpc.stub.AbstractBlockingStub<GreeterBlockingStub> {
    private GreeterBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreeterBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GreeterBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Get Value for Key
     * </pre>
     */
    public io.grpc.examples.helloworld.MaybeVal getVal(io.grpc.examples.helloworld.Key request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetValMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Set Value for Key
     * </pre>
     */
    public io.grpc.examples.helloworld.Val setVal(io.grpc.examples.helloworld.KeyVal request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSetValMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * (Coordinator-only) Set Value for 2PC
     * </pre>
     */
    public io.grpc.examples.helloworld.Val set2PCVal(io.grpc.examples.helloworld.KeyVal request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSet2PCValMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * (Replica-only) Commit
     * </pre>
     */
    public io.grpc.examples.helloworld.Val commit(io.grpc.examples.helloworld.KVList request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCommitMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Shut Down
     * </pre>
     */
    public io.grpc.examples.helloworld.HelloReply shutDown(io.grpc.examples.helloworld.HelloRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getShutDownMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * The greeting service definition.
   * </pre>
   */
  public static final class GreeterFutureStub extends io.grpc.stub.AbstractFutureStub<GreeterFutureStub> {
    private GreeterFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreeterFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GreeterFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Get Value for Key
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.helloworld.MaybeVal> getVal(
        io.grpc.examples.helloworld.Key request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetValMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Set Value for Key
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.helloworld.Val> setVal(
        io.grpc.examples.helloworld.KeyVal request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSetValMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * (Coordinator-only) Set Value for 2PC
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.helloworld.Val> set2PCVal(
        io.grpc.examples.helloworld.KeyVal request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSet2PCValMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * (Replica-only) Commit
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.helloworld.Val> commit(
        io.grpc.examples.helloworld.KVList request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCommitMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Shut Down
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.examples.helloworld.HelloReply> shutDown(
        io.grpc.examples.helloworld.HelloRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getShutDownMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_VAL = 0;
  private static final int METHODID_SET_VAL = 1;
  private static final int METHODID_SET2PCVAL = 2;
  private static final int METHODID_COMMIT = 3;
  private static final int METHODID_SHUT_DOWN = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GreeterImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GreeterImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_VAL:
          serviceImpl.getVal((io.grpc.examples.helloworld.Key) request,
              (io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.MaybeVal>) responseObserver);
          break;
        case METHODID_SET_VAL:
          serviceImpl.setVal((io.grpc.examples.helloworld.KeyVal) request,
              (io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Val>) responseObserver);
          break;
        case METHODID_SET2PCVAL:
          serviceImpl.set2PCVal((io.grpc.examples.helloworld.KeyVal) request,
              (io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Val>) responseObserver);
          break;
        case METHODID_COMMIT:
          serviceImpl.commit((io.grpc.examples.helloworld.KVList) request,
              (io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Val>) responseObserver);
          break;
        case METHODID_SHUT_DOWN:
          serviceImpl.shutDown((io.grpc.examples.helloworld.HelloRequest) request,
              (io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class GreeterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GreeterBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.grpc.examples.helloworld.HelloWorldProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Greeter");
    }
  }

  private static final class GreeterFileDescriptorSupplier
      extends GreeterBaseDescriptorSupplier {
    GreeterFileDescriptorSupplier() {}
  }

  private static final class GreeterMethodDescriptorSupplier
      extends GreeterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GreeterMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GreeterGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GreeterFileDescriptorSupplier())
              .addMethod(getGetValMethod())
              .addMethod(getSetValMethod())
              .addMethod(getSet2PCValMethod())
              .addMethod(getCommitMethod())
              .addMethod(getShutDownMethod())
              .build();
        }
      }
    }
    return result;
  }
}
