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

public class Receiver {
    private final int bufferSize;
    private final DatagramSocket server;
    private final Logger logger;
    private final Executor executor;
    private final UsersHandler usersHandler;

    public Receiver(DatagramSocket server, int bufferSize, Logger logger, Executor executor, UsersHandler usersHandler) {
        this.server = server;
        this.bufferSize = bufferSize;
        this.logger = logger;
        this.executor = executor;
        this.usersHandler = usersHandler;
    }

    public void receive() throws IOException, ClassNotFoundException {
        byte[] bytesReceiving = new byte[bufferSize];
        DatagramPacket request = new DatagramPacket(bytesReceiving, bytesReceiving.length);
        server.receive(request);
        Object received = Serializer.deserialize(bytesReceiving);
        InetAddress client = request.getAddress();
        int port = request.getPort();
        logger.info("received request from address " + client + ", port " + port);
        Object response;
        if (received instanceof PullingRequest) {
            response = usersHandler.handle((PullingRequest) received);
        } else {
            ClientRequest clientRequest = (ClientRequest) received;
            User user = new User(clientRequest.getUsername(), PasswordEncoder.encode(clientRequest.getPassword()));
            if (usersHandler.checkUser(user)) {
                response = executor.executeCommand(clientRequest.getCommandName(), clientRequest.getCommandArguments(),
                        clientRequest.getObjectArgument(), user.getUsername());
            } else {
                response = new ServerResponse("commands can only be executed by authorized users", ExecuteCode.ERROR);
            }
        }
        byte[] bytesSending = Serializer.serialize(response);
        DatagramPacket packet = new DatagramPacket(bytesSending, bytesSending.length, client, port);
        server.send(packet);
        logger.info("response sent to the address " + client + ", port " + port);
    }
}
