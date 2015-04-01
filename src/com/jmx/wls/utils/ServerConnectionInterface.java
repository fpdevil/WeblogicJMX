package com.jmx.wls.utils;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

public interface ServerConnectionInterface {
	/**
	 * Initiate connection to a WLS Server instance for gathering the statistics
	 * When the required parameters are all supplied it provides runtime
	 * statistics
	 * 
	 * @param hostname
	 * @param portString
	 * @param username
	 * @param password
	 * 
	 * 
	 *            This method throws the below exceptions
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public void initConnection(String hostname, String portString,
			String username, String password)
			throws AttributeNotFoundException, InstanceNotFoundException,
			MBeanException, ReflectionException;
}
