package br.com.sismeta.qrcodefrompsm.util;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * Função responsável por converter uma lista em matriz de matriz String[][]
     *
     * @param data List
     * @return String[][]
     */
    public static String[][] converterListaParaMatriz(List<List<String>> data) {
        var linhas = data.size();
        var colunas = data.isEmpty() ? 0 : data.get(0).size();

        var matriz = new String[linhas][colunas];
        for (var i = 0; i < linhas; i++) {
            var linhaDaLista = data.get(i);
            matriz[i] = linhaDaLista.toArray(new String[0]);
        }
        return matriz;
    }


    /**
     * Função responsável por decodificar o charset do arquivo.
     * @param file {@link File}
     * @return String
     */
    public static String detectarCharset(File file) {
        try {
            var buffer = new byte[4096];
            try (var fis = new FileInputStream(file)) {
                var detector = new UniversalDetector(null);

                int bytesRead;
                while ((bytesRead = fis.read(buffer)) > 0 && !detector.isDone()) {
                    detector.handleData(buffer, 0, bytesRead);
                }
                detector.dataEnd();
                var charset = detector.getDetectedCharset();
                detector.reset();
                return charset;
            }
        } catch (Exception e) {
            return "UTF-8";
        }
    }

    /**
     * Função responsável por retornar apenas uma quantidade de elementos que for passada como parametro de um lista.
     * Se essa quantidade for menor aos itens da lista retorna somente a quantidade de itens que a lista possui.
     * Se for maior ou igual retorna apenas o maxItens informados no parâmetro
     * @param list List
     * @param maxItens Int Número de itens que deseja retornar
     * @return List
     * @param <T>
     */
    public static <T> List<T> extrairDezItens(List<T> list, int maxItens) {
        var size = list.size();
        if (size <= maxItens) {
            return new ArrayList<>(list);
        }
        return new ArrayList<>(list.subList(0, maxItens));
    }

}
