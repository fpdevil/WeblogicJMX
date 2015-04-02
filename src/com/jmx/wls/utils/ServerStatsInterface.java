/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Sampath Singamsetty <Singamsetty.Sampath@gmail.com>

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

package com.jmx.wls.utils;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * @author Sampath Singamsetty
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
