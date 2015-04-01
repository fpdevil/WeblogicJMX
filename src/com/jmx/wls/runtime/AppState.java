package com.jmx.wls.runtime;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmx.wls.Helper.WeblogicConnectionBroker;

public class AppState {

	private static Logger LOG = LoggerFactory.getLogger(AppState.class);

	static WeblogicConnectionBroker broker = new WeblogicConnectionBroker();

	public void initConnection(String hostname, String portString,
			String username, String password) {
		broker.initConnection(hostname, portString, username, password);
	}

	public MBeanServerConnection getMBeanConnection() {
		return broker.getMBeanConnection();
	}

	public ObjectName getDomainConfiguration()
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		return broker.getDomainConfiguration();
	}

	public ObjectName[] getDomainServerConfiguration()
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		return broker.getDomainServerConfiguration();
	}

	public ObjectName[] getWlsServerRT() {
		return broker.getWlsServerRT();
	}

	public ObjectName getAppRuntimeStateRuntime(ObjectName domainRuntime)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		return (ObjectName) getMBeanConnection().getAttribute(domainRuntime,
				"AppRuntimeStateRuntime");
	}

	public String[] getApplicationIds(ObjectName domainRuntime)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		String[] appIds = (String[]) getMBeanConnection().getAttribute(
				getAppRuntimeStateRuntime(domainRuntime), "ApplicationIds");
		LOG.info("Total of {} Applicationd deployed", appIds.length);
		return appIds;
	}

}
