package br.com.sismeta.qrcodefrompsm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileMap {

    private String key;
    private boolean printable;
    private boolean money;

    public static List<FileMap> startUp() {
        var entities = new ArrayList<FileMap>();

        entities.add(new FileMap("N.CAD.", true, false));
        entities.add(new FileMap("LOT.REF", true, false));
        entities.add(new FileMap("INSIMO", true, false));
        entities.add(new FileMap("TOPOGR", true, false));
        entities.add(new FileMap("PEDOLO", true, false));
        entities.add(new FileMap("SITQUA", true, false));
        entities.add(new FileMap("TIPCON", true, false));
        entities.add(new FileMap("PADCON", true, false));
        entities.add(new FileMap("USOIMO", true, false));
        entities.add(new FileMap("TIPIMP", true, false));
        entities.add(new FileMap("NOMECON", true, false));
        entities.add(new FileMap("ENDCOR", true, false));
        entities.add(new FileMap("NUMCO", true, false));
        entities.add(new FileMap("COMCOR", true, false));
        entities.add(new FileMap("BAICOR", true, false));
        entities.add(new FileMap("CIDCOR", true, false));
        entities.add(new FileMap("SIGEST", true, false));
        entities.add(new FileMap("CEPCOR", true, false));
        entities.add(new FileMap("ANOS", true, false));
        entities.add(new FileMap("ENDIMO", true, false));
        entities.add(new FileMap("NUMIMO", true, false));
        entities.add(new FileMap("CEPIMO", true, false));
        entities.add(new FileMap("BAIIMO", true, false));
        entities.add(new FileMap("COMIMO", true, false));
        entities.add(new FileMap("UF", true, false));
        entities.add(new FileMap("TESPRI", true, false));
        entities.add(new FileMap("PROPRI", true, false));
        entities.add(new FileMap("ARETER", true, false));
        entities.add(new FileMap("ARETCO", true, true));
        entities.add(new FileMap("VALTER", true, true));
        entities.add(new FileMap("VM2TER", true, true));
        entities.add(new FileMap("VALCON", true, true));
        entities.add(new FileMap("VM2CON", true, true));
        entities.add(new FileMap("VALVEN", true, true));
        entities.add(new FileMap("ALIQUO", true, true));

        for (var i= 0; i <= 9; i++) {
            entities.add(new FileMap("NP"+i, true, false));
            entities.add(new FileMap("VENCIM"+i, true, false));
            entities.add(new FileMap("NUMDOC"+i, true, false));
            entities.add(new FileMap("TOTIPTU"+i, true, true));
            entities.add(new FileMap("TOTLIMP"+i, true, true));
            entities.add(new FileMap("TOTTAXAS"+i, true, true));
            entities.add(new FileMap("DESCIPTU"+i, true, true));
            entities.add(new FileMap("TOTCDESCIPTU"+i, true, true));
            entities.add(new FileMap("TOTCDESCLIMP"+i, true, true));
            entities.add(new FileMap("TOTGERALCDESC"+i, true, true));
            entities.add(new FileMap("CODBARRA"+i, true, false));
            entities.add(new FileMap("CODPROC"+i, true, false));
            entities.add(new FileMap("COD-BARRAS"+i, true, false));
            entities.add(new FileMap("PIX_COPIA_E_COLA"+i, true, false));
        }

        entities.add(new FileMap("CPF CNPJ", true, false));
        entities.add(new FileMap("AREAUNI", true, false));

        return entities;


    }

}
