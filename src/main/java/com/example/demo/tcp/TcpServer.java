package com.example.demo.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.service.ClienteService;

@Component
public class TcpServer {

    private static final int PORT = 5000;

    @Autowired
    private ClienteService clienteService;

    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Servidor TCP iniciado na porta " + PORT);

                while (true) {
                    // Aguarda uma conexão do terminal
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Conexão recebida de: " + clientSocket.getInetAddress());

                    // Processa a requisição do terminal
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            OutputStream out = clientSocket.getOutputStream()) {

                        // Lê o número do cartão enviado pelo terminal
                        String cartao = in.readLine();
                        System.out.println("Número do cartão recebido: " + cartao);

                        // Busca o cliente pelo número do cartão
                        try {
                            var cliente = clienteService.findByCartao(cartao);

                            // Monta a resposta com os saldos
                            String response = String.format("Saldo Débito: %.2f, Limite Crédito: %.2f",
                                    cliente.getSaldoDebito(), cliente.getLimiteCredito());

                            // Envia a resposta para o terminal
                            out.write((response + "\n").getBytes());
                            out.flush();
                        } catch (Exception e) {
                            // Envia uma mensagem de erro caso o cliente não seja encontrado
                            out.write(("Erro: Cliente não encontrado\n").getBytes());
                            out.flush();
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao processar a requisição: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao iniciar o servidor TCP: " + e.getMessage());
            }
        }).start();
    }

}
