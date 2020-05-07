package snmp.cliente;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.AgentConfigManager;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB.SnmpCommunityEntryRow;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;


@SuppressWarnings("deprecation")
public class ClienteSNMP extends BaseAgent{


	public ClienteSNMP() throws IOException {

        super(new File("agenteEjemploP4A1"), new File("agenteEjemploP4A2"), new CommandProcessor(new OctetString("agenteEjemploP4")));
        init();
        addShutdownHook();
        getServer().addContext(new OctetString("public"));
        finishInit();
        sendColdStartNotification();
		this.unregisterManagedObject(this.getSnmpv2MIB());
		//Registramos nuestro objeto en la MIB, en el OID 1.3.6.1.2.1.1.1.0
		this.registerManagedObject(new MOScalar<OctetString>(new OID("1.3.6.1.2.1.1.1.0"), MOAccessImpl.ACCESS_READ_CREATE, new OctetString("En este OID, deberia ir la descripcion del sistema pero si ve esto esta funcionando el agente SNMP que hicimos <3")));
    }

	public void unregisterManagedObject(MOGroup moGroup) {
		moGroup.unregisterMOs(server, getContext(moGroup));
	}

	public void registerManagedObject(ManagedObject mo) {
		try {
			server.register(mo, null);
		} catch (DuplicateRegistrationException ex) {
			throw new RuntimeException(ex);
		}
	}


	@Override
	protected void registerManagedObjects() {}

	@Override
	protected void unregisterManagedObjects() {}

	@Override
	protected void addUsmUser(USM usm) {}

	@Override
	protected void addNotificationTargets(SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) {}

    @Override
    protected void initTransportMappings() throws IOException {
        //Configuramos el agente para que escuche peticiones en el puerto 3161, usando UDP
    	transportMappings = new TransportMapping[1];	
        transportMappings[0] = new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/3161"));
    }

	@Override
	protected void addViews(VacmMIB vacm) {
        vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString("cpublic"), new OctetString("v1v2group"),StorageType.nonVolatile);
		vacm.addAccess(new OctetString("v1v2group"), new OctetString("public"),SecurityModel.SECURITY_MODEL_ANY, SecurityLevel.NOAUTH_NOPRIV,MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),new OctetString("fullWriteView"), new OctetString("fullNotifyView"), StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString("fullReadView"), new OID("1.3"),new OctetString(), VacmMIB.vacmViewIncluded,StorageType.nonVolatile);
		vacm.addViewTreeFamily(new OctetString("fullWriteView"), new OID("1.3"),new OctetString(), VacmMIB.vacmViewIncluded,StorageType.nonVolatile);

	}

	@Override
	protected void addCommunities(SnmpCommunityMIB communityMIB) {
		//Creamos la comunidad public
        communityMIB.getSnmpCommunityEntry().addRow(communityMIB.getSnmpCommunityEntry().createRow(new OctetString("public2public").toSubIndex(true), new Variable[]{new OctetString("public"),new OctetString("cpublic"),getAgent().getContextEngineID(),new OctetString("public"),new OctetString(),new Integer32(StorageType.nonVolatile),new Integer32(RowStatus.active)}));
        
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Iniciando agente SNMP...");
		ClienteSNMP cliente = new ClienteSNMP();
		System.out.println("Agente SNMP iniciado, escuchando peticiones en el puerto 3161");
		while(true) {
			cliente.run();
		}

	}
	
	
}
