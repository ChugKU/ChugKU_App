package client;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import client.Client.Player;

public class ClientHandler implements CMAppEventHandler {

	private CMClientStub clientStub;
	private Client client;
	private boolean playerList[];

	public ClientHandler(CMClientStub stub)
	{
		clientStub = stub;
	}
	
	public void setClient(Client client) {
		this.client = client;
		
		if(client.superPeer) {
			playerList = new boolean[client.playerList.size()];
		}
	}
	
	@Override
	public void processEvent(CMEvent cme) {
		
		playerList[cme.getID()] = true;
		
		switch(cme.getType())
		{
		case CMInfo.CM_USER_EVENT:
			processUserEvent(cme);
			break;
		default:
			return;
		}
		
		if(client.superPeer) {
			for(int i=0; i<playerList.length; i++) {
				if(!playerList[i]) {
					i=0;
				}
			}
		}
		
	}
	
	private void processUserEvent(CMEvent cme) {
		CMUserEvent ue = (CMUserEvent) cme;
		String action = ue.getStringID();
		
		switch(action) {
		case "startGame":
			client.setIngGame(true);
			break;
			
		case "endGame":
			client.setIngGame(false);
			break;
			
		case "move":
			int x = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "x"));
			int y = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "y"));
			//Player temp = new Player(ue.getID(), x, y);
			client.setPlayer(client.new Player(ue.getID(), x, y));
			break;
			
		case "kick":
			// ?
			break;
			
		default:
			break;
		}
	}

}
