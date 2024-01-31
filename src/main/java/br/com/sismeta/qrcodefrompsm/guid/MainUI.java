package br.com.sismeta.qrcodefrompsm.guid;

import br.com.sismeta.qrcodefrompsm.task.QrCodeGeneratorWorker;
import br.com.sismeta.qrcodefrompsm.util.Util;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainUI {

    private File file;
    private List<String> header;
    private List<List<String>> items;

    private JPanel painelMain;
    private JButton buttonEscolherArquivo;
    private JTable table;
    private JComboBox comboBoxNome;
    private JComboBox comboBoxQrCode;
    private JLabel labelArquivoSelecionado;
    private JButton buttonGerarQrCode;
    private JProgressBar progressBar;
    private JTextArea textAreaLog;

    /**
     * Função responsável por aplicar o filtro de CSV
     *
     * @return FileFilter
     */
    private FileFilter csvFilter() {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "Arquivos CSV (*.csv)";
            }
        };
    }

    /**
     * Função responsável por carregar um arquivo csv em uma List.
     * Onde cada linha dessa list possui uma List<String> com as colunas
     *
     * @param file File
     * @return List
     */
    private List<List<String>> items(File file) {
        var charset = Util.detectarCharset(file);
        if (StringUtils.isBlank(charset)) {
            charset = "UTF-8";
        }

        header = new ArrayList<>();
        items = new ArrayList<>();
        try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                var campos = linha.split(";");
                var linhaDados = new ArrayList<String>();
                Collections.addAll(linhaDados, campos);

                if (header.isEmpty()) {
                    header.addAll(linhaDados);
                } else {
                    items.add(linhaDados);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao ler o arquivo!");
        }

        System.out.println("HEADER SIZE: " + header.size());
        System.out.println("ITEMS SIZE: " + items.size());

        return items;
    }

    /**
     * Função responsável por selecionar o arquivo e exibir os dados de seleção na tabela e combobox.
     * Preparação para geração dos qrcodes.
     *
     * @param e {@link ActionEvent}
     */
    private void selectFile(ActionEvent e) {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(this.csvFilter());
        var result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            this.file = fileChooser.getSelectedFile();
            labelArquivoSelecionado.setText(file.getName());

            var items = this.items(file);
            if (!items.isEmpty()) {
                var modelNome = new DefaultComboBoxModel<String>();
                modelNome.addAll(header);
                comboBoxNome.setModel(modelNome);


                var modelQrcode = new DefaultComboBoxModel<String>();
                modelQrcode.addAll(header);
                comboBoxQrCode.setModel(modelQrcode);


                var colunas = header.toArray(new String[0]);
                var dados = Util.converterListaParaMatriz(Util.extrairDezItens(items, 10));

                var model = new DefaultTableModel(dados, colunas);
                table.setModel(model);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                for (var i = 0; i < table.getColumnCount(); i++) {
                    table.getColumnModel().getColumn(i).setMinWidth(200);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Arquivo não processado ou em branco.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    /**
     * Função responsável por bloquear os campos para não permitir edição dos dados após início do processamento dos dados.
     * @param lock boolean
     */
    private void lockFields(boolean lock) {
        comboBoxNome.setEditable(!lock);
        comboBoxQrCode.setEditable(!lock);
        buttonGerarQrCode.setEnabled(!lock);
        buttonEscolherArquivo.setEnabled(!lock);
    }


    /**
     * Função responsável por gerar os qrcodes
     *
     * @param e
     */
    private void generateQrCode(ActionEvent e) {
        if (this.file != null && items != null && !items.isEmpty()) {
            if (comboBoxNome.getSelectedIndex() >= 0 && comboBoxQrCode.getSelectedIndex() >= 0) {
                lockFields(true);
                var worker = new QrCodeGeneratorWorker(
                        file,
                        items,
                        comboBoxNome,
                        comboBoxQrCode,
                        progressBar,
                        textAreaLog,
                        () -> lockFields(false)
                );
                worker.execute();
            } else {
                JOptionPane.showMessageDialog(null, "Selecione o campo que representa o nome e o campo que representa o QrCode antes de continuar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione o arquivo que deseja trabalhar.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    public MainUI() {
        buttonEscolherArquivo.addActionListener(this::selectFile);
        buttonGerarQrCode.addActionListener(this::generateQrCode);
    }

    public JPanel getPainelMain() {
        return painelMain;
    }

}
