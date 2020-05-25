package edu.millocorona.ClienteAdministradorArchivosXMLRPC;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Scanner;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class ClienteAdministradorArchivosXMLRPC {
	public static void main(String[] args) {
		String servidor = "";
		Integer puerto = 0;
		Scanner scn = new Scanner(System.in);
		System.out.println("***********************************************************");
		System.out.println("* Bienvenido al cliente administrador de archivos XML-RPC *");
		System.out.println("***********************************************************");
		System.out.println("");
		System.out.println("Ingrese la IP o dominio del servidor: ");
		servidor = scn.next();
		System.out.println("");
		System.out.println("Ingrese el puerto del servidor: ");
		try {
			puerto = Integer.valueOf(scn.next());
		}catch(NumberFormatException ex) {
			System.out.println("El puerto ingresado no es valido");
		}
		
		try {
			XmlRpcClient clienteXmlRpc = new XmlRpcClient("http://"+servidor+":"+puerto+"/RPC2");
			boolean continuar = true;
			while(continuar) {
				System.out.println("");
				System.out.println("Seleccione una opcion: ");
				System.out.println("");
				System.out.println("\t 1) Crear archivo");
				System.out.println("\t 2) Leer archivo");
				System.out.println("\t 3) Escribir archivo");
				System.out.println("\t 4) Renombrar archivo");
				System.out.println("\t 5) Eliminar archivo");
				System.out.println("\t 6) Crear directorio");
				System.out.println("\t 7) Eliminar directorio");
				System.out.println("\t 8) Listar directorio");
				System.out.println("\t 9) Salir");
				boolean opcionValida = false;
				int opcion = 0;
				while(!opcionValida) {
					try {
						opcion = Integer.valueOf(scn.next());
						if(opcion<10) {
							opcionValida = true;
						}else {
							System.out.println("La opcion ingresada no es valida");
						}
					}catch(NumberFormatException ex) {
						System.out.println("La opcion ingresada no es valida");
					}	
				}
				if(opcion == 1) {
					System.out.println("Ingrese el nombre del archivo: ");
					String nombreArchivo = scn.next();
					Vector<String> parametros = new Vector<String>();
					parametros.add(nombreArchivo);
					String resultadoEjecucion = (String) clienteXmlRpc.execute("ServidorAdministradorArchivosXMLRPC.crearArchivo",parametros);
					System.out.println("Respuesta: \n\t"+resultadoEjecucion+"\n");
				}else if (opcion == 2) {
					System.out.println("Ingrese el nombre remoto del archivo a descargar: ");
					String nombreArchivo = scn.next();
					System.out.println("Ingrese el nombre local de destino: ");
					String destino = scn.next();
					Vector<String> parametros = new Vector<String>();
					parametros.add(nombreArchivo);
					String resultadoEjecucion = (String) clienteXmlRpc.execute("ServidorAdministradorArchivosXMLRPC.leerArchivo",parametros);
					if(resultadoEjecucion.startsWith("Error")) {
						System.out.println("Respuesta: \n\t"+resultadoEjecucion+"\n");
					}else {			
						File archivoDestino = new File(destino);
						if(archivoDestino.getParentFile() != null && archivoDestino.getParentFile().exists()) {
							archivoDestino.createNewFile();
						}else {
							System.out.println("La directorio local de destino no existe");
						}
						Files.write(archivoDestino.toPath(),Base64.getUrlDecoder().decode(resultadoEjecucion));
					}
				}else if (opcion == 3) {
					System.out.println("Ingrese el nombre del archivo local a cargar: ");
					String nombreArchivoLocal = scn.next();
					System.out.println("Ingrese el nombre del archivo remoto:");
					String nombreArchivoRemoto = scn.next();
					Vector<String> parametros = new Vector<String>();
					parametros.add(nombreArchivoRemoto);
					File archivoLocal = new File(nombreArchivoLocal);
					if(archivoLocal.exists()) {
						parametros.add(new String(Base64.getUrlEncoder().encode(Files.readAllBytes(archivoLocal.toPath())),"UTF-8"));
						String resultadoEjecucion = (String) clienteXmlRpc.execute("ServidorAdministradorArchivosXMLRPC.escribirArchivo",parametros);
						System.out.println("Respuesta: \n\t"+resultadoEjecucion+"\n");
					}else {
						System.out.println("El archivo local especificado no existe");
					}
				}else if (opcion == 4) {
					System.out.println("Ingrese el nombre del archivo a renombrar: ");
					String nombreArchivo = scn.next();
					System.out.println("Ingrese el nombre nuevo del archivo: ");
					String nuevoNombreArchivo = scn.next();
					Vector<String> parametros = new Vector<String>();
					parametros.add(nombreArchivo);
					parametros.add(nuevoNombreArchivo);
					String resultadoEjecucion = (String) clienteXmlRpc.execute("ServidorAdministradorArchivosXMLRPC.renombrarArchivo",parametros);
					System.out.println("Respuesta: \n\t"+resultadoEjecucion+"\n");
				}else if (opcion == 5) {
					System.out.println("Ingrese el nombre del archivo: ");
					String nombreArchivo = scn.next();
					Vector<String> parametros = new Vector<String>();
					parametros.add(nombreArchivo);
					String resultadoEjecucion = (String) clienteXmlRpc.execute("ServidorAdministradorArchivosXMLRPC.eliminarArchivo",parametros);
					System.out.println("Respuesta: \n\t"+resultadoEjecucion+"\n");
				}else if (opcion == 6) {
					System.out.println("Ingrese el nombre del directorio: ");
					String nombreDirectorio = scn.next();
					Vector<String> parametros = new Vector<String>();
					parametros.add(nombreDirectorio);
					String resultadoEjecucion = (String) clienteXmlRpc.execute("ServidorAdministradorArchivosXMLRPC.crearDirectorio",parametros);
					System.out.println("Respuesta: \n\t"+resultadoEjecucion+"\n");
				}else if (opcion == 7) {
					System.out.println("Ingrese el nombre del directorio: ");
					String nombreDirectorio = scn.next();
					Vector<String> parametros = new Vector<String>();
					parametros.add(nombreDirectorio);
					String resultadoEjecucion = (String) clienteXmlRpc.execute("ServidorAdministradorArchivosXMLRPC.eliminarDirectorio",parametros);
					System.out.println("Respuesta: \n\t"+resultadoEjecucion+"\n");
				}else if (opcion == 8) {
					System.out.println("Ingrese el nombre del directorio: ");
					String nombreDirectorio = scn.next();
					Vector<String> parametros = new Vector<String>();
					parametros.add(nombreDirectorio);
					String resultadoEjecucion = (String) clienteXmlRpc.execute("ServidorAdministradorArchivosXMLRPC.listarDirectorio",parametros);
					if(resultadoEjecucion.startsWith("Error")) {
						System.out.println("Respuesta: \n\t"+resultadoEjecucion+"\n");
					}else {
						System.out.println("Contenido directorio: \n");
						resultadoEjecucion = resultadoEjecucion.replaceAll("\\[","").replaceAll("\\]","").replaceAll(" ","");
						for(String entrada:resultadoEjecucion.split(",")) {
							System.out.println("\t "+entrada);
						}
					}
				}else if (opcion == 9) {
					continuar = false;
					System.out.println("\n Hasta luego \n");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
