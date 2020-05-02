package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Cliente {
	
	
	private Socket conexionAlServidor;
	private DataOutputStream flujoSalidaAlServidor;
	private DataInputStream flujoEntradaDelServidor;
	private boolean finDelJuego=false;
	private char tablero[][];
	
	public String[] obtenerIpPuerto(){
		
		Scanner sc = new Scanner(System.in);
		String ip, puerto;
		System.out.println("Ingrese la IP: ");
		ip = sc.next();
		System.out.println("Ingrese el puerto: ");
		puerto = sc.next();
		
		return new String[] {ip,puerto};
		
	}
	
	public boolean conectarseAlServidor(String ip, int puerto) {
		try {
			conexionAlServidor = new Socket(ip, puerto);
			flujoSalidaAlServidor = new DataOutputStream(conexionAlServidor.getOutputStream()); //flujo de datos hacia el servidor
			flujoEntradaDelServidor = new DataInputStream(conexionAlServidor.getInputStream());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public String obtenerDificultad(){
		
		Scanner sc = new Scanner(System.in);
		String dificultad;
		System.out.println("Ingrese la Dificultad: ");
		System.out.println("\t 1) Principiante 3 x 3 ");
		System.out.println("\t 2) Avanzado 5 x 5 ");
		dificultad = sc.next();
		
		return dificultad;
		
	}
	
	public void enviarDatoAlServidor(String dato) {
		try {
			flujoSalidaAlServidor.writeUTF(dato);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String leerDatoDelServidor() {
		try {
			return flujoEntradaDelServidor.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
		
	public String leerCoordenadaDelCliente() {
		Scanner sc = new Scanner(System.in);
		String coordenadaLeida;
		coordenadaLeida = sc.next();
		return coordenadaLeida;
		
	}
	
	private boolean casillaOcupada(int x, int y) {
		if (tablero[x][y] == '-') {
			return false;
		} else {
			return true;
		}	
	}
	
	private int [] convertirCoordenada(String coordenada) {
		String[] coordenadas = coordenada.split("-");
		int x,y;

		try {
			char coordenadaX = coordenadas[0].toCharArray()[0];
			y = Integer.parseInt(coordenadas[1])-1; 
			x = coordenadaX - 65;
		}catch(NumberFormatException ex) {
			x=-1;
			y=-1;
		}
		return new int[] {y,x};
	}
	
	public boolean validaCoordenada(String coordenada, String dificultad ) {
		int limiteSuperior;
		int x,y;
		String[] coordenadas = coordenada.split("-");
		if (coordenadas.length < 2) {
			return false;
		}
		
		int[] coordenadasInt = convertirCoordenada(coordenada);
		y = coordenadasInt[0];
		x = coordenadasInt[1];
		
		if(x == -1 || y == -1) {
			return false;
		}
		if(dificultad.trim().equals("1")) {
			limiteSuperior = 3;
		} else {
			limiteSuperior = 5;
		}
		
		if(y>limiteSuperior || x>limiteSuperior) {
			return false;
		}
		
		if(casillaOcupada(y, x)) {
			return false;
		}
		
		return true;
	}

	private void imprimirTablero(char[][] tablero) {
		if(tablero.length == 3) {
			System.out.println("\tA  B  C");
			

		} else {
			System.out.println("\tA  B  C  D  E");

		}
		for(int i = 0; i<tablero.length; i++) {
			System.out.print((i+1) + "\t");
			for(int j=0; j<tablero[0].length; j++) {
				System.out.print(tablero [i][j] + "  ");
			}
			System.out.print("\n");
		}
	}
	
	private char[][] generarTableroVacio (String dificultad){
		char tablero[][];
		
		if(dificultad.trim().equals("1")) {
			tablero = new char [3][3];
		} else {
			tablero = new char [5][5];
		}
		
		for(int i = 0; i<tablero.length; i++) {
			for(int j=0; j<tablero[0].length; j++) {
				tablero [i][j] = '-';
			}
		}
		return tablero;
	}
	
	
	public void actualizarTablero(int numeroCliente,String dificultad) {
		long tiempoInicio = 0;
		while(!finDelJuego) {
			String datoDelServidor = leerDatoDelServidor();
			if(datoDelServidor.contains("EMPATE")) {
				System.out.println("HAN EMPATADO");
				
				int numeroDeClienteQueTiro = Integer.valueOf(datoDelServidor.split("_")[0]);
				int [] coordenadaDeUltimoTiro = convertirCoordenadaDelServidor(datoDelServidor.split("_")[1]);
				char caracterDeTiro = Character.toChars(97+numeroDeClienteQueTiro)[0];
				tablero[coordenadaDeUltimoTiro[1]][coordenadaDeUltimoTiro[0]] = caracterDeTiro;
				
				imprimirTablero(tablero);
				finDelJuego = true;
			}else if(datoDelServidor.contains("GANA")) {
				int numeroDeClienteQueTiro = Integer.valueOf(datoDelServidor.split(" ")[1]);
				int [] coordenadaDeUltimoTiro = convertirCoordenadaDelServidor(datoDelServidor.split("_")[0]);
				char caracterDeTiro = Character.toChars(97+numeroDeClienteQueTiro)[0];
				tablero[coordenadaDeUltimoTiro[1]][coordenadaDeUltimoTiro[0]] = caracterDeTiro;
				String datoVictoria[] = datoDelServidor.split("_")[1].split(" ");
				if(String.valueOf(numeroCliente).equals(datoVictoria[1])) {
					System.out.println("GANASTE!");
				}else {
					System.out.println("PERDISTE, GANA "+(Integer.valueOf(datoDelServidor.split("_")[1].split(" ")[1])+1));
				}
				imprimirTablero(tablero);
				finDelJuego = true;
			}else if(datoDelServidor.contains("JUGADOR_CONECTADO")) {
				System.out.println(datoDelServidor.split(" ")[1]+" de "+datoDelServidor.split(" ")[2]+" jugadores conectados");
			}else if(datoDelServidor.contains("EMPIEZA_JUEGO")) {
				tiempoInicio = System.currentTimeMillis();
				imprimirTablero(tablero);
			}else if(datoDelServidor.contains("TURNO")){
				int jugadorEnTurno = Integer.valueOf(datoDelServidor.split(" ")[1]);
				if(jugadorEnTurno == numeroCliente) {
					System.out.println("Ingresa la coordenada del tiro (COLUMNA-FILA): ");
					//Envia coordenada valida
					boolean coordenadaValida = false;
					int coordenadas[] = new int[2];
					while(!coordenadaValida) {
						String coordenadaLeida = leerCoordenadaDelCliente();
						if(validaCoordenada(coordenadaLeida, dificultad)) {
							coordenadas = convertirCoordenada(coordenadaLeida);
							coordenadaValida = true;
						} else {
							System.out.println("Coordenada incorrecta ");
							System.out.println("Ingresa la coordenada del tiro (COLUMNA-FILA): ");
						}
					} 
					enviarDatoAlServidor(coordenadas[1]+"-"+coordenadas[0]);
				}else {
					System.out.println("Es turno del jugador "+(jugadorEnTurno+1));
				}
			}else{
				int numeroDeClienteQueTiro = Integer.valueOf(datoDelServidor.split(" ")[0]);
				int [] coordenada = convertirCoordenadaDelServidor(datoDelServidor.split(" ")[1]);
				char caracterDeTiro = Character.toChars(97+numeroDeClienteQueTiro)[0];
				tablero[coordenada[1]][coordenada[0]] = caracterDeTiro;
				imprimirTablero(tablero);
			}
		}
		long tiempoFinal = System.currentTimeMillis();
		long tiempoTotal = (tiempoFinal - tiempoInicio)/1000;
		System.out.println("Duracion de la partida: "+ tiempoTotal + " segundos");
	}
	
	public int[] convertirCoordenadaDelServidor(String coordenadaRecibida) {
		String [] coordenadasRecibidas = coordenadaRecibida.split("-");
		return new int[] {Integer.parseInt(coordenadasRecibidas[0]), Integer.parseInt(coordenadasRecibidas[1])};
	}
	
	public void iniciarJuego(String dificultad,int numeroCliente) {
		tablero = generarTableroVacio(dificultad);
		actualizarTablero(numeroCliente,dificultad); 
	}
	
	public void cerrarConexion() {
		try {
			conexionAlServidor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
