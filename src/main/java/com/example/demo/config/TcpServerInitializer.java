package com.example.demo.config;

import com.example.demo.tcp.TcpServer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TcpServerInitializer {

    private final TcpServer tcpServer;

    @Autowired
    public TcpServerInitializer(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
    }

    @PostConstruct
    public void startTcpServer() {
        Thread tcpServerThread = new Thread(tcpServer);
        tcpServerThread.setDaemon(true); // Permite que a aplicação finalize mesmo se o thread estiver rodando
        tcpServerThread.start();
    }
}