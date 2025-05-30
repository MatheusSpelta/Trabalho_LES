package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import jakarta.annotation.PreDestroy;

@Service
public class BalancaService {
    private SerialPort serialPort;
    private boolean portaAberta = false;
    private final Logger logger = LoggerFactory.getLogger(BalancaService.class);
    private volatile Double ultimoPeso = 0.0;

    private class BalancaDataListener implements SerialPortDataListener {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;

            int available = serialPort.bytesAvailable();
            if (available <= 0) return; // Evita NegativeArraySizeException

            byte[] newData = new byte[available];
            int numRead = serialPort.readBytes(newData, newData.length);
            if (numRead > 0) {
                buffer.write(newData, 0, numRead);
                processBuffer();
            } else {
                ultimoPeso = 0.0;
            }
        }

        private void processBuffer() {
            byte[] data = buffer.toByteArray();
            int i = 0;
            int dataLength = data.length;

            while (i < dataLength) {
                if (data[i] == 0x02) {
                    if (i + 7 < dataLength) {
                        if (data[i + 7] == 0x0D) {
                            byte[] weightBytes = Arrays.copyOfRange(data, i + 1, i + 7);
                            String weightStr = new String(weightBytes, StandardCharsets.US_ASCII);
                            try {
                                double weight = Double.parseDouble(weightStr);
                                logger.info("Peso lido: {}", weight);
                                ultimoPeso = weight;
                            } catch (NumberFormatException e) {
                                logger.error("Formato inválido do peso: {}", weightStr, e);
                            }
                            i += 8;
                            continue;
                        } else {
                            i++;
                        }
                    } else {
                        break;
                    }
                }
                i++;
            }
            byte[] remaining = Arrays.copyOfRange(data, i, dataLength);
            buffer.reset();
            buffer.write(remaining, 0, remaining.length);
        }
    }

    public boolean init(String porta) {
        closePort(); // Fecha a porta anterior, se aberta
        try {
            serialPort = SerialPort.getCommPort(porta);
            serialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
            portaAberta = serialPort.openPort();
            if (portaAberta) {
                serialPort.addDataListener(new BalancaDataListener());
                logger.info("Porta {} aberta com sucesso.", porta);
                return true;
            } else {
                logger.error("Não foi possível abrir a porta {}", porta);
                return false;
            }
        } catch (Exception e) {
            logger.error("Erro ao abrir porta serial: {}", e.getMessage());
            return false;
        }
    }

    public void closePort() {
        if (serialPort != null && serialPort.isOpen()) {
            boolean closed = serialPort.closePort();
            if (closed) {
                logger.info("Porta {} fechada com sucesso.", serialPort.getSystemPortName());
            } else {
                logger.error("Falha ao fechar a porta {}", serialPort.getSystemPortName());
            }
        }
    }

    @PreDestroy
    public void onShutdown() {
        closePort();
    }

    public Double getUltimoPeso() {
        BalancaDataListener listener = new BalancaDataListener();
        listener.serialEvent(new SerialPortEvent(serialPort, SerialPort.LISTENING_EVENT_DATA_AVAILABLE));
        return ultimoPeso;
    }
}