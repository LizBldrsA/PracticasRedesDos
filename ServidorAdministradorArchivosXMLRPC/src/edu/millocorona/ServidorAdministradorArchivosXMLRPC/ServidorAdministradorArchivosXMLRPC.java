package edu.millocorona.ServidorAdministradorArchivosXMLRPC;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import org.apache.xmlrpc.*;

public class ServidorAdministradorArchivosXMLRPC {
    
	private static final String DIRECTORIO_INICIAL = "/home/millocorona/Desarrollo/Java/ServidorAdministradorArchivosXMLRPC/run/DirectorioInicial";
	
	public static void main(String[] args) {
		Integer puerto = 0;
		Scanner scn = new Scanner(System.in);
		System.out.println("************************************************************");
		System.out.println("* Bienvenido al servidor administrador de archivos XML-RPC *");
		System.out.println("************************************************************");
		System.out.println("");
		System.out.println("Ingrese el puerto de escucha: ");
		try {
			puerto = Integer.valueOf(scn.next());
		}catch(NumberFormatException ex) {
			System.out.println("El puerto ingresado no es valido");
		}
		
        System.out.println("Iniciando servidor de archivos XML-RPC en el puerto "+puerto+"...");
        WebServer server = new WebServer(puerto);
        server.addHandler("ServidorAdministradorArchivosXMLRPC", new ServidorAdministradorArchivosXMLRPC());
        server.start();
        System.out.println("Servidor de archivos XML-RPC iniciado correctamente.");	
	}
	
