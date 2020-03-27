package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import servidor.Servidor;

public class MainServidor {

	public static void main(String[] args){
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
			servidor.enviarDatoTodosClientes("JUGADOR_CONECTADO "+(numeroDeCliente+1)+" "+numConexiones);
		}
		
		servidor.enviarDatoTodosClientes("EMPIEZA_JUEGO");
		
		for(int j = 0; j< numConexiones; j++) {
			final int numeroCliente = j;
			Thread hiloDeConexion = new Thread(new Runnable() {
					@Override
					public void run() {
						servidor.iniciarJuego( servidor.getFlujosDeSalidaAClientes().get(numeroCliente), servidor.getFlujosDeEntradaDeClientes().get(numeroCliente), numeroCliente);
					}
				}
			);
			hilosConexionesDeClientes[j] = hiloDeConexion;	
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
