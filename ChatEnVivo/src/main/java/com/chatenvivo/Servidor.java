package com.chatenvivo;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.Color;

public class Servidor {
    private ServerSocket serverSocket;
    private List<ManejadorCliente> clientes;
    private Map<String, Color> coloresUsuarios;

    public Servidor(int puerto) {
        clientes = new ArrayList<>();
        coloresUsuarios = new HashMap<>();
        try {
            serverSocket = new ServerSocket(puerto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void iniciar() {
        try {
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                ManejadorCliente cliente = new ManejadorCliente(clienteSocket);
                clientes.add(cliente);
                Thread clienteThread = new Thread(cliente);
                clienteThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void difundirMensaje(String mensaje, ManejadorCliente remitente) {
        for (ManejadorCliente cliente : clientes) {
            cliente.enviarMensaje(mensaje);
        }
    }

    private void difundirColores() {
        String coloresMsg = "COLORS:" + serializarColores();
        for (ManejadorCliente cliente : clientes) {
            cliente.enviarMensaje(coloresMsg);
        }
    }

    private String serializarColores() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Color> entry : coloresUsuarios.entrySet()) {
            sb.append(entry.getKey()).append(",")
                    .append(entry.getValue().getRGB()).append(";");
        }
        return sb.toString();
    }

    private class ManejadorCliente implements Runnable {
        private Socket clienteSocket;
        private BufferedReader entrada;
        private PrintWriter salida;
        private String nombreUsuario;

        public ManejadorCliente(Socket socket) {
            this.clienteSocket = socket;
            try {
                entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
                salida = new PrintWriter(clienteSocket.getOutputStream(), true);
                nombreUsuario = entrada.readLine();
                if (!coloresUsuarios.containsKey(nombreUsuario)) {
                    coloresUsuarios.put(nombreUsuario, generarColorAleatorio());
                }
                enviarColores();
                difundirColores();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void enviarMensaje(String mensaje) {
            salida.println(mensaje);
        }

        private void enviarColores() {
            salida.println("COLORS:" + serializarColores());
        }

        public void run() {
            try {
                String mensajeCliente;
                while ((mensajeCliente = entrada.readLine()) != null) {
                    difundirMensaje(nombreUsuario + ": " + mensajeCliente, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clienteSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clientes.remove(this);
            }
        }
    }

    private Color generarColorAleatorio() {
        return new Color((int)(Math.random() * 0x1000000));
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor(5000);
        servidor.iniciar();
    }
}