	public String crearArchivo(String nombreArchivo) {
		try {
			System.out.println("Peticion de creacion de archivo recibida, nombre de archivo: "+nombreArchivo);
			if(!nombreArchivo.startsWith(File.separator)) {
				nombreArchivo=File.separator+nombreArchivo;
			} 
			File nuevoArchivo = new File(DIRECTORIO_INICIAL+nombreArchivo);
			if(nuevoArchivo.getParentFile() != null && nuevoArchivo.getParentFile().exists()) {
				nuevoArchivo.createNewFile();
				return "Exito: Archivo creado correctamente";
			}else {
				return "Error: El directorio padre del archivo no existe";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "Error: Error interno del servidor";
		}
	}
	
	public String leerArchivo(String nombreArchivo) {
		try {
			System.out.println("Peticion de lectura de archivo recibida, nombre de archivo: "+nombreArchivo);
			if(!nombreArchivo.startsWith(File.separator)) {
				nombreArchivo=File.separator+nombreArchivo;
			}
			File archivo = new File(DIRECTORIO_INICIAL+nombreArchivo);
			if(archivo != null && archivo.exists()) {
				return new String(Base64.getUrlEncoder().encode(Files.readAllBytes(archivo.toPath())),"UTF-8");
			}else {
				return "Error: El archivo solicitado no existe";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "Error: Error interno del servidor";
		}
	}
	
	public String escribirArchivo(String nombreArchivo,String datosArchivo) {
		try {
			System.out.println("Peticion de escritura de archivo recibida, nombre de archivo: "+nombreArchivo+" datos: "+datosArchivo);
			if(!nombreArchivo.startsWith(File.separator)) {
				nombreArchivo=File.separator+nombreArchivo;
			}
			File archivo = new File(DIRECTORIO_INICIAL+nombreArchivo);
			if(archivo.getParentFile() != null && archivo.getParentFile().exists()){
				if(archivo == null || !archivo.exists()) {
					archivo.createNewFile();
				} 
				Files.write(archivo.toPath(),Base64.getUrlDecoder().decode(datosArchivo));
				return "Exito: Archivo escrito correctamente";
			}else{
				return "Error: El directorio padre del archivo no existe";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "Error: Error interno del servidor";
		}
	}
	
	public String renombrarArchivo(String nombreArchivo, String nombreArchivoNuevo) {
		System.out.println("Peticion de renombramiento de archivo recibida, nombre de archivo original: "+nombreArchivo+" nombre de archivo nuevo: "+nombreArchivoNuevo);
		if(!nombreArchivo.startsWith(File.separator)) {
			nombreArchivo=File.separator+nombreArchivo;
		}
		File archivo = new File(DIRECTORIO_INICIAL+nombreArchivo);
		if(archivo != null && archivo.exists()) {
			if(!nombreArchivoNuevo.startsWith(File.separator)) {
				nombreArchivoNuevo=File.separator+nombreArchivoNuevo;
			}
			File archivoNuevo = new File(DIRECTORIO_INICIAL+nombreArchivoNuevo);
			if(archivoNuevo != null && archivoNuevo.exists()) {
				return "Error: Ya existe un archivo con ese nombre";
			}else {
				archivo.renameTo(archivoNuevo);
				return "Exito: Archivo renombrado correctamente";
			}
		}else {
			return "Error: El archivo no existe";
		}
	}
	
	public String eliminarArchivo(String nombreArchivo) {
		System.out.println("Peticion de eliminacion de archivo recibida, nombre de archivo: "+nombreArchivo);
		if(!nombreArchivo.startsWith(File.separator)) {
			nombreArchivo=File.separator+nombreArchivo;
		}
		File archivo = new File(DIRECTORIO_INICIAL+nombreArchivo);
		if(archivo != null && archivo.exists()) {
			archivo.delete();
			return "Exito: Archivo eliminado correctamente";
		}else {
			return "Error: El archivo solicitado no existe";
		}
	}
	
	public String crearDirectorio(String nombreDirectorio) {
		System.out.println("Peticion de crecion de directorio recibida, nombre de directorio: "+nombreDirectorio);
		if(!nombreDirectorio.startsWith(File.separator)) {
			nombreDirectorio=File.separator+nombreDirectorio;
		}
		File directorio = new File(DIRECTORIO_INICIAL+nombreDirectorio);
		if(directorio != null && directorio.exists()) {
			return "Error: El directorio ya existe";
		}else {
			directorio.mkdirs();
			return "Exito: Directorio creado correctamente";
		}
		
	}
	
	public String eliminarDirectorio(String nombreDirectorio) {
		System.out.println("Peticion de eliminacion de directorio recibida, nombre de directorio: "+nombreDirectorio);
		if(!nombreDirectorio.startsWith(File.separator)) {
			nombreDirectorio=File.separator+nombreDirectorio;
		}
		File directorio = new File(DIRECTORIO_INICIAL+nombreDirectorio);
		if(directorio!=null && directorio.exists()) {
			if(directorio.isDirectory()) {
				directorio.delete();
				return "Exito: Directorio eliminado correctamente";
			}else {
				return "Error: La ruta indicada es un archivo, no un directorio";
			}
		}else {
			return "Error: El directorio no existe";
		}
	}
	
	public String listarDirectorio(String nombreDirectorio) {
		System.out.println("Peticion de listado de directorio recibida, nombre de directorio: "+nombreDirectorio);
		if(nombreDirectorio.trim().equalsIgnoreCase("RAIZ")) {
			File directorio = new File(DIRECTORIO_INICIAL);
			if(directorio!=null && directorio.exists()) {
				if(directorio.isDirectory()) {
					return Arrays.toString(directorio.list());
				}else {
					return "Error: La ruta indicada es un archivo, no un directorio";
				}
			}else {
				return "Error: El directorio no existe";
			}
		}else{
			if(!nombreDirectorio.startsWith(File.separator)) {
				nombreDirectorio=File.separator+nombreDirectorio;
			}
			File directorio = new File(DIRECTORIO_INICIAL+nombreDirectorio);
			if(directorio!=null && directorio.exists()) {
				if(directorio.isDirectory()) {
					return Arrays.toString(directorio.list());
				}else {
					return "Error: La ruta indicada es un archivo, no un directorio";
				}
			}else {
				return "Error: El directorio no existe";
			}
		}
	}
	
	
}
