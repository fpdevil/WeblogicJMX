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

	public static void main(String[] args) throws Exception {
		
		ApplicationRuntimes ar = new ApplicationRuntimes();
		
		if (args.length == 0) {
			wsc.usage();
			System.exit(1);
		}

		String hostname = args[0];
		String portString = args[1];
		String username = args[2];
		String password = args[3];
		
		wsc.initConnection(hostname, portString, username, password);
		ar.getDeploymentsInfo();
		ar.displayStats(ar.getDeploymentListMap());
		wsc.closeConnection();
	}
}
