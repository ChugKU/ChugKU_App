package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.manager.CMConfigurator;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class Server {
	private CMServerStub serverStub; //send
	private ServerHandler serverHandler;
	
	private boolean isRun;
	Scanner sc = null;
	
	public Server() {
		serverStub = new CMServerStub();
		serverHandler = new ServerHandler(serverStub);
		serverHandler.init();
		serverStub.setAppEventHandler(serverHandler);
	}
	
	public CMServerStub getServerStub() {
		return serverStub;
	}
	
	public ServerHandler getServerHandler() {
		return serverHandler;
	}
	
	public void serverStart() {
		isRun = true;
		sc = new Scanner(System.in);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		int command = -1;
				
		while(isRun) {
			System.out.println("Type \"0\" for menu.");
			System.out.print("> ");
			
			try {
				input = br.readLine();
			}catch(IOException e) {
				e.printStackTrace();
				continue;
			}
			
			try {
				command = Integer.parseInt(input);
			}catch(NumberFormatException e) {
				System.out.println("Incorrect command number");
				continue;
			}
			
			switch(command) {
			case 0:
				printAllMenus();
				break;
			case 100:
				startCM();
				break;
			case 999:
				terminateCM();
				break;
			case 1:
				printSessionInfo();
				break;
			case 2:
				printGroupInfo();
				break;
			case 3:
				printLoginUsers();
				break;
			case 4:
				printConfigurations();
				break;
			case 5:
				changeConfiguration();
				break;
			default:
				System.err.println("Unknown command.");
				break;
			}
		}
	}
	
	private void printAllMenus() {
		System.out.println("---------------MENU---------------");
		System.out.println("| 0: print all menus             |");
		System.out.println("| 100: start CM                  |");
		System.out.println("| 999: terminate CM              |");
		System.out.println("----------------------------------");
		System.out.println("| 1: print session information   |");
		System.out.println("| 2: print group information     |");
		System.out.println("| 3: print login users           |");
		System.out.println("----------------------------------");
		System.out.println("| 4: print configuration         |");
		System.out.println("| 5: change configuration        |");
		System.out.println("----------------------------------");
	}
	
	public void startCM() {
		String strSavedServerAddress = null;
		String strCurServerAddress = null;
		int savedServerPort = -1;
		String strNewServerAddress = null;
		String strNewServerPort = null;
		int newServerPort = -1;
		
		strSavedServerAddress = serverStub.getServerAddress();
		strCurServerAddress = CMCommManager.getLocalIP();
		savedServerPort = serverStub.getServerPort();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("<<<Start CM>>>");
		System.out.println("detected server address: "+strCurServerAddress);
		System.out.println("saved server port: "+savedServerPort);
		
		try {
			System.out.print("new server address (enter for detected value): ");
			strNewServerAddress = br.readLine().trim();
			if(strNewServerAddress.isEmpty()) strNewServerAddress = strCurServerAddress;
			
			System.out.print("new server port (enter for saved value): ");
			strNewServerPort = br.readLine().trim();
			try {
				if(strNewServerPort.isEmpty()) newServerPort = savedServerPort;
				else newServerPort = Integer.parseInt(strNewServerPort);
			} catch(NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
			if(!strNewServerAddress.equals(strSavedServerAddress)) serverStub.setServerAddress(strNewServerAddress);
			if(newServerPort!=savedServerPort) serverStub.setServerPort(Integer.parseInt(strNewServerPort));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		boolean result = serverStub.startCM();
		if(!result) {
			System.err.println("CM initialization error!");
			return;
		}
		
		this.serverStart();
	}
	
	private void terminateCM() {
		serverStub.terminateCM();
		isRun = false;
	}
	
	private void printSessionInfo() {
		System.out.println("------------------------------------------------------");
		System.out.format("%-20s%-20s%-10s%-10s%n", "session name", "session addr", "port", "#users");
		System.out.println("------------------------------------------------------");
		
		CMInteractionInfo interInfo = serverStub.getCMInfo().getInteractionInfo();
		Iterator<CMSession> iter = interInfo.getSessionList().iterator();
		while(iter.hasNext())
		{
			CMSession session = iter.next();
			System.out.format("%-20s%-20s%-10d%-10d%n", session.getSessionName(), session.getAddress()
					, session.getPort(), session.getSessionUsers().getMemberNum());
		}
		return;
	}
	
	private void printGroupInfo() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String strSessionName = null;
		
		System.out.println("====== print group information");
		System.out.print("Session name: ");
		try {
			strSessionName = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CMInteractionInfo interInfo = serverStub.getCMInfo().getInteractionInfo();
		CMSession session = interInfo.findSession(strSessionName);
		if(session == null)
		{
			System.out.println("Session("+strSessionName+") not found.");
			return;
		}
		
		System.out.println("------------------------------------------------------------------");
		System.out.format("%-20s%-20s%-10s%-10s%n", "group name", "multicast addr", "port", "#users");
		System.out.println("------------------------------------------------------------------");

		Iterator<CMGroup> iter = session.getGroupList().iterator();
		while(iter.hasNext())
		{
			CMGroup gInfo = iter.next();
			System.out.format("%-20s%-20s%-10d%-10d%n", gInfo.getGroupName(), gInfo.getGroupAddress()
					, gInfo.getGroupPort(), gInfo.getGroupUsers().getMemberNum());
		}

		System.out.println("======");
		return;
	}
	
	private void printLoginUsers() {
		System.out.println("========== print login users");
		CMMember loginUsers = serverStub.getLoginUsers();
		if(loginUsers == null)
		{
			System.err.println("The login users list is null!");
			return;
		}
		
		System.out.println("Currently ["+loginUsers.getMemberNum()+"] users are online.");
		Vector<CMUser> loginUserVector = loginUsers.getAllMembers();
		Iterator<CMUser> iter = loginUserVector.iterator();
		int nPrintCount = 0;
		while(iter.hasNext())
		{
			CMUser user = iter.next();
			System.out.print(user.getName()+" ");
			nPrintCount++;
			if((nPrintCount % 10) == 0)
			{
				System.out.println();
				nPrintCount = 0;
			}
		}
	}
	
	private void printConfigurations() {
		String[] strConfigurations;
		System.out.print("========== print all current configurations\n");
		Path confPath = serverStub.getConfigurationHome().resolve("cm-server.conf");
		strConfigurations = CMConfigurator.getConfigurations(confPath.toString());
		
		System.out.print("configuration file path: "+confPath.toString()+"\n");
		for(String strConf : strConfigurations)
		{
			String[] strFieldValuePair;
			strFieldValuePair = strConf.split("\\s+");
			System.out.print(strFieldValuePair[0]+" = "+strFieldValuePair[1]+"\n");
		}
	}
	
	private void changeConfiguration() {
		boolean bRet = false;
		String strField = null;
		String strValue = null;
		System.out.println("========== change configuration");
		Path confPath = serverStub.getConfigurationHome().resolve("cm-server.conf");
		
		System.out.print("Field name: ");
		strField = sc.next();
		System.out.print("Value: ");
		strValue = sc.next();
		
		bRet = CMConfigurator.changeConfiguration(confPath.toString(), strField, strValue);
		if(bRet)
		{
			System.out.println("cm-server.conf file is successfully updated: ("+strField+"="+strValue+")");
		}
		else
		{
			System.err.println("The configuration change is failed!: ("+strField+"="+strValue+")");
		}
		
		return;	
	}
	
	public static void main(String args[]) {
		Server server = new Server();
		CMServerStub serverStub = server.getServerStub();
		serverStub.setAppEventHandler(server.getServerHandler());
		
		server.startCM();
	}
	
	
	
}