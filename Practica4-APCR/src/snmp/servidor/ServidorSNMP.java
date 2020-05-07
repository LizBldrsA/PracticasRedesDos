package snmp.servidor;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Scanner;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import org.snmp4j.smi.Variable;


public class ServidorSNMP {
	
	public static void main (String args[]) {
		Scanner scn = new Scanner(System.in);
		System.out.println("*******************************");
		System.out.println("* Bienvenido al servidor SNMP *");
		System.out.println("*******************************");
		System.out.println("");
		System.out.println("");
		System.out.println("Para realizar una consulta o asignacion a un agente, ingrese los datos que se le solicitan");
		System.out.println("");
		System.out.println("Ingrese la IP del agente: ");
		String ipAgente = scn.next();
		boolean puertoValido = false;
		String puertoAgente = "";
		while(!puertoValido) {
			System.out.println("Ingrese el puerto del agente: ");
			puertoAgente = scn.next();
			try {
				Integer.parseInt(puertoAgente);
				puertoValido = true;
			}catch(NumberFormatException ex) {
				System.out.println("El puerto ingresado es invalido");
			}
		}
		
		
		System.out.println("Ingrese la comunidad del agente: ");
		String comunidadAgente = scn.next();
		String versionSNMP = "";	
		boolean versionSNMPValida = false; 
		while(!versionSNMPValida) {
			System.out.println("Ingrese la version de SNMP a utilizar: ");
			System.out.println("\t 1) Version 1 ");
			System.out.println("\t 2) Version 2c ");
			String opcionVersionString = scn.next();
			try {
				int opcionVersion = Integer.parseInt(opcionVersionString);
				if(opcionVersion == 1) {
					versionSNMP = "1";
					versionSNMPValida = true;
				}else if (opcionVersion == 2) {
					versionSNMP = "2c";
					versionSNMPValida = true;
				}else {
					System.out.println("La version ingresada no es valida");
				}
				
			}catch(NumberFormatException ex) {
				System.out.println("La version ingresada no es valida");
			}
		}
	
		System.out.println("Ingrese el Object ID: ");
		String objectID = scn.next();
		boolean operacionValida = false;
		int operacion = 0;
		while(!operacionValida) {
			System.out.println("Ingrese la operacion a realizar: ");
			System.out.println("\t 1) Get");
			System.out.println("\t 2) Set");
			try {
				operacion = Integer.valueOf(scn.next());
				if(operacion<3) {
					operacionValida=true;
				}
			}catch(NumberFormatException ex) {
				
			}
		}
		if(operacion == 1) {
			System.out.println("Resultado: ");
			try {
				System.out.println(objectID+" : "+ejecutarSNMPGet(ipAgente, puertoAgente, comunidadAgente, versionSNMP, objectID));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				System.out.println("Ingrese el nuevo valor para el OID: ");
				System.out.println(objectID+" : "+ejecutarSNMPSet(ipAgente, puertoAgente, comunidadAgente, versionSNMP, objectID,scn.next()));
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static String ejecutarSNMPGet(String ipAgente,String puertoAgente,String comunidadAgente,String versionSNMP,String objectID) throws IOException {
	    TransportMapping transport = new DefaultUdpTransportMapping();
	    transport.listen();
	    CommunityTarget comtarget = new CommunityTarget();
	    comtarget.setCommunity(new OctetString(comunidadAgente));
	    int snmpVersion = SnmpConstants.version1;
	    if(versionSNMP.trim().equals("2c")){
	    	snmpVersion = SnmpConstants.version2c;
	    }
	    comtarget.setVersion(snmpVersion);
	    comtarget.setAddress(new UdpAddress(ipAgente + "/" + puertoAgente));
	    comtarget.setRetries(0);
	    comtarget.setTimeout(1000);
	    PDU pdu = new PDU();
	    pdu.add(new VariableBinding(new OID(objectID)));
	    pdu.setType(PDU.GET);
	    pdu.setRequestID(new Integer32(1));
	    Snmp snmp = new Snmp(transport);
	    ResponseEvent response = snmp.get(pdu, comtarget);
	    if (response != null){
	      PDU responsePDU = response.getResponse();
	      if (responsePDU != null){
	        int errorStatus = responsePDU.getErrorStatus();
	        int errorIndex = responsePDU.getErrorIndex();
	        String errorStatusText = responsePDU.getErrorStatusText();
	        if (errorStatus == PDU.noError){
	          String result = responsePDU.getVariableBindings().get(0).toString().split("=")[1];
	          snmp.close();
	          return result;
	        }else{
	          snmp.close();
	          return "Error: La consulta ha fallado \t Estado = " + errorStatus+"\t Indice = " + errorIndex+"\t Descripcion = " + errorStatusText;
	        }
	      }else{
	        snmp.close();
	        return "Error: La respuesta es nula";
	      }
	    }else{
	      snmp.close();
	      return "Error: Tiempo de espera excedido";
	    }
	    
	}
	
	private static String ejecutarSNMPSet(String ipAgente,String puertoAgente,String comunidadAgente,String versionSNMP,String objectID,String nuevoValor) throws IOException, ParseException {
	    TransportMapping transport = new DefaultUdpTransportMapping();
	    transport.listen();
	    CommunityTarget comtarget = new CommunityTarget();
	    comtarget.setCommunity(new OctetString(comunidadAgente));
	    int snmpVersion = SnmpConstants.version1;
	    if(versionSNMP.trim().equals("2c")){
	    	snmpVersion = SnmpConstants.version2c;
	    }
	    comtarget.setVersion(snmpVersion);
	    comtarget.setAddress(new UdpAddress(ipAgente + "/" + puertoAgente));
	    comtarget.setRetries(0);
	    comtarget.setTimeout(1000);
	    PDU pdu = new PDU();
	    pdu.add(new VariableBinding(new OID(objectID),new OctetString(nuevoValor)));
	    pdu.setType(PDU.SET);
	    pdu.setRequestID(new Integer32(1));
	    Snmp snmp = new Snmp(transport);
	    ResponseEvent response = snmp.set(pdu, comtarget);
	    if (response != null){
	      PDU responsePDU = response.getResponse();
	      if (responsePDU != null){
	        int errorStatus = responsePDU.getErrorStatus();
	        int errorIndex = responsePDU.getErrorIndex();
	        String errorStatusText = responsePDU.getErrorStatusText();
	        if (errorStatus == PDU.noError){
	          String result = responsePDU.getVariableBindings().get(0).toString().split("=")[1];
	          snmp.close();
	          return result;
	        }else{
	          snmp.close();
	          return "Error: La asignacion ha fallado \t Estado = " + errorStatus+"\t Indice = " + errorIndex+"\t Descripcion = " + errorStatusText;
	        }
	      }else{
	        snmp.close();
	        return "Error: La respuesta es nula";
	      }
	    }else{
	      snmp.close();
	      return "Error: Tiempo de espera excedido";
	    }
	    
	}
}
