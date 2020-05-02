package servidor;

import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class Servidor {
	
	private ServerSocket socketServidor;
	private char[][] tablero;
	private String dificultad;
	private ArrayList<DataOutputStream> flujosDeSalidaAClientes = new ArrayList<DataOutputStream>();
	private ArrayList<DataInputStream> flujosDeEntradaDeClientes = new ArrayList<DataInputStream>();
	private ArrayList<Socket> clientesConectados = new ArrayList<Socket>();
	private boolean finDelJuego = false;
	private volatile Integer TURNO = 0;


	public int obtenerPuerto(){
		
		Scanner sc = new Scanner(System.in);
		int puerto;
		System.out.println("Ingrese el puerto: ");
		puerto = sc.nextInt();
		
		return puerto;
		
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
	

	public int obtenerNumeroDeConexionesSimultaneas(){
		
		Scanner sc = new Scanner(System.in);
		int conexiones;
		System.out.println("Ingrese el numero de conexiones simultaneas: ");
		conexiones = sc.nextInt();
		
		return conexiones;
		
	}
	
	public void iniciarServidor(int puerto,String dificultad) {
		try {
			this.dificultad=dificultad;
			tablero = generarTableroVacio();
			socketServidor = new ServerSocket(puerto);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket esperarCliente() {
		
		try {
			Socket conexionAlCliente = socketServidor.accept(); // accept() para comenzar el socket y esperar la conexión
			return conexionAlCliente;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public DataOutputStream abrirFlujoDeSaidaAlCliente(Socket conexionAlCliente) {
		try {
			DataOutputStream flujoSalidaAlCliente = new DataOutputStream(conexionAlCliente.getOutputStream());
			return flujoSalidaAlCliente;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public DataInputStream abrirFlujoDeEntradaDelCliente(Socket conexionAlCliente) {
		try {
			DataInputStream flujoEntradaDelCliente = new DataInputStream(conexionAlCliente.getInputStream());
			return flujoEntradaDelCliente;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void enviarDatoAlCliente(DataOutputStream flujoSalidaAlCliente, String dato) {
		try {
			flujoSalidaAlCliente.writeUTF(dato);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void enviarDatoTodosClientes(String dato) {
		for(int i=0; i<clientesConectados.size(); i++ ) {
			enviarDatoAlCliente(flujosDeSalidaAClientes.get(i), dato);
		}
	}
	
	public String leerDatoDelCliente(DataInputStream flujoDeEntradaDelCliente) {
		try {
			return flujoDeEntradaDelCliente.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	private char[][] generarTableroVacio (){
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
	
	private int[] obtenerTiroDelCliente(String coordenadaRecibida) {
		String [] coordenadasRecibidas = coordenadaRecibida.split("-");
		return new int[] {Integer.parseInt(coordenadasRecibidas[0]), Integer.parseInt(coordenadasRecibidas[1])};
	}
	
	private boolean casillaOcupada(int x, int y) {
		if (tablero[x][y] == '-') {
			return false;
		} else {
			return true;
		}	
	}
	
	
	private String validarResultado() {
		boolean	hayEspaciosVacios = false;
		for(int i=0; i<clientesConectados.size(); i++ ) {
			boolean esteClienteGana = false;
			
			boolean ganaPorFila = false;
			boolean ganaPorColumna = false;
			char caracterDeTiro = Character.toChars(97+i)[0];
			for(int j=0; j<tablero.length; j++) {
				boolean mismoCaracterFila = true;
				for(int k=0; k<tablero.length; k++) {
					if(tablero[j][k] == '-' || tablero[j][k] != caracterDeTiro) {
						if(tablero[j][k] == '-') {
							hayEspaciosVacios = true;
						}
						mismoCaracterFila = false;
						break;
					}
				}
				if(mismoCaracterFila) {
					ganaPorFila = true;
					break;
				}
			}
			
			if(ganaPorFila) {
				esteClienteGana = true;
			}else {
				for(int j=0; j<tablero.length; j++) {
					boolean mismoCaracterColumna = true;
					for(int k=0; k<tablero.length; k++) {
						if(tablero[k][j] == '-' || tablero[k][j] != caracterDeTiro) {
							if(tablero[k][j] == '-') {
								hayEspaciosVacios = true;
							}
							mismoCaracterColumna = false;
							break;
						}
					}
					if(mismoCaracterColumna) {
						ganaPorColumna = true;
						break;
					}
				}
				if(ganaPorColumna) {
					esteClienteGana = true;
				}
			}
			if(esteClienteGana) {
				//Retornamos al ganador
				return "GANA "+i;
			}
		}
		if(hayEspaciosVacios) {
			return "CONTINUA";
		}else {
			return "EMPATE";
		}
	}
	
	public void iniciarJuego (DataOutputStream flujoSalidaAlCliente, DataInputStream flujoDeEntradaDelCliente, int numeroCliente) {
		char caracterDeTiro = Character.toChars(97+numeroCliente)[0];
		while(!finDelJuego) {
			synchronized (TURNO) {
				if(TURNO == numeroCliente) {
					enviarDatoTodosClientes("TURNO "+numeroCliente);
					String datoRecibido = leerDatoDelCliente(flujoDeEntradaDelCliente);
					System.out.println("Dato recibido del cliente "+numeroCliente+" : "+datoRecibido);
					int [] coordenada = obtenerTiroDelCliente(datoRecibido);
					tablero[coordenada[1]][coordenada[0]] = caracterDeTiro;
					imprimirTablero(tablero);
					String estadoPartida = validarResultado();
					if(estadoPartida.equals("CONTINUA")) {
						//Reenvio de coordenada a todos los clientes
						enviarDatoTodosClientes(numeroCliente+" "+datoRecibido);
					}else if (estadoPartida.equals("EMPATE")) {
						//Envia a todos los clientes que empataron
						enviarDatoTodosClientes(numeroCliente+"_"+datoRecibido+"_"+"EMPATE");
						finDelJuego = true;
					}else {
						//Envia a todos los clientes quien ganó
						enviarDatoTodosClientes(datoRecibido+"_"+estadoPartida);
						finDelJuego = true;
					}
					if(TURNO == clientesConectados.size() - 1) {
						TURNO = 0;
					} else {
						TURNO++;
					}
				}		
			}
		}		
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



	public ArrayList<Socket> getClientesConectados() {
		return clientesConectados;
	}

	public ArrayList<DataOutputStream> getFlujosDeSalidaAClientes() {
		return flujosDeSalidaAClientes;
	}

	public ArrayList<DataInputStream> getFlujosDeEntradaDeClientes() {
		return flujosDeEntradaDeClientes;
	}
}
