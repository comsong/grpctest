package com.comsong.server;

import grpc.hello.HelloGrpc;
import grpc.hello.HelloReply;
import grpc.hello.HelloRequest;
import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;

public class HelloServerImpl extends HelloGrpc.HelloImplBase {
  private static final Logger logger = Logger.getLogger(HelloServerImpl.class.getName());

  @Override
  public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
    ;
    logger.info("Greeting: " + request.getName());
    responseObserver.onNext(HelloReply.newBuilder().setMessage("hello").build());
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<HelloRequest> sayHelloStreaming(
      StreamObserver<HelloReply> responseObserver) {
    return new StreamObserver<HelloRequest>() {
      @Override
      public void onNext(HelloRequest helloRequest) {
//        System.out.println(helloRequest.getName());
        logger.info("Greeting: " + helloRequest.getName());
        responseObserver.onNext(HelloReply.newBuilder().setMessage("Hello").build());
      }

      @Override
      public void onError(Throwable throwable) {
        logger.info("onError호출"+throwable.getMessage());
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
        logger.info("Done");
      }
    };
  }
}
/*static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

  @Override
  public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
    HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }
}*/
