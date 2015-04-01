package com.jmx.wls.state;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmx.wls.core.WlsStateCollector;
import com.jmx.wls.runtime.ServerStatus;

public class PrintStatus {

	private static final Logger LOG = LoggerFactory.getLogger(PrintStatus.class);
	
	public Map<String, Object> o = new HashMap<String, Object>();
	public static String seperator = "=>";

	/**
	 * The functions takes in an Object map and prints the key, value pairs
	 * 
	 * @param obj
	 */
	public static void displayStats(Map<String, Object> obj) {
		
		for (Map.Entry<String, Object> m : obj.entrySet()) {
			// System.out.println(m.getKey() + " : " + m.getValue());
			System.out.printf("%-32s %s %s", m.getKey(), seperator,
					m.getValue());
			System.out.println();
		}
	}

	public static void main(String[] args) throws AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException,
			IOException {

		WlsStateCollector wsc = new WlsStateCollector();
		ServerStatus ss = new ServerStatus();

		if (args.length != 5 || args.length == 0) {
			wsc.usage();
			System.exit(1);
		}

		String hostname = args[0];
		String portString = args[1];
		String username = args[2];
		String password = args[3];
		String option = args[4];
		
		//wsc.initConnection(hostname, portString, username, password);
		
		switch (option) {
		case "jdbc":
			LOG.info("***** JDBCINFORMATION: Statistics gathering for option {}", option);
			wsc.initConnection(hostname, portString, username, password);
			for (int i = 0; i < wsc.getServerRT().length; i++) 
			{
				wsc.getJDBCDataSourceRTInfo(wsc.getServerRT()[i]);
				System.out.println("------------------------------------------");
				PrintStatus.displayStats(wsc.getJdbcDataSourceRTMap());
			}
			wsc.closeConnection();
			break;
		case "wtc":
			LOG.info("***** WTCINFORMATION: Statistics gathering for option {}", option);
			wsc.initConnection(hostname, portString, username, password);
			for (int x = 0; x < wsc.getWTCRuntime().length; x++)
			{
				System.out.println(wsc.getWTCRuntime()[x]);
				System.out.println();
			}
			wsc.closeConnection();
			break;
		case "jvm":
			LOG.info("***** JVMINFORMATION: Statistics gathering for option {}", option);
			wsc.initConnection(hostname, portString, username, password);
			for (int i = 0; i < wsc.getServerRT().length; i++) 
			{
				System.out.println("\nFor Instance: " + wsc.getConnection().getAttribute(wsc.getServerRT()[i], "Name"));
				wsc.getJVMInfo(wsc.getServerRT()[i]);
				System.out.println("------------------------------------------");
				PrintStatus.displayStats(wsc.getJvmRTMap());
			}
			wsc.closeConnection();
			break;
		case "threadpool":
			LOG.info("***** THREADPOOLINFORMATION: Statistics gathering for option {}", option);
			wsc.initConnection(hostname, portString, username, password);
			for (int i = 0; i < wsc.getServerRT().length; i++) 
			{
				System.out.println("\nFor Instance: " + wsc.getConnection().getAttribute(wsc.getServerRT()[i], "Name"));
				wsc.getThreadPoolRuntimeInfo(wsc.getServerRT()[i]);
				System.out.println("------------------------------------------");
				PrintStatus.displayStats(wsc.getThreadPoolRTMap());
			}
			wsc.closeConnection();
			break;
		case "svrstatus":
			LOG.info("***** SERVERSTATUSINFORMATION: Statistics gathering for option {}", option);
			ss.initConnection(hostname, portString, username, password);
			ss.getRuntimeServerStats(ss.getDomainConfigServerName());
			// System.out.println("***** Managed Servers Running: " + ss.getWlsServersRunningList());
			// System.out.println("***** Managed Servers Shutdown: " + ss.getWlsServersShutdownList());
			LOG.info("***** Managed Servers Running: {}", ss.getWlsServersRunningList());
			LOG.info("***** Managed Servers Shutdown: {}", ss.getWlsServersShutdownList());
			for (int i = 0; i < ss.getWlsServerRT().length; i++)
			{
				// Excluding the Admin Server from the output statistics as its the
				// managed server which hosts the application and is important
				// If Admin Server output is also required take the content of the if loop 
				// outside of the loop
				boolean adminSvr = (boolean) ss.getConnection().getAttribute(ss.getWlsServerRT()[i], "AdminServer");
				if (adminSvr == false)
				{
					// System.out.println("\nFor Instance: " + ss.getConnection().getAttribute(ss.getWlsServerRT()[i], "Name"));
					System.out.println("------------------------------------------");
					LOG.info("For Weblogic Instance {}", ss.getConnection().getAttribute(ss.getWlsServerRT()[i], "Name"));
					ss.getWlsServerRuntimeInfo(ss.getWlsServerRT()[i]);
					PrintStatus.displayStats(ss.getWlsServerRTMap());
				}
			}
			ss.closeConnection();
			break;
		default:
			LOG.error("Invalid Arguments: {}", option);
			System.out.println("Invalid Option: " + option + " specified");
			System.out.println("Please specify a Valid Option: [jdbc|wtc|jvm|threadpool|svrstatus]");
			break;
		}

		// wsc.closeConnection();
		
//		for (int i = 0; i < wsc.getServerRT().length; i++) {
//			wsc.getJVMInfo(wsc.getServerRT()[i]);
//			wsc.getServerRuntimeInfo(wsc.getServerRT()[i]);
//			wsc.getThreadPoolRuntimeInfo(wsc.getServerRT()[i]);
//			wsc.getJDBCRuntimeInfo(wsc.getServerRT()[i]);
//			wsc.getJDBCDataSourceRTInfo(wsc.getServerRT()[i]);

//			System.out.println("------------------------------------------");


//			 PrintStatus.displayStats(wsc.getServerRTMap());
//			 PrintStatus.displayStats(wsc.getJvmRTMap());
//			 PrintStatus.displayStats(wsc.getThreadPoolRTMap());
//			 PrintStatus.displayStats(wsc.getJdbcDataSourceRTMap());
//		}
		

//		for (int x = 0; x < wsc.getWTCRuntime().length; x++) {
//			System.out.println(wsc.getWTCRuntime()[x]);
//			System.out.println();
//		}
		
//		for (int i = 0; i < wsc.getServerRT().length; i++) {
//			wsc.getJDBCDataSourceRTInfo(wsc.getServerRT()[i]);
//			System.out.println("------------------------------------------");
//			PrintStatus.displayStats(wsc.getJdbcDataSourceRTMap());
//		}
		
//		for (int i = 0; i < wsc.getServerRT().length; i++) {
//			System.out.println("\nFor Instance: " + wsc.getConnection().getAttribute(wsc.getServerRT()[i], "Name"));
//			wsc.getJVMInfo(wsc.getServerRT()[i]);
//			System.out.println("------------------------------------------");
//			PrintStatus.displayStats(wsc.getJvmRTMap());
//		}
		
//		wsc.closeConnection();
	}

}
