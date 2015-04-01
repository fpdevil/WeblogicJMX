package com.jmx.wls.Helper;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmx.wls.core.WlsStateCollector;
import com.jmx.wls.runtime.ServerStatus;
import com.jmx.wls.utils.ServerConnectionInterface;

public class WeblogicConnectionBroker implements ServerConnectionInterface {
	
	private static final Logger LOG = LoggerFactory.getLogger(ServerStatus.class);
	WlsStateCollector weblogicStateCollector = new WlsStateCollector();	

	public WlsStateCollector getWeblogicStateCollector() {
		return weblogicStateCollector;
	}
	
	@Override
	public void initConnection(String hostname, String portString,
			String username, String password) {
		// TODO Auto-generated method stub
		try {
			LOG.info("Iniating connection to {}:{}", hostname, portString);
			getWeblogicStateCollector().initConnection(hostname, portString, username, password);
		} catch (AttributeNotFoundException e) {
			// TODO Auto-generated catch block
			LOG.error("AttributeNotFoundException {}", e.getMessage());
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			LOG.error("InstanceNotFoundException {}", e.getMessage());
			e.printStackTrace();
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			LOG.error("MBeanException {}", e.getMessage());
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			LOG.error("ReflectionException {}", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public MBeanServerConnection getMBeanConnection() {
		return getWeblogicStateCollector().getConnection();
	}
	
	public ObjectName getDomainConfiguration() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		return getWeblogicStateCollector().getDomainConfiguration();
	}
	
	public ObjectName[] getDomainServerConfiguration() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		return getWeblogicStateCollector().getDomainServerConfiguration();
	}
	
	public ObjectName getDomainRuntime() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		return getWeblogicStateCollector().getDomainRuntime();
	}
	public ObjectName[] getWlsServerRT() {
		return getWeblogicStateCollector().getServerRT();
	}
	
	public void closeConnection() {
		getWeblogicStateCollector().closeConnection();
	}
	
	public void usage() {
		getWeblogicStateCollector().usage();
	}
}
