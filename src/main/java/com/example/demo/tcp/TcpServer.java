package com.example.demo.tcp;

import com.example.demo.model.Cliente;
import com.example.demo.service.ClienteService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class TcpServer implements Runnable {

    private static final String CLEAR_SCREEN = "\u001B[2J\u001B[0;0f";

    private final ClienteService clienteService;

    private String codigoCartaoCliente = "";

    public TcpServer(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server started on port " + serverSocket.getLocalPort() + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client " + socket.getRemoteSocketAddress() + " connected to server...");
                handleClient(socket);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    private void processInput(String input, DataOutputStream dos) throws IOException {
        // Limpa a tela antes de exibir as informações
        dos.writeBytes(CLEAR_SCREEN);
        dos.flush();

        // Exibe as teclas digitadas em tempo real
        for (char c : input.toCharArray()) {
            dos.writeBytes(String.valueOf(c)); // Envia cada caractere digitado
            dos.flush();
        }

        dos.writeBytes("\r\n"); // Quebra de linha após a digitação completa
        dos.flush();

        // Processa o código do cartão
        codigoCartaoCliente = input.trim();
        verificarContaCliente(codigoCartaoCliente, dos);

        // Pausa antes de retornar à tela inicial
        try {
            Thread.sleep(5000); // Pausa de 5 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Retorna à tela inicial
        dos.writeBytes(CLEAR_SCREEN + "\r\nDigite o numero do cartao\r\n");
        dos.flush();
    }

    private void verificarContaCliente(String codigo, DataOutputStream dos) throws IOException {
        try {
            Cliente cliente = clienteService.findByCartao(codigo);
//            if (cliente == null) {
//                throw new IllegalArgumentException("Cliente não encontrado para o cartão: " + codigo);
//            }


            String resposta = String.format(
                    "Cartao: %s\r\nMatricula: %s\r\nDebito: RS %.2f\r\nCredito: RS %.2f",
                    codigo, cliente.getMatricula(), cliente.getSaldoDebito(), cliente.getLimiteCredito());

            System.out.println(resposta);
            dos.write(resposta.getBytes("UTF-8")); // Envia os dados com codificação UTF-8
            dos.flush();
        } catch (Exception e) {

            dos.flush();
            String erro = String.format("Cartao: " + codigo + " nao encontrado!\r\n");
            System.out.println(erro);
            dos.write(erro.getBytes("UTF-8")); // Envia os dados com codificação UTF-8
            dos.flush();
        }
    }


    private void handleClient(Socket socket) throws IOException {
        try (InputStream in = socket.getInputStream();
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            dos.writeBytes(CLEAR_SCREEN + "\r\nDigite o numero do cartao:\r\n");
            dos.flush();

            int character;
            StringBuilder inputBuilder = new StringBuilder();

            while ((character = in.read()) != -1) {
                char c = (char) character;

                // Detecta a tecla Enter
                if (character == 13) {
                    String input = inputBuilder.toString().trim();
                    if (!input.isEmpty()) {
                        processInput(input, dos); // Realiza a consulta
                    }
                    inputBuilder.setLength(0); // Limpa o buffer após a consulta
                }
                // Detecta a tecla Del (código 127)
                else if (character == 127) {
                    inputBuilder.setLength(0); // Limpa o buffer, mas não atualiza o terminal
                }
                // Adiciona outros caracteres ao buffer
                else {
                    inputBuilder.append(c);
                    dos.writeBytes(String.valueOf(c)); // Exibe o caractere digitado
                    dos.flush();
                }
            }
        }
    }
}