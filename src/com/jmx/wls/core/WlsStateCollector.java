package com.jmx.wls.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weblogic.health.HealthState;

import com.jmx.wls.utils.ServerConnectionInterface;
import com.jmx.wls.utils.ServerStatsInterface;

/**
 * @author n073866 (Sampath Singamsetty)
 * 
 */
public class WlsStateCollector implements ServerStatsInterface, ServerConnectionInterface {

	private JMXConnector jmxConnector;

	private MBeanServerConnection connection;

	private static final ObjectName domainRuntimeServiceMBean;

	private static final Logger LOG = LoggerFactory
			.getLogger(WlsStateCollector.class);

	private ObjectName[] serverRT;

	private Map<String, Object> jvmRTMap = new HashMap<String, Object>();

	private Map<String, Object> serverRTMap = new HashMap<String, Object>();
	
	private Map<String, Object> threadPoolRTMap = new HashMap<String, Object>();
	
	private Map<String, Object> jdbcDataSourceRTMap = new HashMap<String, Object>();
	
	private List<Object> appListByName = new ArrayList<Object>();
	
	private Map<String, Object> wtcRTMap = new HashMap<String, Object>();
	
	static {
		try {
			domainRuntimeServiceMBean = new ObjectName(
					"com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
		} catch (MalformedObjectNameException e) {
			LOG.error("Error in creating the initial service runtime mbean: ",
					e.getStackTrace());
			throw new IllegalStateException(e);
		}
	}

	public MBeanServerConnection getConnection() {
//		LOG.debug("MBeanServerConnection object...{}", connection);
		return connection;
	}

	protected Map<String, String> getJMXContextProperties() {
		Map<String, String> props = new HashMap<String, String>();
		props.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
				"weblogic.management.remote");
		return props;
	}


	/* (non-Javadoc)
	 * @see com.jmx.wls.utils.ServerConnectionInterface#initConnection(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void initConnection(String hostname, String portString,
			String username, String password)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException {

		if (hostname == "null" || hostname == "")
			hostname = "127.0.0.1";
		if (portString == null || portString == "")
			portString = "7001";
		if (username == "null" || username == "")
			username = "weblogic";
		if (password == "null" || password == "")
			password = "welcome1";

		if (jmxConnector == null) {
			String protocol = "t3";
			Integer portInteger = Integer.valueOf(portString);
			int port = portInteger.intValue();
			String jndiroot = "/jndi/";
			String mserver = "weblogic.management.mbeanservers.domainruntime";

			LOG.info("Connecting to the Weblogic JMX System...");

			try {
				JMXServiceURL serviceURL = new JMXServiceURL(protocol,
						hostname, port, jndiroot + mserver);
				LOG.debug("Creating a JMXServiceURL Object...{}", serviceURL);

				Map<String, String> h = getJMXContextProperties();
				h.put(Context.SECURITY_PRINCIPAL, username);
				h.put(Context.SECURITY_CREDENTIALS, password);
				jmxConnector = JMXConnectorFactory.connect(serviceURL, h);
				LOG.debug("JMXConnector...{}", jmxConnector);
				connection = jmxConnector.getMBeanServerConnection();
				LOG.debug("MBean Server Connection...{}", connection);

				serverRT = getServerRuntimes();
				LOG.debug("ServerRuntime MBean Reference...{}", getServerRT().toString());
				LOG.debug("Got Server Runtime Info for {} weblogic instances", getServerRT().length);
//				System.out.println("Got Server runtime info for "
//						+ getServerRT().length + " instances \n");

			} catch (IOException e) {
				LOG.error("Error creating the JMXServiceURL Object: ",
						e.toString());
				e.printStackTrace();
			}

		}
	}

	/**
	 * For closing the Connection channel to the JMXConnector Object
	 */
	public void closeConnection() {
		try {
			LOG.debug("JMXConnector Object Status...{}", jmxConnector);
			if (jmxConnector != null) {
				LOG.info("Closing the JMXConnector object connection...{}",
						jmxConnector);
				jmxConnector.close();
			}
		} catch (IOException e) {
			LOG.debug(
					"Error in closing the connection to JMXConnector object...{}",
					jmxConnector);
			LOG.error(
					"Unable to close the connection to the JMXConnector object: ",
					e.getMessage());
			e.printStackTrace();
		}
	}

