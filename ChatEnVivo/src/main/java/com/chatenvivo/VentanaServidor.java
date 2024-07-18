package com.chatenvivo;

import javax.swing.*;
import java.awt.*;

public class VentanaServidor extends JFrame {
    private JTextArea areaLog;

    public VentanaServidor() {
        setTitle("Servidor de Chat");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaLog);

        add(scrollPane, BorderLayout.CENTER);

        Thread servidorThread = new Thread(new Runnable() {
            public void run() {
                Servidor servidor = new Servidor(5000);
                areaLog.append("Servidor iniciado en el puerto 5000\n");
                servidor.iniciar();
            }
        });
        servidorThread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new VentanaServidor().setVisible(true);
            }
        });
    }
}