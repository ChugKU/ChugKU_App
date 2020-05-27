package client;

import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class Client {
	private CMClientStub clientStub;
	private ClientHandler clientHandler;
	String session, group;

	Client(String session, String group){
		clientStub = new CMClientStub();
		clientHandler = new ClientHandler(clientStub);
		this.session = session;
		this.group = group;
	}
	
	//return "client application service" - interaction with CM
	public CMClientStub getClientStub() {
		return clientStub;
	}		
	
	public ClientHandler getClientEventHandler() {
		return clientHandler;
	}
	
	public void multicast() {
		CMUserEvent cme = new CMUserEvent();
		//cme.setEventField(nDataType, strFieldName, strFieldValue);
		clientStub.multicast(cme, session, group);
	}
}
