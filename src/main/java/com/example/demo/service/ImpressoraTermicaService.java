package com.example.demo.service;

import com.example.demo.model.Cliente;
import com.example.demo.model.Produto;
import com.example.demo.model.VendaProduto;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.PrinterOutputStream;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
class ImpressoraTermicaService {
    private static final Logger logger = LoggerFactory.getLogger(ImpressoraTermicaService.class);

    private final ProdutoService produtoService;

    public void imprimirRecibo(Cliente cliente, List<VendaProduto> produtos, double total, double valorCredito, double valorDebito, double saldoDebito, double limiteCreditoDisponivel) {
        try {
            logger.info("Iniciando impressão do recibo para o cliente: {}", cliente.getNome());

            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
            if (printService == null) {
                throw new RuntimeException("Nenhuma impressora térmica encontrada.");
            }

            EscPos escpos = new EscPos(new PrinterOutputStream(printService));

            Style titleStyle = new Style()
                    .setFontName(Style.FontName.Font_B)
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setBold(true);

            escpos.writeLF(titleStyle, "Recibo de Venda");
            escpos.writeLF("--------------------------------");
            escpos.writeLF("Cliente: " + cliente.getNome());
            escpos.writeLF("Matricula: " + cliente.getMatricula());
            String dataFormatada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            escpos.writeLF("Data: " + dataFormatada);
            escpos.writeLF("--------------------------------");

            for (VendaProduto vendaProduto : produtos) {
                Produto produto = produtoService.findById(vendaProduto.getProduto().getId());
                escpos.writeLF(produto.getDescricao() + " x" + vendaProduto.getQuantidade());
                escpos.writeLF(String.format("R$ %.2f", vendaProduto.getValorTotal()));
            }

            escpos.writeLF("--------------------------------");
            escpos.writeLF(String.format("Total: R$ %.2f", total));
            escpos.writeLF(String.format("Pago no Crédito: R$ %.2f", valorCredito));
            escpos.writeLF(String.format("Pago no Débito: R$ %.2f", valorDebito));
            escpos.writeLF(String.format("Saldo em Débito: R$ %.2f", saldoDebito));
            escpos.writeLF(String.format("Limite de Crédito Disponível: R$ %.2f", limiteCreditoDisponivel));
            escpos.writeLF("--------------------------------");

            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);
            escpos.close();

        } catch (IOException e) {
            logger.error("Erro ao imprimir o recibo: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao imprimir o recibo: " + e.getMessage());
        }
    }
}