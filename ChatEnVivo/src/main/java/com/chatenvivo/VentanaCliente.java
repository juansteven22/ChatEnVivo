package com.chatenvivo;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class VentanaCliente extends JFrame {
    private JTextPane areaChat;
    private JTextField campoMensaje;
    private JButton botonEnviar;
    private Cliente cliente;
    private String nombreUsuario;
    private Map<String, Color> coloresUsuarios;

    public VentanaCliente() {
        nombreUsuario = JOptionPane.showInputDialog(this, "Ingrese su nombre de usuario:", "Nombre de usuario", JOptionPane.PLAIN_MESSAGE);
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            System.exit(0);
        }

        setTitle("Chat Cliente - " + nombreUsuario);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        coloresUsuarios = new HashMap<>();

        areaChat = new JTextPane();
        areaChat.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaChat);

        campoMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        cliente = new Cliente("localhost", 5000, nombreUsuario, this::mostrarMensaje, this::actualizarColores);

        botonEnviar.addActionListener(e -> enviarMensaje());
        campoMensaje.addActionListener(e -> enviarMensaje());
    }

    private void enviarMensaje() {
        String mensaje = campoMensaje.getText();
        if (!mensaje.isEmpty()) {
            cliente.enviarMensaje(mensaje);
            campoMensaje.setText("");
        }
    }

    private void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            String[] partes = mensaje.split(":", 2);
            if (partes.length == 2) {
                String nombreUsuario = partes[0].trim();
                String contenidoMensaje = partes[1].trim();

                Color colorUsuario = coloresUsuarios.getOrDefault(nombreUsuario, Color.BLACK);

                StyledDocument doc = areaChat.getStyledDocument();
                try {
                    SimpleAttributeSet estiloUsuario = getEstiloUsuario(colorUsuario);
                    SimpleAttributeSet estiloMensaje = getEstiloMensaje();

                    boolean esMensajePropio = nombreUsuario.equals(this.nombreUsuario);
                    int alineacion = esMensajePropio ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT;

                    StyleConstants.setAlignment(estiloUsuario, alineacion);
                    StyleConstants.setAlignment(estiloMensaje, alineacion);

                    doc.insertString(doc.getLength(), nombreUsuario + ": ", estiloUsuario);
                    doc.insertString(doc.getLength(), contenidoMensaje + "\n", estiloMensaje);

                    doc.setParagraphAttributes(doc.getLength() - contenidoMensaje.length() - nombreUsuario.length() - 3,
                            contenidoMensaje.length() + nombreUsuario.length() + 3,
                            estiloUsuario, false);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            areaChat.setCaretPosition(areaChat.getDocument().getLength());
        });
    }

    private void actualizarColores(String coloresSerializados) {
        coloresUsuarios.clear();
        String[] pares = coloresSerializados.split(";");
        for (String par : pares) {
            String[] partes = par.split(",");
            if (partes.length == 2) {
                String nombre = partes[0];
                Color color = new Color(Integer.parseInt(partes[1]));
                coloresUsuarios.put(nombre, color);
            }
        }
    }

    private SimpleAttributeSet getEstiloUsuario(Color color) {
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        StyleConstants.setForeground(estilo, color);
        StyleConstants.setBold(estilo, true);
        return estilo;
    }

    private SimpleAttributeSet getEstiloMensaje() {
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        StyleConstants.setForeground(estilo, Color.BLACK);
        return estilo;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaCliente().setVisible(true));
    }
}