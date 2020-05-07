package http.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class ServidorHttp {
	public static void main(String[] args) {
		try {
			System.out.println("Iniciando servidor HTTP en el puerto 8080...");
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
			HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0",8080), 0);
			server.createContext("/P4",new ServidorHttp().new ManejadorPeticiones());
			server.setExecutor(threadPoolExecutor);
			server.start();
			LogManager.getLogManager().reset();
			System.out.println("Servidor HTTP iniciado, esperando peticiones...");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	class ManejadorPeticiones implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			System.out.print("\nPeticion recibida -  Hora: "+new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date())+" URI: "+httpExchange.getRequestURI()+" "+"Version: "+httpExchange.getProtocol()+" ");
			switch (httpExchange.getRequestMethod().toUpperCase()){
				case "GET":
						System.out.print("Tipo: GET");
						try {
							String parametroNombre = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
							String respuestaHTML = "<html><body><h1>Hola, "+parametroNombre+", esta es una solicitud GET</h1></body></html>";
							httpExchange.sendResponseHeaders(200, respuestaHTML.length());
							httpExchange.getResponseBody().write(respuestaHTML.getBytes());							
							httpExchange.getResponseBody().flush();
							httpExchange.getResponseBody().close();
						}catch(ArrayIndexOutOfBoundsException ex) {
							httpExchange.sendResponseHeaders(400,0);							
							httpExchange.getResponseBody().flush();
							httpExchange.getResponseBody().close();
						}
					
					break;
				case "HEAD":
						System.out.print("Tipo HEAD");
						try {
							String parametroNombre = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
							String respuestaHTML = "<html><body><h1>Hola "+parametroNombre+", esta es una solicitud GET</h1></body></html>";
							httpExchange.sendResponseHeaders(200, respuestaHTML.length());
							httpExchange.getResponseBody().flush();
							httpExchange.getResponseBody().close();
						}catch(ArrayIndexOutOfBoundsException ex) {
							httpExchange.sendResponseHeaders(400,0);							
							httpExchange.getResponseBody().flush();
							httpExchange.getResponseBody().close();
						}
					break;
				case "POST":
					System.out.print("Tipo POST");
					try {				
						String parametrosPeticion = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody())).lines().collect(Collectors.joining("\n"));
						System.out.print(" Parametros: "+parametrosPeticion);
						String parametroNombre = parametrosPeticion.split("=")[1];
						String respuestaHTML = "<html><body><h1>Hola, "+parametroNombre+", esta es una solicitud POST</h1></body></html>";
						httpExchange.sendResponseHeaders(200, respuestaHTML.length());
						httpExchange.getResponseBody().write(respuestaHTML.getBytes());
						httpExchange.getResponseBody().flush();
						httpExchange.getResponseBody().close();
					}catch(ArrayIndexOutOfBoundsException ex) {
						httpExchange.sendResponseHeaders(400,0);							
						httpExchange.getResponseBody().flush();
						httpExchange.getResponseBody().close();
					}
					break;
				case "PUT":
					System.out.print("Tipo PUT");
					try {				
						String parametrosPeticion = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody())).lines().collect(Collectors.joining("\n"));
						System.out.print(" Parametros: "+parametrosPeticion);
						httpExchange.sendResponseHeaders(200,0);
						httpExchange.getResponseBody().flush();
						httpExchange.getResponseBody().close();
					}catch(ArrayIndexOutOfBoundsException ex) {
						httpExchange.sendResponseHeaders(400,0);							
						httpExchange.getResponseBody().flush();
						httpExchange.getResponseBody().close();
					}
					break;
				case "DELETE":
					System.out.print("Tipo: DELETE");
					try {
						String parametroNombre = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
						String respuestaHTML = "<html><body><h1>Hola "+parametroNombre+", esta es una solicitud DELETE</h1></body></html>";
						httpExchange.sendResponseHeaders(200, respuestaHTML.length());
						httpExchange.getResponseBody().write(respuestaHTML.getBytes());							
						httpExchange.getResponseBody().flush();
						httpExchange.getResponseBody().close();
					}catch(ArrayIndexOutOfBoundsException ex) {
						httpExchange.sendResponseHeaders(400,0);							
						httpExchange.getResponseBody().flush();
						httpExchange.getResponseBody().close();
					}	
					break;
				case "OPTIONS":
						try {
							System.out.print("Tipo: OPTIONS");
							String respuestaHTML = "<html><body><h1>Hola, esta es una solicitud OPTIONS</h1></body></html>";
							httpExchange.sendResponseHeaders(200, respuestaHTML.length());
							httpExchange.getResponseBody().write(respuestaHTML.getBytes());
							httpExchange.getResponseBody().flush();
							httpExchange.getResponseBody().close();
						}catch(ArrayIndexOutOfBoundsException ex) {
							httpExchange.sendResponseHeaders(400,0);
							httpExchange.getResponseBody().flush();
							httpExchange.getResponseBody().close();
						}
					break;
				case "TRACE":
					System.out.print("Tipo: TRACE");
						httpExchange.sendResponseHeaders(200,0);
						httpExchange.getResponseBody().flush();
						httpExchange.getResponseBody().close();
					break;
			}
		}
		
	}
}
