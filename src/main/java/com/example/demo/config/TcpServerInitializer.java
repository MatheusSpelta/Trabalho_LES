package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.tcp.TcpServer;

import jakarta.annotation.PostConstruct;

@Component
public class TcpServerInitializer {

    @Autowired
    private TcpServer tcpServer;

    @PostConstruct
    public void init() {
        tcpServer.startServer();
    }
}