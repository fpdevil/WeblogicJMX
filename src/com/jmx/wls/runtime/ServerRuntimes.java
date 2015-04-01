package com.jmx.wls.runtime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.jmx.wls.core.WlsStateCollector;

public class ServerRuntimes {
	
	public Map<String, Object> serverRTMap = new HashMap<String, Object>();
	static WlsStateCollector wsc = new WlsStateCollector();
	
	public Map<String, Object> getServerRTMap() {
		return serverRTMap;
	}

	public ObjectName[] serverRT() {
		return wsc.getServerRT();
	}

	public Map<String, Object> getServerRuntimeInfo(ObjectName objName) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		this.serverRTMap.clear();
		this.serverRTMap.put("Name", wsc.getConnection().getAttribute(objName, "Name"));
		this.serverRTMap.put("ListenPort", wsc.getConnection().getAttribute(objName, "ListenPort"));
		this.serverRTMap.put("ListenAddress", wsc.getConnection().getAttribute(objName, "ListenAddress"));
		this.serverRTMap.put("State", wsc.getConnection().getAttribute(objName, "State"));
		this.serverRTMap.put("StateVal", wsc.getConnection().getAttribute(objName, "StateVal"));
		this.serverRTMap.put("RestartRequired", wsc.getConnection().getAttribute(objName, "RestartRequired"));
		this.serverRTMap.put("RestartsTotalCount", wsc.getConnection().getAttribute(objName, "RestartsTotalCount"));
		this.serverRTMap.put("HealthState", wsc.getConnection().getAttribute(objName, "HealthState"));
		this.serverRTMap.put("AdminServer", wsc.getConnection().getAttribute(objName, "AdminServer"));
		this.serverRTMap.put("AdministrationURL", wsc.getConnection().getAttribute(objName, "AdministrationURL"));
		this.serverRTMap.put("OpenSocketsCurrentCount", wsc.getConnection().getAttribute(objName, "OpenSocketsCurrentCount"));
		this.serverRTMap.put("SocketsOpenedTotalCount", wsc.getConnection().getAttribute(objName, "SocketsOpenedTotalCount"));
		this.serverRTMap.put("Type", wsc.getConnection().getAttribute(objName, "Type"));
		this.serverRTMap.put("WeblogicVersion", wsc.getConnection().getAttribute(objName, "WeblogicVersion"));
		return serverRTMap;
	}
	
	public static String seperator = "=>";

	public static void displayStats(Map<String, Object> obj) {
		for (Map.Entry<String, Object> m : obj.entrySet()) {
			System.out.printf("%-32s %s %s", m.getKey(), seperator,
					m.getValue());
			System.out.println();
		}
	}
	
	public static void main(String[] args) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		if (args.length == 0) {
			wsc.usage();
			System.exit(1);
		}

		String hostname = args[0];
		String portString = args[1];
		String username = args[2];
		String password = args[3];
		
		ServerRuntimes sr = new ServerRuntimes();
		
		wsc.initConnection(hostname, portString, username, password);

		for (int i = 0; i < sr.serverRT().length; i++) {
			System.out.println("----------------------------------------------");
			sr.getServerRuntimeInfo(sr.serverRT()[i]);
			ServerRuntimes.displayStats(sr.getServerRTMap());
		}
		
		wsc.closeConnection();
	}

}
