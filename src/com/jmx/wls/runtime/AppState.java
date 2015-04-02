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
