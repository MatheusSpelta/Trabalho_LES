package com.example.demo.service;


import com.example.demo.model.Produto;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.barcode.BarCode;
import com.github.anastaciocintra.output.PrinterOutputStream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImpressoraEtiquetaService {

    private final ProdutoService produtoService;

    public void imprimirEtiqueta(Produto produtoRequest) {
        Produto produto = produtoService.findById(produtoRequest.getId());
        try {
            PrintService printService = findPrintService("Etiqueta");
            if (printService == null) {
                throw new RuntimeException("Impressora 'Etiqueta' não encontrada.");
            }

            EscPos escPos = new EscPos(new PrinterOutputStream(printService));
            Style title = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            // Conteúdo a ser impresso
            String nomeProduto = "Produto: " + produto.getDescricao();
            String valorProduto = "Valor: R$ " + String.format("%.2f", produto.getValorVenda());

            // Exibir no console o que será enviado para a impressora
            System.out.println("Conteúdo enviado para impressão:");
            System.out.println(nomeProduto);
            System.out.println(valorProduto);

            // Imprimir o nome do produto
            escPos.write(title, nomeProduto);

            // Imprimir o valor
            escPos.writeLF(valorProduto);


            // Finalizar impressão
            escPos.feed(3).cut(EscPos.CutMode.FULL);
            escPos.close();


            System.out.println("Etiqueta enviada para impressão com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao imprimir etiqueta: " + e.getMessage());
            throw new RuntimeException("Erro ao imprimir etiqueta", e);
        }
    }

    private PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().equalsIgnoreCase(printerName)) {
                return printService;
            }
        }
        return null;
    }
}