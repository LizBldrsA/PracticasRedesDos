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
		servidor.iniciarServidor(puerto);
		
		Socket conexionAlCliente;
		conexionAlCliente = servidor.esperarCliente();
		
		DataOutputStream flujoSalidaAlCliente;
		flujoSalidaAlCliente = servidor.abrirFlujoDeSaidaAlCliente(conexionAlCliente);
	
		DataInputStream flujoDeEntradaDelCliente;
		flujoDeEntradaDelCliente = servidor.abrirFlujoDeEntradaDelCliente(conexionAlCliente);
	
		System.out.println("Esperando la dificultad");
		String dificultad = servidor.leerDatoDelCliente(flujoDeEntradaDelCliente);
		System.out.println("Dificultad recibida: "+ dificultad);
		
		servidor.iniciarJuego(dificultad, flujoSalidaAlCliente, flujoDeEntradaDelCliente);
		
		conexionAlCliente.close();
	}
	
}