	public ObjectName[] getServerRT() {
		return serverRT;
	}
	
	
	/**
	 * @return Active DomainMBean for the current WebLogic Server domain
	 * This MBean lists all the information about the active configuration 
	 * of all servers and resources in the domain
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public ObjectName getDomainConfiguration() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		LOG.debug("Getting DomainConfigurationMBean...");
		LOG.debug("DomainRuntimeServiceMBean...{}", domainRuntimeServiceMBean);
		ObjectName domainConfig = (ObjectName) getConnection().getAttribute(domainRuntimeServiceMBean, "DomainConfiguration");
		return domainConfig;
	}
	
	/**
	 * @return Contains the DomainRuntimeMBean for the current WebLogic Server domain.
	 * This MBean provides access to the special service interfaces that exist only on 
	 * the Administration Server and provide life cycle control over the domain
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public ObjectName getDomainRuntime() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		ObjectName domainRuntime = (ObjectName) getConnection().getAttribute(domainRuntimeServiceMBean, "DomainRuntime");
		return domainRuntime;
	}
	
	/**
	 * @return The ObjectNames for all ServerMBeans in the domain by getting
	 * the value of the DomainMBean Servers attribute
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public ObjectName[] getDomainServerConfiguration() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		LOG.debug("Getting Server configuration from Domain...{}", getDomainConfiguration());
		ObjectName[] domainConfigServerMB = (ObjectName[]) getConnection().getAttribute(getDomainConfiguration(), "Servers");
		return domainConfigServerMB;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jmx.wls.utils.ServerStatsIntf#getServerRuntimes()
	 */
	@Override
	public ObjectName[] getServerRuntimes() throws AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			IOException {
		return (ObjectName[]) getConnection().getAttribute(
				domainRuntimeServiceMBean, "ServerRuntimes");
	}

