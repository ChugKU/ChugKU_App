package server;

import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class Server {
	private CMServerStub serverStub; //send
	private ServerHandler serverHandler;
	
	public Server() {
		serverStub = new CMServerStub();
		serverHandler = new ServerHandler(serverStub);
	}
	
	public CMServerStub getServerStub() {
		return serverStub;
	}
	
	public ServerHandler getServerHandler() {
		return serverHandler;
	}
}
