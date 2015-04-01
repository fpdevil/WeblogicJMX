/**
 * 
 */
package com.jmx.wls.utils;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * @author n073866 (Sampath Singamsetty)
 * 
 */
public interface ServerStatsInterface {
	
	/**
	 * Print an array of ServerRuntimeMBeans This MBean is the root of the
	 * runtime MBean hierarchy, and each server in the domain hosts its own
	 * instance. Its accessed through DomainRuntimeServiceMBean.ServerRuntimes
	 * It contains all ServerRuntimeMBean instances on all servers in the domain
	 * Type: ServerRuntimeMBean[] The link MBeanReference has more information
	 * <a href=MBeanReference
	 * >http://docs.oracle.com/cd/E11035_01/wls100/wlsmbeanref
	 * /core/index.html</a>
	 * 
	 * @return All ServerRuntimeMBean instances on all servers in the domain
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public ObjectName[] getServerRuntimes() throws AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			IOException;

	/**
	 * Provides methods for retrieving information about the Java Virtual
	 * Machine (JVM) within with the current server instance is running The link
	 * MBeanReference has more information <a
	 * href=MBeanReference>http://docs.oracle
	 * .com/cd/E11035_01/wls100/wlsmbeanref/core/index.html</a>
	 * 
	 * @return Information about the JVM runtime statistics
	 * @throws IOException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws InstanceNotFoundException
	 * @throws AttributeNotFoundException
	 */
	public ObjectName getJVMRuntime(ObjectName obj)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException;

	/**
	 * Provides method for retrieving the information of current runtime thread
	 * related informationThe link MBeanReference has more information <a
	 * href=MBeanReference
	 * >http://docs.oracle.com/cd/E11035_01/wls100/wlsmbeanref
	 * /core/index.html</a>
	 * 
	 * @return Information about the Thread Pool Runtime MBean
	 * @throws IOException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws InstanceNotFoundException
	 * @throws AttributeNotFoundException
	 */
	public ObjectName getThreadPoolRuntime(ObjectName obj)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException;

	/**
	 * Provides method for accessing the JDBC DataSource runtime information
	 * like the Connection Count, Active connection's etc The MBeanReference <a
	 * href="MBeanReference"
	 * >http://docs.oracle.com/cd/E11035_01/wls100/wlsmbeanref
	 * /core/index.html</a> link has more information
	 * 
	 * @return Information about the JDBC Runtime MBean
	 * @throws IOException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws InstanceNotFoundException
	 * @throws AttributeNotFoundException
	 */
	public ObjectName getJDBCRuntime(ObjectName obj)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException;

	/**
	 * Provides method for accessing the Application Runtime information like
	 * components deployed on one or more targets where a target is either a
	 * cluster or a server
	 * 
	 * @return
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public ObjectName[] getApplicationRuntime()
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException, IOException;

	/**
	 * Provides the WTC runtime information for the servers
	 * 
	 * @return
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	public ObjectName[] getWTCRuntime() throws AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			IOException;

}
