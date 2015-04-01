package com.jmx.wls.runtime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weblogic.health.HealthState;

import com.jmx.wls.Helper.WeblogicConnectionBroker;
import com.jmx.wls.core.WlsStateCollector;

public class ServerStatus {
	
	public TreeMap<String, Object> wlsServerRTMap = new TreeMap<String, Object>();
	public Map<String, Object> wlsDomainServerRTMap = new HashMap<String, Object>();
	public List<String> wlsServerRTList = new ArrayList<String>();
	
	public List<String> wlsServersRunningList = new ArrayList<String>();
	public List<String> wlsServersShutdownList = new ArrayList<String>(); 
	
	private static final Logger LOG = LoggerFactory.getLogger(ServerStatus.class);
	
	static WeblogicConnectionBroker wlsBroker = new WeblogicConnectionBroker();

	public TreeMap<String, Object> getWlsServerRTMap() {
		return wlsServerRTMap;
	}

	public Map<String, Object> getWlsDomainServerRTMap() {
		return wlsDomainServerRTMap;
	}

	public List<String> getWlsServerRTList() {
		return wlsServerRTList;
	}

	public List<String> getWlsServersRunningList() {
		return wlsServersRunningList;
	}

	public List<String> getWlsServersShutdownList() {
		return wlsServersShutdownList;
	}

	public MBeanServerConnection getConnection() {
		return wlsBroker.getMBeanConnection();
	}
	
	public void initConnection(String hostname, String portString, String username, String password) {
		wlsBroker.initConnection(hostname, portString, username, password);
	}
	
	public ObjectName getDomainConfiguration() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		return wlsBroker.getDomainConfiguration();
	}
	
	public ObjectName[] getDomainServerConfiguration() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		return wlsBroker.getDomainServerConfiguration();
	}
	
	public String getServerActivationTime(long millisec) {
		// Date Formatter
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d'th' yyyy 'at' h:mm:ss a", Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("CST"));
		Date activationDate = new Date(millisec);
		return sdf.format(activationDate);
	}

	public ObjectName[] getWlsServerRT() {
		return wlsBroker.getWlsServerRT();
	}
	
	public String getAdminSvr() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		String adminSvr = (String) getConnection().getAttribute(getDomainConfiguration(), "AdminServerName");
		LOG.debug("Admin Server from the Domain Configuration: {}", adminSvr);
		return adminSvr;
	}

	public void closeConnection() {
		wlsBroker.closeConnection();
	}
	
	public List<String> getDomainConfigServerName() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		this.wlsServerRTList.clear();
		
		for (ObjectName svr : getDomainServerConfiguration())
		{
			this.wlsServerRTList.add((String) getConnection().getAttribute(svr, "Name"));
		}
		if (this.wlsServerRTList.contains(getAdminSvr()))
		{
			this.wlsServerRTList.remove(getAdminSvr());
		}
		return wlsServerRTList;
	}
	
	public void getRuntimeServerStats(List<String> domainSvrList) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		ObjectName[] wlsServerRuntimes = getWlsServerRT();
		List<ObjectName> wlsServerRuntimeStatusList = Arrays.asList(wlsServerRuntimes);
		
		this.wlsServersRunningList.clear();
		this.wlsServersShutdownList.clear();
			
		for (ObjectName wsname : wlsServerRuntimes)
		{
			String wlsServerRuntimeName = (String) getConnection().getAttribute(wsname, "Name");
			this.wlsServersRunningList.add(wlsServerRuntimeName);
		}
		if (this.wlsServersRunningList.contains(getAdminSvr()))
		{
			this.wlsServersRunningList.remove(getAdminSvr());
		}
		LOG.info("Number of Managed Servers is {} from Server Runtime MBean: {}", (wlsServerRuntimeStatusList.size() - 1), wlsServerRuntimeStatusList);
		if (wlsServersRunningList.size() < domainSvrList.size())
		{
			for (String dom : domainSvrList)
			{
				if (!wlsServersRunningList.contains(dom))
				{
					this.wlsServersShutdownList.add(dom);
				}
			}
		}
		LOG.info("Number of Managed Servers is {} from Domain Runtime MBean: {}", domainSvrList.size(), domainSvrList);
	}
	
	public Object getWlsServerHealthState(ObjectName objName) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		weblogic.health.HealthState wlh = (HealthState) getConnection().getAttribute(objName, "HealthState");
		return wlh.getState();
	}
	
	public Object[] getWlsServerHealthCode(ObjectName objName) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		weblogic.health.HealthState wlh = (HealthState) getConnection().getAttribute(objName, "HealthState");
		LOG.debug("HealthCode {}", wlh.getReasonCode());
		return wlh.getReasonCode();
	}
	
	public Map<String, Object> getWlsServerRuntimeInfo(ObjectName objName) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		this.wlsServerRTMap.clear();
		
		Object activationTime = getServerActivationTime((long) getConnection().getAttribute(objName, "ActivationTime"));
		
		this.wlsServerRTMap.put("Server Activation Time", activationTime);
		this.wlsServerRTMap.put("Server Health State", getWlsServerHealthState(objName));
		this.wlsServerRTMap.put("Server Health Code", Arrays.toString(getWlsServerHealthCode(objName)));
		this.wlsServerRTMap.put("Server Name", getConnection().getAttribute(objName, "Name"));
		this.wlsServerRTMap.put("Server State", getConnection().getAttribute(objName, "State"));
		this.wlsServerRTMap.put("SocketsOpenedTotalCount", getConnection().getAttribute(objName, "SocketsOpenedTotalCount"));
		this.wlsServerRTMap.put("HealthState", getConnection().getAttribute(objName, "HealthState"));
		this.wlsServerRTMap.put("OpenSocketsCurrentCount", getConnection().getAttribute(objName, "OpenSocketsCurrentCount"));
//		this.wlsServerRTMap.put("Whether AdminServer", getConnection().getAttribute(objName, "AdminServer"));
		this.wlsServerRTMap.put("RestartRequired", getConnection().getAttribute(objName, "RestartRequired"));
		this.wlsServerRTMap.put("RestartsTotalCount", getConnection().getAttribute(objName, "RestartsTotalCount"));
		this.wlsServerRTMap.put("Server URL", getConnection().getAttribute(objName, "DefaultURL"));
//		this.wlsServerRTMap.put("AdminServerHost", getConnection().getAttribute(objName, "AdminServerHost"));
//		this.wlsServerRTMap.put("AdminServerListenPort", getConnection().getAttribute(objName, "AdminServerListenPort"));
		
		return wlsServerRTMap;
	}
}