	/* (non-Javadoc)
	 * @see com.jmx.wls.utils.ServerStatsInterface#getJVMRuntime(javax.management.ObjectName)
	 */
	@Override
	public ObjectName getJVMRuntime(ObjectName on)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		LOG.debug("Object Name inside getJVMRuntime(Obj)...{}", on);
		ObjectName jvmRTMB = (ObjectName) getConnection().getAttribute(on,
				"JVMRuntime");
		LOG.debug("JVM Runtime MBean...{}", jvmRTMB);
		return jvmRTMB;
	}

	/* (non-Javadoc)
	 * @see com.jmx.wls.utils.ServerStatsInterface#getThreadPoolRuntime(javax.management.ObjectName)
	 */
	@Override
	public ObjectName getThreadPoolRuntime(ObjectName on) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		LOG.debug("Object Name inside getThreadPoolRuntime(Obj)...{}", on);
		ObjectName threadPoolRTMB = (ObjectName) getConnection().getAttribute(on, "ThreadPoolRuntime");
		LOG.debug("ThreadPool Runtime MBean...{}", threadPoolRTMB);
		return threadPoolRTMB;
	}
	
	
	/* (non-Javadoc)
	 * @see com.jmx.wls.utils.ServerStatsInterface#getJDBCRuntime(javax.management.ObjectName)
	 */
	@Override
	public ObjectName getJDBCRuntime(ObjectName on) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		LOG.debug("Object Name inside the getJDBCRuntime...{}", on);
		ObjectName jdbcRTMB = (ObjectName) getConnection().getAttribute(on, "JDBCServiceRuntime");
		LOG.debug("JDBC Service Runtime MBean...{}", jdbcRTMB);
		return jdbcRTMB;
	}
	
	/* (non-Javadoc)
	 * @see com.jmx.wls.utils.ServerStatsInterface#getWTCRuntime(javax.management.ObjectName)
	 */
	@Override
	public ObjectName[] getWTCRuntime()
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		LOG.debug("ObjectName inside the getWTCRuntime...{}", getDomainConfiguration());
		ObjectName[] wtcServerMB = (ObjectName[]) getConnection().getAttribute(getDomainConfiguration(), "WTCServers");
		LOG.debug("WTC Runtime MBean...{}", wtcServerMB);
		return wtcServerMB;
	}
	
	/* (non-Javadoc)
	 * @see com.jmx.wls.utils.ServerStatsInterface#getApplicationRuntime()
	 */
	@Override
	public ObjectName[] getApplicationRuntime()
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		LOG.debug("Getting AppDeployments from Domain Configuration...{}", getDomainConfiguration());
		ObjectName[] appDeploymentsRTMB = (ObjectName[]) getConnection().getAttribute(getDomainConfiguration(), "AppDeployments");
		LOG.debug("AppDeploymentMBean...{}", appDeploymentsRTMB);
		return appDeploymentsRTMB;
	}
	
	public List<Object> getDeploymentsList() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		this.appListByName.clear();
		for (int deployList = 0; deployList < getApplicationRuntime().length; deployList++) {
			this.appListByName.add(getConnection().getAttribute(getApplicationRuntime()[deployList], "Name"));
		}
		LOG.debug("appListByName...{}", appListByName);
		return this.appListByName;
	}
	
	public ObjectName[] getJDBCDataSourceRuntime(ObjectName obj) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException  {
		LOG.debug("ObjectName inside the getJDBCDataSourceRuntime(obj)...{}", obj);
		ObjectName[] jdbcDataSourceRTMB = (ObjectName[]) getConnection().getAttribute(getJDBCRuntime(obj), "JDBCDataSourceRuntimeMBeans");
		LOG.debug("JDBC DataSource Runtime MBean...{}", jdbcDataSourceRTMB);
		return jdbcDataSourceRTMB;
	}
	
	public String getHealthState(ObjectName obj) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		weblogic.health.HealthState hs = (HealthState) getConnection().getAttribute(obj, "HealthState");
		String hState;
		LOG.info("Object weblogic.health.HealthState {}", hs);
		LOG.info("Health State Code: {}", hs.getState());
		switch(hs.getState()) {
		case 0:
			hState = "HEALTH_OK";
			break;
		case 1:
			hState = "HEALTH_WARN";
			break;
		case 2:
			hState = "HEALTH_CRITICAL";
			break;
		case 3:
			hState = "HEALTH_FAILED";
			break;
		case 4:
			hState = "HEALTH_OVERLOADED";
			break;
		default :
			hState = "LOW_MEMORY_REASON";
			break;
		}
		return hState;
	}
	
	public String[] getReasonCode(ObjectName obj) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		weblogic.health.HealthState hs = (HealthState) getConnection().getAttribute(obj, "HealthState");
		return hs.getReasonCode();
	}

	public Map<String, Object> getJVMInfo(ObjectName on)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		this.jvmRTMap.clear();
		this.jvmRTMap.put("OSName", getConnection().getAttribute(getJVMRuntime(on), "OSName"));
		this.jvmRTMap.put("OSVersion", getConnection().getAttribute(getJVMRuntime(on), "OSVersion"));
		this.jvmRTMap.put("Uptime", getConnection().getAttribute(getJVMRuntime(on), "Uptime"));
		this.jvmRTMap.put("HeapFreeCurrent", getConnection().getAttribute(getJVMRuntime(on), "HeapFreeCurrent"));
		this.jvmRTMap.put("HeapFreePercent", getConnection().getAttribute(getJVMRuntime(on), "HeapFreePercent"));
		this.jvmRTMap.put("HeapSizeCurrent", getConnection().getAttribute(getJVMRuntime(on), "HeapSizeCurrent"));
		this.jvmRTMap.put("HeapSizeMax", getConnection().getAttribute(getJVMRuntime(on), "HeapSizeMax"));
		this.jvmRTMap.put("JavaVersion", getConnection().getAttribute(getJVMRuntime(on), "JavaVersion"));
		this.jvmRTMap.put("JavaVMVendor", getConnection().getAttribute(getJVMRuntime(on), "JavaVMVendor"));
		return this.jvmRTMap;
	}

	public Map<String, Object> getServerRuntimeInfo(ObjectName on)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException {
		this.serverRTMap.clear();
		this.serverRTMap.put("Name", getConnection().getAttribute(on, "Name"));
		this.serverRTMap.put("ListenPort", getConnection().getAttribute(on, "ListenPort"));
		this.serverRTMap.put("ListenAddress", getConnection().getAttribute(on, "ListenAddress"));
		this.serverRTMap.put("State", getConnection().getAttribute(on, "State"));
		this.serverRTMap.put("StateVal", getConnection().getAttribute(on, "StateVal"));
		this.serverRTMap.put("RestartRequired", getConnection().getAttribute(on, "RestartRequired"));
		this.serverRTMap.put("RestartsTotalCount", getConnection().getAttribute(on, "RestartsTotalCount"));
		this.serverRTMap.put("HealthState", getConnection().getAttribute(on, "HealthState"));
//		this.serverRTMap.put("AdminServer", getConnection().getAttribute(on, "AdminServer"));
		this.serverRTMap.put("AdministrationURL", getConnection().getAttribute(on, "AdministrationURL"));
		this.serverRTMap.put("OpenSocketsCurrentCount", getConnection().getAttribute(on, "OpenSocketsCurrentCount"));
		this.serverRTMap.put("SocketsOpenedTotalCount", getConnection().getAttribute(on, "SocketsOpenedTotalCount"));
//		this.serverRTMap.put("Type", getConnection().getAttribute(on, "Type"));
//		this.serverRTMap.put("WeblogicVersion", getConnection().getAttribute(on, "WeblogicVersion"));
		return this.serverRTMap;
	}
	
	public Map<String, Object> getThreadPoolRuntimeInfo(ObjectName on) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		this.threadPoolRTMap.clear();
		
		Object healthState = getConnection().getAttribute(getThreadPoolRuntime(on), "HealthState");
		String state = healthState.toString();
		String[] splits = state.split("[,:]+");
		this.threadPoolRTMap.put("ServerHealthState", splits[3]);
		if (splits[splits.length - 1] != null)
		{
			this.threadPoolRTMap.put("ReasonCode", splits[splits.length-1]);
		}
		else
		{
			this.threadPoolRTMap.put("ReasonCode", "[OK]");
		}
		
		
		this.threadPoolRTMap.put("CompletedRequestCount", getConnection().getAttribute(getThreadPoolRuntime(on), "CompletedRequestCount"));
		this.threadPoolRTMap.put("ExecuteThreads", getConnection().getAttribute(getThreadPoolRuntime(on), "ExecuteThreads"));
		this.threadPoolRTMap.put("ExecuteThreadIdleCount", getConnection().getAttribute(getThreadPoolRuntime(on), "ExecuteThreadIdleCount"));
		this.threadPoolRTMap.put("ExecuteThreadTotalCount", getConnection().getAttribute(getThreadPoolRuntime(on), "ExecuteThreadTotalCount"));
		this.threadPoolRTMap.put("HoggingThreadCount", getConnection().getAttribute(getThreadPoolRuntime(on), "HoggingThreadCount"));
		this.threadPoolRTMap.put("StandbyThreadCount", getConnection().getAttribute(getThreadPoolRuntime(on), "StandbyThreadCount"));
		this.threadPoolRTMap.put("QueueLength", getConnection().getAttribute(getThreadPoolRuntime(on), "QueueLength"));
