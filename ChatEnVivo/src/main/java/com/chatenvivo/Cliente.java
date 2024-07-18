package com.chatenvivo;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class Cliente {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String nombreUsuario;
    private Consumer<String> manejadorMensajes;
    private Consumer<String> manejadorColores;
    private boolean coloresRecibidos = false;

    public Cliente(String direccionServidor, int puerto, String nombreUsuario,
                   Consumer<String> manejadorMensajes, Consumer<String> manejadorColores) {
        this.nombreUsuario = nombreUsuario;
        this.manejadorMensajes = manejadorMensajes;
        this.manejadorColores = manejadorColores;
        try {
            socket = new Socket(direccionServidor, puerto);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println(nombreUsuario);
        } catch (IOException e) {
            e.printStackTrace();
        }
        iniciarRecepcion();
    }

    public void enviarMensaje(String mensaje) {
        salida.println(mensaje);
    }

    public void iniciarRecepcion() {
        new Thread(() -> {
            try {
                String mensajeServidor;
                while ((mensajeServidor = entrada.readLine()) != null) {
                    if (mensajeServidor.startsWith("COLORS:")) {
                        manejadorColores.accept(mensajeServidor.substring(7));
                        coloresRecibidos = true;
                    } else if (coloresRecibidos) {
                        manejadorMensajes.accept(mensajeServidor);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void cerrar() {
        try {
            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}