package br.com.sismeta.qrcodefrompsm;

import br.com.sismeta.qrcodefrompsm.guid.MainUI;

import javax.swing.*;

public class StartupApp {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        var frame = new JFrame("QRCode generator");
        frame.setContentPane(new MainUI().getPainelMain());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        SwingUtilities.updateComponentTreeUI(frame);

        frame.setVisible(true);
    }

}
