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
		String dificultad = cliente.leerDatoDelServidor();
		String numeroCliente = cliente.leerDatoDelServidor();
		System.out.println("Eres el cliente: "+numeroCliente);
		cliente.iniciarJuego(dificultad,Integer.valueOf(numeroCliente));
		cliente.cerrarConexion();
		
	}
	
}