//		this.threadPoolRTMap.put("CachingDisabled", getConnection().getAttribute(getThreadPoolRuntime(on), "CachingDisabled"));
//		this.threadPoolRTMap.put("HealthState", getConnection().getAttribute(getThreadPoolRuntime(on), "HealthState"));
//		this.threadPoolRTMap.put("Server Health", getHealthState(on));
//		this.threadPoolRTMap.put("Health Reason", Arrays.toString(getReasonCode(on)));
//		this.threadPoolRTMap.put("MBeanInfo", getConnection().getAttribute(getThreadPoolRuntime(on), "MBeanInfo"));
//		this.threadPoolRTMap.put("MinThreadsConstraintsCompleted", getConnection().getAttribute(getThreadPoolRuntime(on), "MinThreadsConstraintsCompleted"));
//		this.threadPoolRTMap.put("MinThreadsConstraintsPending", getConnection().getAttribute(getThreadPoolRuntime(on), "MinThreadsConstraintsPending"));
		this.threadPoolRTMap.put("Name", getConnection().getAttribute(getThreadPoolRuntime(on), "Name"));
//		this.threadPoolRTMap.put("ObjectName", getConnection().getAttribute(getThreadPoolRuntime(on), "ObjectName"));
//		this.threadPoolRTMap.put("Parent", getConnection().getAttribute(getThreadPoolRuntime(on), "Parent"));
		this.threadPoolRTMap.put("PendingUserRequestCount", getConnection().getAttribute(getThreadPoolRuntime(on), "PendingUserRequestCount"));
