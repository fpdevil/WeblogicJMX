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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.management.ObjectName;

import com.jmx.wls.core.WlsStateCollector;

public class ApplicationRuntimes {

	static WlsStateCollector wsc = new WlsStateCollector();

	private TreeMap<Object, Object> deploymentListMap = new TreeMap<Object, Object>();

	private static ObjectName findDeploymentByName(String deploymentName) throws Exception {
		ObjectName[] appDeployments = wsc.getApplicationRuntime();
		for (ObjectName app : appDeployments) 
		{
			String appName = (String) wsc.getConnection().getAttribute(app,	"Name");
			if (appName.equals(deploymentName)) 
			{
				return app;
			}
		}
		return null;
	}

	public Map<Object, Object> getDeploymentsInfo() throws Exception {
		this.deploymentListMap.clear();
		wsc.getDeploymentsList();
		for (int apps = 0; apps < wsc.getApplicationRuntime().length; apps++) 
		{
			String appName = (String) wsc.getConnection().getAttribute(wsc.getApplicationRuntime()[apps], "Name");
			List<String> targetList = new ArrayList<String>();
			ObjectName appDeployment = findDeploymentByName(appName);
			if (appDeployment == null) {
				throw new Exception("The deployment could not be found in the MBean Server");
			}
			ObjectName[] targets = (ObjectName[]) wsc.getConnection().getAttribute(appDeployment, "Targets");
			for (ObjectName target : targets) 
			{
				String targetName = (String) wsc.getConnection().getAttribute(target, "Name");
				targetList.add(targetName);
			}
			this.deploymentListMap.put(appName, targetList);
		}
		return this.deploymentListMap;
	}

	public TreeMap<Object, Object> getDeploymentListMap() {
		return deploymentListMap;
	}
	
	public static String seperator = "=>";

	public void displayStats(TreeMap<Object,Object> obj) {
		for (Map.Entry<Object, Object> m : obj.entrySet()) 
		{
			System.out.printf("%-38s %s %s", m.getKey(), seperator,	m.getValue());
			System.out.println();
		}
	}

//	public static void main(String[] args) throws Exception {
//		
//		ApplicationRuntimes ar = new ApplicationRuntimes();
//		
//		if (args.length == 0) {
//			wsc.usage();
//			System.exit(1);
//		}
//
//		String hostname = args[0];
//		String portString = args[1];
//		String username = args[2];
//		String password = args[3];
//		
//		wsc.initConnection(hostname, portString, username, password);
//		ar.getDeploymentsInfo();
//		ar.displayStats(ar.getDeploymentListMap());
//		wsc.closeConnection();
//	}
}
