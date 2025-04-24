package com.example.demo.tcp;

import com.example.demo.service.ClienteService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class TcpServer {

    private static final String CLEAR_SCREEN = "\u001B[2J\u001B[0;0f";

    private final ClienteService clienteService;

    private String codigoCartaoCliente = "";
    private boolean searchProduct = false;

    public TcpServer(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostConstruct
    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server started on port " + serverSocket.getLocalPort() + "...");
            System.out.println(serverSocket.getLocalSocketAddress());
            System.out.println(serverSocket.getReuseAddress());
            System.out.println(serverSocket.getChannel());
            System.out.println(serverSocket.getReceiveBufferSize());
            System.out.println(serverSocket.getInetAddress());

            System.out.println("Waiting for client...");

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Client " + socket.getRemoteSocketAddress() + " connected to server...");

                    try (BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                         DataInputStream dis = new DataInputStream(bis);
                         DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        while (true) {
                            try {
                                int tecla = in.read() - 48; // Lê a tecla pressionada
                                processKey(tecla, dos);

                                if (!searchProduct) {
                                    dos.writeBytes("<START>Codigo do cartao: " + codigoCartaoCliente + "<END>\r\n");
                                    dos.flush();
                                }

                                System.out.println(codigoCartaoCliente);
                            } catch (IOException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }

                    System.out.println("Client " + socket.getRemoteSocketAddress() + " disconnected from server...");
                } catch (IOException e) {
                    System.out.println("Error: " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    private void processKey(int key, DataOutputStream dos) throws IOException {
        dos.writeBytes(CLEAR_SCREEN + "\r\n"); // Limpa a tela no terminal

        switch (key) {
            case -2: // Tecla .
            case 40: // Tecla X
                break;
            case -35: // Tecla ENTER
                if (searchProduct) {
                    searchProduct = false;
                    codigoCartaoCliente = "";
                    break;
                }
                verificarContaCliente(codigoCartaoCliente, dos);
                break;
            case -40: // Tecla BACKSPACE
                if (codigoCartaoCliente.length() == 0) {
                    break;
                }
                codigoCartaoCliente = codigoCartaoCliente.substring(0, codigoCartaoCliente.length() - 1);
                break;
            case -21: // Tecla DELETE
                codigoCartaoCliente = "";
                break;
            default:
                if (codigoCartaoCliente.length() < 14) {
                    codigoCartaoCliente = codigoCartaoCliente.concat(String.valueOf(key));
                }
        }

        // Envia o código atualizado ao cliente
        dos.writeBytes("Codigo do cartao: " + codigoCartaoCliente + "\r\n");
        dos.flush(); // Garante que os dados sejam enviados imediatamente
    }

    private void verificarContaCliente(String codigo, DataOutputStream dos) throws IOException {
        try {
            var cliente = clienteService.findByCartao(codigo);
            String saldo = String.format(
                    "Saldo Débito: R$ %.2f\r\nLimite Crédito: R$ %.2f",
                    cliente.getSaldoDebito(), cliente.getLimiteCredito());
            System.out.println(saldo);
            dos.writeBytes("Codigo: " + codigo + "\r\n" + saldo + "\r\n");
            dos.flush(); // Garante que os dados sejam enviados imediatamente
        } catch (Exception e) {
            System.out.println("Deu erro");
            dos.writeBytes("Erro ao consultar cliente: " + e.getMessage() + "\r\n");
            dos.flush(); // Garante que os dados sejam enviados imediatamente
        }
    }
}