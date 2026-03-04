package br.com.sismeta.qrcodefrompsm;

import br.com.sismeta.qrcodefrompsm.dto.FileMap;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConversorCSV {

    private final List<FileMap> maps = FileMap.startUp();

    public static void main(String[] args) {
        var start = System.currentTimeMillis();

        var conversor = new ConversorCSV();
        conversor.convert();

        System.out.println("A execução durou " + (System.currentTimeMillis() - start) + " ms" );
    }

    private FileMap getMap(String key) {
        return maps.stream()
                .filter(m -> m.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public void convert() {
        var inputFile = "C:\\temp\\iptu\\ARQUIVO_IMPRESSAO_IPTU2026_PAULISTA.txt";
        var outputDir = "C:\\temp\\iptu\\";
        var df = new DecimalFormat("#,###.00");
        var printable = new HashMap<Integer, FileMap>();
        var headerLine = "";
        var fileCount = 1;
        var recordCount = 0;
        var maxRecords = 5000;

        BufferedWriter bw = null;

        try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            var isHeader = true;
            String linha;

            while ((linha = br.readLine()) != null) {
                var printRow = new ArrayList<String>();
                var campos = linha.split(";");

                for (var i = 0; i < campos.length; i++) {
                    var data = StringUtils.trimToEmpty(campos[i]);

                    if (isHeader) {
                        var map = this.getMap(data);
                        if (map != null) {
                            printable.put(i, map);
                        }
                    }

                    if (printable.containsKey(i)) {
                        var fm = printable.get(i);
                        if (!isHeader) {
                            if (fm.isMoney()) {
                                data = df.format(Long.parseLong(data) / 100.0);
                            }
                        }
                        printRow.add(data);
                    }
                }

                String rowStr = String.join(";", printRow);

                if (isHeader) {
                    headerLine = rowStr; // salvar cabeçalho
                    isHeader = false;

                    // abrir o primeiro arquivo já com cabeçalho
                    var outputFile = outputDir + "parte_" + fileCount + ".csv";
                    bw = new BufferedWriter(new FileWriter(outputFile));
                    bw.write(headerLine);
                    bw.newLine();
                    fileCount++;
                    recordCount = 0;
                    continue; // não escrever o cabeçalho de novo como "linha normal"
                }

                // abrir novo arquivo se necessário
                if (recordCount >= maxRecords) {
                    bw.close();
                    var outputFile = outputDir + "parte_" + fileCount + ".csv";
                    bw = new BufferedWriter(new FileWriter(outputFile));
                    bw.write(headerLine); // cabeçalho só nas partes seguintes
                    bw.newLine();
                    fileCount++;
                    recordCount = 0;
                }

                bw.write(rowStr);
                bw.newLine();
                recordCount++;
            }

            if (bw != null) {
                bw.close();
            }

            System.out.println("Arquivos CSV gerados com sucesso em: " + outputDir);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao processar o arquivo!");
        }
    }


}
