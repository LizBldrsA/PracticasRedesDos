package http.cliente;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ClienteHttp {
	
	private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
	
	public static void main(String args[]) {
		
		new ClienteHttp().iniciarClienteHTTP();
		
	}
	
	private void iniciarClienteHTTP() {
		String servidor = "";
		String puerto = "";
		Scanner scn = new Scanner(System.in);
		System.out.println("******************************");
		System.out.println("* Bienvenido al cliente HTTP *");
		System.out.println("******************************");
		System.out.println("");
		System.out.println("Ingrese la IP o dominio del servidor: ");
		servidor = scn.next();
		System.out.println("");
		System.out.println("Ingrese el puerto del servidor: ");
		puerto = scn.next();
		boolean continuar = true;
		while(continuar) {
			System.out.println("");
			System.out.println("Seleccione una opcion: ");
			System.out.println("");
			System.out.println("\t 1) Enviar peticion GET");
			System.out.println("\t 2) Enviar peticion HEAD");
			System.out.println("\t 3) Enviar peticion POST");
			System.out.println("\t 4) Enviar peticion PUT");
			System.out.println("\t 5) Enviar peticion DELETE");
			System.out.println("\t 6) Enviar peticion OPTIONS");
			System.out.println("\t 7) Enviar peticion TRACE");
			System.out.println("\t 8) Salir");
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
			try {
				System.out.println("Ingrese el contexto del servidor para la peticion: ");
				String contexto = scn.next();
				
				switch(opcion) {
					case 1:
						HttpRequest getRequest = HttpRequest.newBuilder().GET().uri(URI.create("http://"+servidor+":"+puerto+"/"+contexto+"?"+getParametrosHTTPComoCadena(solicitarParametrosPeticion()))).setHeader("User-Agent", "Cliente HTTP Simple").build();
						HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
						System.out.println("Resultado: ");
						System.out.println("");
						manejarEstado(getResponse.statusCode());
						System.out.println("");
						System.out.println(getResponse.body());
						break;
					case 2:
						HttpRequest headRequest = HttpRequest.newBuilder().method("HEAD",HttpRequest.BodyPublishers.noBody()).uri(URI.create("http://"+servidor+":"+puerto+"/"+contexto+"?"+getParametrosHTTPComoCadena(solicitarParametrosPeticion()))).setHeader("User-Agent", "Cliente HTTP Simple").build();
						HttpResponse<String> headResponse = httpClient.send(headRequest, HttpResponse.BodyHandlers.ofString());
						System.out.println("Resultado: ");
						System.out.println("");
						manejarEstado(headResponse.statusCode());
						break;
					case 3:
						HttpRequest postRequest = HttpRequest.newBuilder().POST(getParametrosHTTP(solicitarParametrosPeticion())).uri(URI.create("http://"+servidor+":"+puerto+"/"+contexto)).setHeader("User-Agent", "Cliente HTTP Simple").header("Content-Type", "application/x-www-form-urlencoded").build();
						HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
						System.out.println("Resultado: ");
						System.out.println("");
						manejarEstado(postResponse.statusCode());
						System.out.println("");
						System.out.println(postResponse.body());
						break;
					case 4:
						HttpRequest putRequest = HttpRequest.newBuilder().PUT(getParametrosHTTP(solicitarParametrosPeticion())).uri(URI.create("http://"+servidor+":"+puerto+"/"+contexto)).setHeader("User-Agent", "Cliente HTTP Simple").header("Content-Type", "application/x-www-form-urlencoded").build();
						HttpResponse<String> putResponse = httpClient.send(putRequest, HttpResponse.BodyHandlers.ofString());
						System.out.println("Resultado: ");
						System.out.println("");
						manejarEstado(putResponse.statusCode());
						
						break;
					case 5:
						HttpRequest deleteRequest = HttpRequest.newBuilder().DELETE().uri(URI.create("http://"+servidor+":"+puerto+"/"+contexto+"?"+getParametrosHTTPComoCadena(solicitarParametrosPeticion()))).setHeader("User-Agent", "Cliente HTTP Simple").build();
						HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
						System.out.println("Resultado: ");
						System.out.println("");
						manejarEstado(deleteResponse.statusCode());
						System.out.println("");
						System.out.println(deleteResponse.body());
						break;
					case 6:
						HttpRequest optionsRequest = HttpRequest.newBuilder().method("OPTIONS",HttpRequest.BodyPublishers.noBody()).uri(URI.create("http://"+servidor+":"+puerto+"/"+contexto)).setHeader("User-Agent", "Cliente HTTP Simple").build();
						HttpResponse<String> optionsResponse = httpClient.send(optionsRequest, HttpResponse.BodyHandlers.ofString());
						System.out.println("Resultado: ");
						System.out.println("");
						manejarEstado(optionsResponse.statusCode());	
						System.out.println("");
						System.out.println(optionsResponse.body());
						break;
					case 7:
						HttpRequest traceRequest = HttpRequest.newBuilder().method("TRACE",HttpRequest.BodyPublishers.noBody()).uri(URI.create("http://"+servidor+":"+puerto+"/"+contexto)).setHeader("User-Agent", "Cliente HTTP Simple").build();
						HttpResponse<String> traceResponse = httpClient.send(traceRequest, HttpResponse.BodyHandlers.ofString());
						System.out.println("Resultado: ");
						System.out.println("");
						manejarEstado(traceResponse.statusCode());	
						break;
					case 8:
							System.out.println("Hasta luego.");
							continuar=false;
						break;
				}
			}catch(IOException | InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static void manejarEstado(int estado) {
		System.out.print("Estado HTTP: "+estado);
		if(estado == 100) {
			System.out.print(" Continue");
		}else if(estado == 101) {
			System.out.print(" Switching protocols");
		}else if(estado == 200) {
			System.out.print(" OK");
		}else if(estado == 201) {
			System.out.print(" Created");
		}else if(estado == 202) {
			System.out.print(" Accepted");
		}else if(estado == 203) {
			System.out.print(" Non-Authoritative Information");
		}else if(estado == 204) {
			System.out.print(" No Content");
		}else if(estado == 205) {
			System.out.print(" Reset Content");
		}else if(estado == 300) {
			System.out.print(" Multiple choices");
		}else if(estado == 301) {
			System.out.print(" Moved Permanently");
		}else if(estado == 302) {
			System.out.print(" Found");
		}else if(estado == 303) {
			System.out.print(" See other");
		}else if(estado == 305) {
			System.out.print(" Use proxy");
		}else if(estado == 306) {
			System.out.print(" (Unused)");
		}else if(estado == 307) {
			System.out.print(" Temporary redirect");
		}else if(estado == 400) {
			System.out.print(" Bad request");
		}else if(estado == 402) {
			System.out.print(" Payment required");
		}else if(estado == 403) {
			System.out.print(" Forbbiden");
		}else if(estado == 404) {
			System.out.print(" Not found");
		}else if(estado == 405) {
			System.out.print(" Method Not Allowed");
		}else if(estado == 406) {
			System.out.print(" Not Acceptable");
		}else if(estado == 408) {
			System.out.print(" Timeout");
		}else if(estado == 409) {
			System.out.print(" Conflict");
		}else if(estado == 410) {
			System.out.print(" Gone");
		}else if(estado == 411) {
			System.out.print(" Length Required");
		}else if(estado == 413) {
			System.out.print(" Payload too large");
		}else if(estado == 414) {
			System.out.print(" URI too long");
		}else if(estado == 415) {
			System.out.print(" Unsupported Media Type");
		}else if(estado == 417) {
			System.out.print(" Expectation failed");
		}else if(estado == 426) {
			System.out.print(" Upgrade Required");
		}else if(estado == 500) {
			System.out.print(" Internal server error");
		}else if(estado == 501) {
			System.out.print(" Not implemented");
		}else if(estado == 502) {
			System.out.print(" Bad Gateway");
		}else if(estado == 503) {
			System.out.print(" Service unavaliable");
		}else if(estado == 504) {
			System.out.print(" Gateway timeout");
		}else if(estado == 505) {
			System.out.print(" HTTP version not supported");
		}
		System.out.println("\n");
	}
	
	private static HttpRequest.BodyPublisher getParametrosHTTP(Map<String, String> parametros) throws UnsupportedEncodingException{
        StringBuilder cadenaParametrosHttp = new StringBuilder();
        for (Map.Entry<String, String> entry : parametros.entrySet()) {
        	cadenaParametrosHttp.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
        	cadenaParametrosHttp.append("=");
        	cadenaParametrosHttp.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        	cadenaParametrosHttp.append("&");
        }        
        return HttpRequest.BodyPublishers.ofString(cadenaParametrosHttp.toString().length() > 0 ? cadenaParametrosHttp.toString().substring(0, cadenaParametrosHttp.toString().length() - 1):cadenaParametrosHttp.toString());
	}
	
	private static String getParametrosHTTPComoCadena(Map<String, String> parametros) throws UnsupportedEncodingException{
        StringBuilder cadenaParametrosHttp = new StringBuilder();
        for (Map.Entry<String, String> entry : parametros.entrySet()) {
        	cadenaParametrosHttp.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
        	cadenaParametrosHttp.append("=");
        	cadenaParametrosHttp.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        	cadenaParametrosHttp.append("&");
        }        
        return cadenaParametrosHttp.toString().length() > 0 ? cadenaParametrosHttp.toString().substring(0, cadenaParametrosHttp.toString().length() - 1):cadenaParametrosHttp.toString();
	}
	
	private static HashMap<String,String> solicitarParametrosPeticion() {
		HashMap<String,String> parametrosPeticion = new HashMap<String,String>();
		Scanner scn = new Scanner(System.in);
		boolean respuestaAgregarParametrosValida = false;
		String agregarParametros = "";
		while(!respuestaAgregarParametrosValida) {
			System.out.println("Desea agregar parametros? S/N");
			agregarParametros = scn.next();
			if(agregarParametros.trim().equalsIgnoreCase("S") || agregarParametros.trim().equalsIgnoreCase("N")) {
				respuestaAgregarParametrosValida = true;
			}
		}
		if(agregarParametros.trim().equalsIgnoreCase("S")) {
			boolean agregarMasParametros = true;
			while(agregarMasParametros) {
				System.out.println("Ingrese la clave: ");
				String clave = scn.next();
				System.out.println("Ingrese el valor: ");
				String valor = scn.next();
				parametrosPeticion.put(clave, valor);
				String respuestaAgregarMasParametros = "";
				boolean respuestaAgregarMasParametrosValida = false;
				while(!respuestaAgregarMasParametrosValida) {
					System.out.println("Desea agregar mas parametros? S/N");
					respuestaAgregarMasParametros = scn.next();
					if(respuestaAgregarMasParametros.trim().equals("S") || respuestaAgregarMasParametros.trim().equals("N")) {
						respuestaAgregarMasParametrosValida = true;
					}
				}
				if(respuestaAgregarMasParametros.trim().equalsIgnoreCase("N")) {
					agregarMasParametros = false;
				}
			}
		}
		return parametrosPeticion;
	}

}
