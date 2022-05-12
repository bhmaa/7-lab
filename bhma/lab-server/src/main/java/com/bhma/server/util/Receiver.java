package com.bhma.server.util;

import com.bhma.common.util.ClientRequest;
import com.bhma.common.util.ExecuteCode;
import com.bhma.common.util.PullingRequest;
import com.bhma.common.util.Serializer;
import com.bhma.common.util.ServerResponse;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class Receiver {
    private final int bufferSize;
    private final DatagramSocket server;
    private final Logger logger;
    private final Executor executor;
    private final UsersHandler usersHandler;

    public Receiver(DatagramSocket server, int bufferSize, Logger logger, Executor executor,
                    UsersHandler usersHandler) {
        this.server = server;
        this.bufferSize = bufferSize;
        this.logger = logger;
        this.executor = executor;
        this.usersHandler = usersHandler;
    }

    public void start(ExecutorService requestReadingPool, ExecutorService requestProcessingPool,
                      ExecutorService responseSendingPool) throws ExecutionException, InterruptedException {
        while (true) {
            requestReadingPool.submit(() -> {
                ReceivedData receivedData = receive();
                Object request = receivedData.getRequest();
                InetAddress client = receivedData.getClient();
                int port = receivedData.getPort();
                try {
                    requestProcessingPool.submit(() -> {
                        Object response = processRequest(request);
                        try {
                            responseSendingPool.submit(() -> sendResponse(response, client, port)).get();
                            responseSendingPool.shutdown();
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error(e);
                        }
                    }).get();
                    requestProcessingPool.shutdown();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e);
                }
            });
        }
    }

    private Object processRequest(Object received) {
        if (received instanceof PullingRequest) {
            return usersHandler.handle((PullingRequest) received);
        } else {
            ClientRequest clientRequest = (ClientRequest) received;
            User user = new User(clientRequest.getUsername(), PasswordEncoder.encode(clientRequest.getPassword()));
            if (usersHandler.checkUser(user)) {
                return executor.executeCommand(clientRequest.getCommandName(), clientRequest.getCommandArguments(),
                        clientRequest.getObjectArgument(), user.getUsername());
            } else {
                return new ServerResponse("commands can only be executed by authorized users", ExecuteCode.ERROR);
            }
        }
    }

    private boolean sendResponse(Object response, InetAddress client, int port) {
        try {
            byte[] bytesSending = Serializer.serialize(response);
            DatagramPacket packet = new DatagramPacket(bytesSending, bytesSending.length, client, port);
            server.send(packet);
            logger.info("response sent to the address " + client + ", port " + port);
        } catch (IOException e) {
            logger.error("error during sending response", e);
        }
        return true;
    }

    private ReceivedData receive() {
        ReceivedData receivedData = null;
        try {
            byte[] bytesReceiving = new byte[bufferSize];
            DatagramPacket request = new DatagramPacket(bytesReceiving, bytesReceiving.length);
            server.receive(request);
            Object received = Serializer.deserialize(bytesReceiving);
            InetAddress client = request.getAddress();
            int port = request.getPort();
            receivedData = new ReceivedData(received, client, port);
            logger.info(() -> "received request from address " + client + ", port " + port);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("error during reading response", e);
        }
        return receivedData;
    }
}
