package main;

import java.io.IOException;
import cliente.Cliente;
import java.util.Scanner;

public class MainCliente {
	public static void main(String[] args) throws IOException{
		Cliente cliente = new Cliente();
		boolean conectadoAlServidor = false;
		
		while(!conectadoAlServidor) {
			
			String[] ipPuerto = cliente.obtenerIpPuerto();
			
			if(cliente.conectarseAlServidor(ipPuerto[0], Integer.parseInt(ipPuerto[1]))) {
				conectadoAlServidor = true;
			}
		}
		
		String dificultad = cliente.obtenerDificultad();
		cliente.enviarDatoAlServidor(dificultad);
		long tiempoInicio = System.currentTimeMillis();
		cliente.iniciarJuego(dificultad);
		long tiempoFinal = System.currentTimeMillis();
		long tiempoTotal = (tiempoFinal - tiempoInicio)/1000;
		System.out.println("Duracion de la partida: "+ tiempoTotal + " segundos");
		cliente.cerrarConexion();
		
	}
	
}
