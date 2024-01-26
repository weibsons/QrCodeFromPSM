package br.com.sismeta.qrcodefrompsm.task;

import br.com.sismeta.qrcodefrompsm.interfaces.ExecutionCallback;
import br.com.sismeta.qrcodefrompsm.util.QRCodeCreator;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@AllArgsConstructor
public class QrCodeGeneratorWorker extends SwingWorker<Void, Integer> {

    private File file;
    private List<List<String>> items;
    private JComboBox<String> comboBoxNome;
    private JComboBox<String> comboBoxQrCode;
    private JProgressBar progressBar;
    private JTextArea textAreaLog;

    private ExecutionCallback callback;

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

    private void generateQrCode() {
        textAreaLog.setText("");
        var nomeIndex = comboBoxNome.getSelectedIndex();
        var qrcodeIndex = comboBoxQrCode.getSelectedIndex();

        var creator = QRCodeCreator.builder()
                .width(600)
                .height(600)
                .build();
        creator.makeSavePath(file.getParent());

        var log = new StringBuilder();

        var size = items.size();
        for (int i = 0; i < size; i++) {
            var item = items.get(i);

            var name = item.get(nomeIndex);
            var text = item.get(qrcodeIndex);
            try {
                creator.create(name + ".png", text);

                var progress = i * 100 / size;
                publish(progress);
                textAreaLog.append((i+1) + " : " + name + " criado com sucesso!\n");
            } catch (Exception ex) {
                log.append("Erro ao tentar gerar o QrCode da linha " + i + ". Nome atribuído: [" + name + "] Texto para o QRCode: [" + text + "]\n");
                textAreaLog.append((i+1) + " : Erro ao tentar gerar o QrCode.");
                ex.printStackTrace();
            }
        }
        publish(100);

        try {
            var logfile = new File(file.getParent() + "/qrcodes/logs/");
            FileUtils.forceMkdir(logfile);
            FileUtils.writeStringToFile(new File(logfile, "erros.txt"), log.toString(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void process(List<Integer> chunks) {
        progressBar.setString(chunks.get(chunks.size() - 1) + "%");
        progressBar.setValue(chunks.get(chunks.size() - 1));
    }
}
