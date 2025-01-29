package br.com.sismeta.qrcodefrompsm.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author links
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QRCodeCreator {

    private String savePath;
    @Builder.Default
    private int width = 600;
    @Builder.Default
    private int height = 600;

    /**
     * Função responsável por criar o diretório onde será salvo os QRCodes
     *
     * @param savePath {@link String}
     */
    public void makeSavePath(String savePath) {
        var path = savePath + File.separator + "qrcodes" + File.separator;
        var filePath = new File(path);
        if (!filePath.exists()) {
            var created = filePath.mkdirs();
            System.out.println("Directory created: " + created);
        }
        this.savePath = path;
    }

    /**
     * Função responsável por criar os QRCodes
     *
     * @param name {@link String} Nome do QRCode
     * @param text {@link String} Conteúdo do QRCode
     * @throws WriterException Erro ao escrever conteúdo do QRCode
     * @throws IOException Caso não exista arquivo ou caminho
     */
    public void create(String name, String text) throws WriterException, IOException {
        var file = new File(savePath, name);

        var hintMap = new HashMap<EncodeHintType, Object>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hintMap.put(EncodeHintType.MARGIN, 0);
        var matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, getWidth(), getHeight(), hintMap);
        try (var fos = new FileOutputStream(file)) {
            MatrixToImageWriter.writeToStream(matrix, "png", fos);
        }
    }

}
