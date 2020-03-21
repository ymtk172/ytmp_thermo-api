package com.yamalc.ytmp.thermoapi;

import java.io.IOException;
import java.util.logging.Logger;


import com.yamalc.ytmp.grpc.thermo.BodyTemperatureResponse;
import com.yamalc.ytmp.grpc.thermo.ThermoGrpc;
import com.yamalc.ytmp.grpc.thermo.UserIdRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class ThermoApiServer {
    Logger logger = Logger.getLogger(getClass().getName());

    Server server;

    public static void main(String... args) throws IOException, InterruptedException {
        ThermoApiServer userApiServer = new ThermoApiServer();
        userApiServer.start();

        //simpleServer.blockUntilShutdown();

        System.console().readLine("> Enter stop.");

        userApiServer.stop();
    }

    public void start() throws IOException {
        server =
                ServerBuilder
                        .forPort(8082)
                        .addService(new AuthenticateServiceImpl())
                        .build()
                        .start();

        logger.info("start gRPC server.");
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
            logger.info("shutdown gRPC server.");
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static class AuthenticateServiceImpl extends ThermoGrpc.ThermoImplBase {
        Logger logger = Logger.getLogger(getClass().getName());

        @Override
        public void latestBodyTemperature(UserIdRequest request,
                                          StreamObserver<BodyTemperatureResponse> responseObserver) {
            logger.info(String.format("request: id = %s", request.getId()));

            BodyTemperatureResponse response =
                    BodyTemperatureResponse
                            .newBuilder()
                            .setId(request.getId())
                            .setBodyTemperature(36.5)
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void latestHealthCheck(UserIdRequest request,
                                          StreamObserver<BodyTemperatureResponse> responseObserver) {
            logger.info(String.format("request: id = %s", request.getId()));

            BodyTemperatureResponse response =
                    BodyTemperatureResponse
                            .newBuilder()
                            .setId(request.getId())
                            .setBodyTemperature(36.6)
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void recentlyHealthCheck(UserIdRequest request,
                                          StreamObserver<BodyTemperatureResponse> responseObserver) {
            logger.info(String.format("request: id = %s", request.getId()));

            BodyTemperatureResponse response =
                    BodyTemperatureResponse
                            .newBuilder()
                            .setId(request.getId())
                            .setBodyTemperature(36.3)
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void normalBodyTemperature(UserIdRequest request,
                                          StreamObserver<BodyTemperatureResponse> responseObserver) {
            logger.info(String.format("request: id = %s", request.getId()));

            BodyTemperatureResponse response =
                    BodyTemperatureResponse
                            .newBuilder()
                            .setId(request.getId())
                            .setBodyTemperature(36.0)
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
