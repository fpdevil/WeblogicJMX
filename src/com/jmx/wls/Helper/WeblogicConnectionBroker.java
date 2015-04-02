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
