package com.bhma.server.util;

import java.net.InetAddress;

public class ReceivedData {
    private final Object request;
    private final InetAddress client;
    private final int port;

    public ReceivedData(Object request, InetAddress client, int port) {
        this.request = request;
        this.client = client;
        this.port = port;
    }

    public Object getRequest() {
        return request;
    }

    public InetAddress getClient() {
        return client;
    }

    public int getPort() {
        return port;
    }
}
