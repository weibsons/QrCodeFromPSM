package br.com.sismeta.qrcodefrompsm.task;

import br.com.sismeta.qrcodefrompsm.interfaces.ExecutionCallback;
import br.com.sismeta.qrcodefrompsm.util.QRCodeCreator;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class QrCodeGeneratorWorker extends SwingWorker<Void, Integer> {

    private final File file;
    private final List<List<String>> items;
    private final JComboBox<String> comboBoxNome;
    private final JComboBox<String> comboBoxQrCode;
    private final JProgressBar progressBar;
    private final JTextArea textAreaLog;
    private final ExecutionCallback callback;

    private final StringBuilder log = new StringBuilder();
    private final AtomicInteger current = new AtomicInteger(0);
    private final AtomicInteger total = new AtomicInteger(0);


    @Override
    protected Void doInBackground() {
        generateQrCode();
        return null;
    }

    @Override
    protected void done() {
        JOptionPane.showMessageDialog(null, "QRCodes gerados com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        if (Desktop.isDesktopSupported()) {
            var desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                try {
                    var pasta = new File(file.getParent() + "/qrcodes/");
                    desktop.open(pasta);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println("Erro ao abrir a pasta.");
                }
            } else {
                System.out.println("A abertura de pastas não é suportada na área de trabalho.");
            }
        }

        if (callback != null) {
            callback.onComplete();
        }
    }

    /**
     * Função responsável por gerar os qrcodes baseados em algum campo do Excel.<br/>
     * As atividades são enfileiradas em jobs para serem processados em paralelo, a quantidade de jobs são baseadas na quantidade de processadores existentes na CPU.
     */
    private void generateQrCode() {
        textAreaLog.setText("");
        var nomeIndex = comboBoxNome.getSelectedIndex();
        var qrcodeIndex = comboBoxQrCode.getSelectedIndex();

        var creator = QRCodeCreator.builder()
                .width(200)
                .height(200)
                .build();
        creator.makeSavePath(file.getParent());

        total.set(items.size());
        current.set(1);
        System.out.println("Tamanho total do job: " + total.get());

        var numeroDeThreads = Runtime.getRuntime().availableProcessors();
        var executorService = Executors.newFixedThreadPool(numeroDeThreads);
        var tasks = new ArrayList<Callable<Void>>();

        for (var item : items) {
            tasks.add(() -> {
                workItem(item, nomeIndex, qrcodeIndex, creator);
                return null;
            });
        }

        try {
            var futures = executorService.invokeAll(tasks);
            for (var future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        try {
            var logfile = new File(file.getParent() + "/qrcodes/logs/");
            FileUtils.forceMkdir(logfile);
            FileUtils.writeStringToFile(new File(logfile, "erros.txt"), log.toString(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        publish(100);
    }

    /**
     * Função reponsável por pegar um item da lista e criar o QRCode
     *
     * @param item        List
     * @param nomeIndex   Int
     * @param qrcodeIndex Int
     * @param creator     {@link QRCodeCreator}
     */
    private void workItem(List<String> item, int nomeIndex, int qrcodeIndex, QRCodeCreator creator) {
        var now = current.incrementAndGet();
        if (nomeIndex >= 0 && nomeIndex < item.size() && qrcodeIndex >= 0 && qrcodeIndex < item.size()) {
            var name = item.get(nomeIndex);
            var text = item.get(qrcodeIndex);
            try {
                creator.create(name + ".png", text);

                var progress = now * 100 / total.get();
                publish(progress);
                textAreaLog.append((now) + " : " + name + " criado com sucesso!\n");
                System.out.println(now + " de " + total.get());
            } catch (Exception ex) {
                log.append("Erro ao tentar gerar o QrCode da linha ").append(now).append(". Nome atribuído: [").append(name).append("] Texto para o QRCode: [").append(text).append("]\n");
                textAreaLog.append((now) + " : Erro ao tentar gerar o QrCode.\n");
                ex.printStackTrace();
            }
        } else {
            var exMsg = "Linha " + now + " não foi executada, os parâmetros de nome e ou qrcode não encontrados.\n";
            System.out.println(exMsg);
            log.append(exMsg);
            textAreaLog.append(exMsg);
        }

        textAreaLog.setCaretPosition(textAreaLog.getDocument().getLength());

        if (now % 1000 == 0) {
            System.gc();
        }

    }

    @Override
    protected void process(List<Integer> chunks) {
        progressBar.setString(chunks.get(chunks.size() - 1) + "%");
        progressBar.setValue(chunks.get(chunks.size() - 1));
    }
}