//		this.threadPoolRTMap.put("Registered", getConnection().getAttribute(getThreadPoolRuntime(on), "Registered"));
//		this.threadPoolRTMap.put("SharedCapacityForWorkManagers", getConnection().getAttribute(getThreadPoolRuntime(on), "SharedCapacityForWorkManagers"));
		this.threadPoolRTMap.put("Suspended", getConnection().getAttribute(getThreadPoolRuntime(on), "Suspended"));
		this.threadPoolRTMap.put("Throughput", getConnection().getAttribute(getThreadPoolRuntime(on), "Throughput"));
//		this.threadPoolRTMap.put("Type", getConnection().getAttribute(getThreadPoolRuntime(on), "Type"));
		return this.threadPoolRTMap;
	}
	
	public Map<String, Object> getJDBCDataSourceRTInfo(ObjectName obj) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		this.jdbcDataSourceRTMap.clear();
		for (ObjectName jdbcProps :  getJDBCDataSourceRuntime(obj)) {
			
			LOG.debug("Checking the weblogic Instance {}", getConnection().getAttribute(getJDBCRuntime(obj), "Name"));
			
		    this.jdbcDataSourceRTMap.put("Name", getConnection().getAttribute(jdbcProps, "Name"));
			this.jdbcDataSourceRTMap.put("CurrCapacity", getConnection().getAttribute(jdbcProps, "CurrCapacity"));
			this.jdbcDataSourceRTMap.put("ActiveConnectionsAverageCount", getConnection().getAttribute(jdbcProps, "ActiveConnectionsAverageCount"));
			this.jdbcDataSourceRTMap.put("ActiveConnectionsCurrentCount", getConnection().getAttribute(jdbcProps, "ActiveConnectionsCurrentCount"));
			this.jdbcDataSourceRTMap.put("ActiveConnectionsHighCount", getConnection().getAttribute(jdbcProps, "ActiveConnectionsHighCount"));
//			this.jdbcDataSourceRTMap.put("CachingDisabled", getConnection().getAttribute(jdbcProps, "CachingDisabled"));
			this.jdbcDataSourceRTMap.put("ConnectionDelayTime", getConnection().getAttribute(jdbcProps, "ConnectionDelayTime"));
			this.jdbcDataSourceRTMap.put("ConnectionsTotalCount", getConnection().getAttribute(jdbcProps, "ConnectionsTotalCount"));
			this.jdbcDataSourceRTMap.put("CurrCapacity", getConnection().getAttribute(jdbcProps, "CurrCapacity"));
			this.jdbcDataSourceRTMap.put("CurrCapacityHighCount", getConnection().getAttribute(jdbcProps, "CurrCapacityHighCount"));
//			this.jdbcDataSourceRTMap.put("DeploymentState", getConnection().getAttribute(jdbcProps, "DeploymentState"));
//			this.jdbcDataSourceRTMap.put("Enabled", getConnection().getAttribute(jdbcProps, "Enabled"));
			this.jdbcDataSourceRTMap.put("FailedReserveRequestCount", getConnection().getAttribute(jdbcProps, "FailedReserveRequestCount"));
			this.jdbcDataSourceRTMap.put("FailuresToReconnectCount", getConnection().getAttribute(jdbcProps, "FailuresToReconnectCount"));
			this.jdbcDataSourceRTMap.put("HighestNumAvailable", getConnection().getAttribute(jdbcProps, "HighestNumAvailable"));
			this.jdbcDataSourceRTMap.put("HighestNumUnavailable", getConnection().getAttribute(jdbcProps, "HighestNumUnavailable"));
			this.jdbcDataSourceRTMap.put("LeakedConnectionCount", getConnection().getAttribute(jdbcProps, "LeakedConnectionCount"));
//			MBean Info is Deprecated
//			this.jdbcDataSourceRTMap.put("MBeanInfo", getConnection().getAttribute(jdbcProps, "MBeanInfo"));
			this.jdbcDataSourceRTMap.put("ModuleId", getConnection().getAttribute(jdbcProps, "ModuleId"));
			this.jdbcDataSourceRTMap.put("NumAvailable", getConnection().getAttribute(jdbcProps, "NumAvailable"));
			this.jdbcDataSourceRTMap.put("NumUnavailable", getConnection().getAttribute(jdbcProps, "NumUnavailable"));
//			ObjectName is Deprecated
//			this.jdbcDataSourceRTMap.put("ObjectName", getConnection().getAttribute(jdbcProps, "ObjectName"));
			this.jdbcDataSourceRTMap.put("Parent", getConnection().getAttribute(jdbcProps, "Parent"));
//			this.jdbcDataSourceRTMap.put("PrepStmtCacheAccessCount", getConnection().getAttribute(jdbcProps, "PrepStmtCacheAccessCount"));
//			this.jdbcDataSourceRTMap.put("PrepStmtCacheAddCount", getConnection().getAttribute(jdbcProps, "PrepStmtCacheAddCount"));
//			this.jdbcDataSourceRTMap.put("PrepStmtCacheCurrentSize", getConnection().getAttribute(jdbcProps, "PrepStmtCacheCurrentSize"));
//			this.jdbcDataSourceRTMap.put("PrepStmtCacheDeleteCount", getConnection().getAttribute(jdbcProps, "PrepStmtCacheDeleteCount"));
//			this.jdbcDataSourceRTMap.put("PrepStmtCacheHitCount", getConnection().getAttribute(jdbcProps, "PrepStmtCacheHitCount"));
//			this.jdbcDataSourceRTMap.put("PrepStmtCacheMissCount", getConnection().getAttribute(jdbcProps, "PrepStmtCacheMissCount"));
			this.jdbcDataSourceRTMap.put("Properties", getConnection().getAttribute(jdbcProps, "Properties"));
//			Registered is Deprecated
//			this.jdbcDataSourceRTMap.put("Registered", getConnection().getAttribute(jdbcProps, "Registered"));
			this.jdbcDataSourceRTMap.put("ReserveRequestCount", getConnection().getAttribute(jdbcProps, "ReserveRequestCount"));
			this.jdbcDataSourceRTMap.put("State", getConnection().getAttribute(jdbcProps, "State"));
			this.jdbcDataSourceRTMap.put("Type", getConnection().getAttribute(jdbcProps, "Type"));
			this.jdbcDataSourceRTMap.put("VersionJDBCDriver", getConnection().getAttribute(jdbcProps, "VersionJDBCDriver"));
			this.jdbcDataSourceRTMap.put("WaitingForConnectionCurrentCount", getConnection().getAttribute(jdbcProps, "WaitingForConnectionCurrentCount"));
			this.jdbcDataSourceRTMap.put("WaitingForConnectionFailureTotal", getConnection().getAttribute(jdbcProps, "WaitingForConnectionFailureTotal"));
			this.jdbcDataSourceRTMap.put("WaitingForConnectionHighCount", getConnection().getAttribute(jdbcProps, "WaitingForConnectionHighCount"));
			this.jdbcDataSourceRTMap.put("WaitingForConnectionSuccessTotal", getConnection().getAttribute(jdbcProps, "WaitingForConnectionSuccessTotal"));
			this.jdbcDataSourceRTMap.put("WaitingForConnectionTotal", getConnection().getAttribute(jdbcProps, "WaitingForConnectionTotal"));
			this.jdbcDataSourceRTMap.put("WaitSecondsHighCount", getConnection().getAttribute(jdbcProps, "WaitSecondsHighCount"));
			
			LOG.info(jdbcDataSourceRTMap.toString());
		}
		return jdbcDataSourceRTMap;
	}
	
	public Map<String, Object> getWTCRuntimeInfo(ObjectName obj) {
		this.wtcRTMap.clear();
		return null;
	}

	public Map<String, Object> getJvmRTMap() {
		return jvmRTMap;
	}

	public Map<String, Object> getServerRTMap() {
		return serverRTMap;
	}

	public Map<String, Object> getThreadPoolRTMap() {
		return threadPoolRTMap;
	}
	
	public Map<String, Object> getJdbcDataSourceRTMap() {
		return jdbcDataSourceRTMap;
	}
	
	public List<Object> getAppListByName() {
		return appListByName;
	}
	
	public Map<String, Object> getWtcRTMap() {
		return wtcRTMap;
	}

	public void usage() {
		spit("Usage: java " + WlsStateCollector.class.getName()
				+ " <hostname> <port> <username> <password> <option>");
	}

	private void spit(Object o) {
		System.out.println(o);

	}
}
