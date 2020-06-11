package server;

import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;
import java.util.Scanner;

public class Server {
	private CMServerStub serverStub; //send
	private ServerHandler serverHandler;
	
	public Server() {
		serverStub = new CMServerStub();
		serverHandler = new ServerHandler(serverStub);
		serverStub.setAppEventHandler(serverHandler);
	}
	
	public CMServerStub getServerStub() {
		return serverStub;
	}
	
	public ServerHandler getServerHandler() {
		return serverHandler;
	}
	
	public static void main(String args[]) {
		Server server = new Server();
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			scanner.nextLine();
		}
	}
}