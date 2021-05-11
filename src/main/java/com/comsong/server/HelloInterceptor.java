package com.comsong.server;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCallListener;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.logging.Logger;

public class HelloInterceptor implements ServerInterceptor {
  private static final Logger logger = Logger.getLogger(HelloInterceptor.class.getName());
/*  private final MethodOptionsRegistry reg;

  HelloInterceptor(MethodOptionsRegistry reg) {
    this.reg = reg;
  }*/
  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
      Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
//    String name = serverCall.getMethodDescriptor().getFullMethodName();
    // TODO: do something with options...
    final Listener<ReqT> original = serverCallHandler.startCall(serverCall, metadata);
//    Contexts

    return new SimpleForwardingServerCallListener<ReqT>(original) {
      @Override
      protected Listener<ReqT> delegate() {
        logger.info("delegate");
        return super.delegate();
      }

      @Override
      public void onMessage(ReqT message) {
        logger.info("onMessage : "+message.toString());
        super.onMessage(message);
      }

      @Override
      public void onHalfClose() {
        logger.info("onHalfClose");
        super.onHalfClose();
      }

      @Override
      public void onCancel() {
        logger.info("onCancel");
        super.onCancel();
      }

      @Override
      public void onComplete() {
        logger.info("onComplete");
        super.onComplete();
      }

      @Override
      public void onReady() {
        logger.info("onReady");
        super.onReady();
      }
    };
  }
}
