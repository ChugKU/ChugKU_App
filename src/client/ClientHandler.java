package client;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import client.Client.Player;

public class ClientHandler implements CMAppEventHandler {

	private CMClientStub clientStub;

	public ClientHandler(CMClientStub stub)
	{
		clientStub = stub;
	}
	
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		switch(cme.getType())
		{
		case CMInfo.CM_USER_EVENT:
			processUserEvent(cme);
			break;
		default:
			return;
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
			client.setPlayer(new Player(ue.getID(), x, y));
			break;
			
		default:
			break;
		}
	}

}
