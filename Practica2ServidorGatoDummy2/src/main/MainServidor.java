package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import servidor.Servidor;

public class MainServidor {

	public static void main(String[] args) throws IOException{
		Servidor servidor = new Servidor();
		
		System.out.println("Iniciando Servidor y abriendo flujos de entrada y salida de datos");
		
		int puerto = servidor.obtenerPuerto();
		String dificultad = servidor.obtenerDificultad();
		servidor.iniciarServidor(puerto,dificultad);
		
		int numConexiones = servidor.obtenerNumeroDeConexionesSimultaneas();
		
		Thread [] hilosConexionesDeClientes = new Thread [numConexiones];
		for(int i=0; i<numConexiones; i++) {
			final int numeroDeCliente = i;
			Socket conexionAlCliente = servidor.esperarCliente();
			DataOutputStream flujoSalidaAlCliente = servidor.abrirFlujoDeSaidaAlCliente(conexionAlCliente);
			DataInputStream flujoDeEntradaDelCliente = servidor.abrirFlujoDeEntradaDelCliente(conexionAlCliente);
			servidor.getClientesConectados().add(conexionAlCliente);
			servidor.getFlujosDeSalidaAClientes().add(flujoSalidaAlCliente);
			servidor.getFlujosDeEntradaDeClientes().add(flujoDeEntradaDelCliente);
			servidor.enviarDatoAlCliente(flujoSalidaAlCliente, dificultad);
			servidor.enviarDatoAlCliente(flujoSalidaAlCliente, ""+numeroDeCliente);
			Thread hiloDeConexion = new Thread(new Runnable() {
					@Override
					public void run() {
						servidor.iniciarJuego(flujoSalidaAlCliente, flujoDeEntradaDelCliente,numeroDeCliente);
					}
				}
			);
			hilosConexionesDeClientes[numeroDeCliente] = hiloDeConexion;
			hiloDeConexion.start();
		}
		
		for(int i = 0; i< hilosConexionesDeClientes.length;i++) {
			try {
				hilosConexionesDeClientes[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
