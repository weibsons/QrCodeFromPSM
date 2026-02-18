package br.com.sismeta.qrcodefrompsm;

import br.com.sismeta.qrcodefrompsm.dto.FileMap;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ConversorCSV {

    private final List<FileMap> maps = FileMap.startUp();

    public static void main(String[] args) {
        var conversor = new ConversorCSV();
        conversor.convert();
    }

    private FileMap getMap(String key) {
        return maps.stream()
                .filter(m -> m.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public void convert() {
        var inputFile = "C:\\temp\\iptu\\IPTU2026_PAULISTA.txt";
        var outputFile = "C:\\temp\\iptu\\IPTU2026_PAULISTA.csv";


        var df = new DecimalFormat("#,###.00");
        var printable = new HashMap<Integer, FileMap>();
        var items = new ArrayList<String>();
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

                    // ele já começa da primeira linha para por o header no arquivo (para o header todos serão verdadeiros)
                    if (printable.containsKey(i)) {
                        var fm = printable.get(i);
                        if (!isHeader) {
                            if (fm.isMoney()) {
                                data = df.format(Integer.parseInt(data) / 100.0);
                            }
                        }
                        printRow.add(data);
                    }
                }
                isHeader = false;
                items.add(String.join(";", printRow));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao ler o arquivo!");
        }



        // Escrita no novo arquivo .csv
        try (var bw = new BufferedWriter(new FileWriter(outputFile))) {
            for (var l : items) {
                bw.write(l);
                bw.newLine();
            }
            System.out.println("Arquivo CSV gerado com sucesso em: " + outputFile);
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo: " + e.getMessage());
        }
    }



}
