package com.comsong.client;

import grpc.hello.HelloGrpc;
import grpc.hello.HelloReply;
import grpc.hello.HelloRequest;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloClient {
  private static final Logger logger = Logger.getLogger(HelloClient.class.getName());

  private final HelloGrpc.HelloStub asyncStub;
  private final HelloGrpc.HelloBlockingStub blockingStub;

  private static Boolean check = false;

  /** Construct client for accessing HelloWorld server using the existing channel. */
  public HelloClient(Channel channel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    asyncStub = HelloGrpc.newStub(channel);
    blockingStub = HelloGrpc.newBlockingStub(channel);
  }

  public void greetUnary(String name) {
    String response = blockingStub.sayHello(HelloRequest.newBuilder().setName(name).build()).getMessage();
    logger.info("Greeting: " + response);
  }

  /** Say hello to server. */
  public void greet(String name) {
    logger.info("Will try to greet " + name + " ...");
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;
    try {
      StreamObserver<HelloRequest> requestObserver = asyncStub.sayHelloStreaming(
          new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply helloReply) {
              logger.info("Greeting: " + helloReply.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
              logger.info("onError호출"+throwable.getMessage());
            }

            @Override
            public void onCompleted() {
              logger.info("on Completed" );
              check = true;
            }
          });


//      requestObserver.onNext(HelloRequest.newBuilder().setName(name).build());
      requestObserver.onNext(HelloRequest.newBuilder().setName("test1").build());
      requestObserver.onNext(HelloRequest.newBuilder().setName("test2").build());
      logger.info("async니까 바로 로그 찍힘");
      requestObserver.onCompleted();
      while(true){
        if (check){
          logger.info("check!!");
          return;
        } else {
          Thread.sleep(1000);
          logger.info("net yet!!");
        }
      }

    } catch (StatusRuntimeException | InterruptedException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStackTrace());
      return;
    }
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting. The second argument is the target server.
   */
  public static void main(String[] args) throws Exception {
    String user = "Bryan Song";
    // Access a service running on the local machine on port 50051
    String target = "localhost:50051";
    // Allow passing in the user and target strings as command line arguments
    if (args.length > 0) {
      if ("--help".equals(args[0])) {
        System.err.println("Usage: [name [target]]");
        System.err.println("");
        System.err.println("  name    The name you wish to be greeted by. Defaults to " + user);
        System.err.println("  target  The server to connect to. Defaults to " + target);
        System.exit(1);
      }
      user = args[0];
    }
    if (args.length > 1) {
      target = args[1];
    }

    // Create a communication channel to the server, known as a Channel. Channels are thread-safe
    // and reusable. It is common to create channels at the beginning of your application and reuse
    // them until the application shuts down.
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build();
    try {
      HelloClient client = new HelloClient(channel);
      client.greet(user);
//      client.greetUnary(user);
    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
      // resources the channel should be shut down when it will no longer be used. If it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}

