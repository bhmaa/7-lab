package com.bhma.server.util;

import java.net.InetAddress;

public class ReceivedData {
    private final Object data;
    private final InetAddress client;
    private final int port;

    public ReceivedData(Object data, InetAddress client, int port) {
        this.data = data;
        this.client = client;
        this.port = port;
    }

    public Object getData() {
        return data;
    }

    public InetAddress getClient() {
        return client;
    }

    public int getPort() {
        return port;
    }
}
