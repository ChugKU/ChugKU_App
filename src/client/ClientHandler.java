package client; //jihyun hi~^^~f

import kr.ac.konkuk.ccslab.cm.event.CMDataEvent;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;


public class ClientHandler implements CMAppEventHandler {
	private Client client;
	private CMClientStub clientStub;
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
		
		//playerList[cme.getID()] = true;
		
		switch(cme.getType())
		{
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
			break;
		case CMInfo.CM_USER_EVENT:
			processUserEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
		case CMInfo.CM_DATA_EVENT:
			processDataEvent(cme);
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
			if(client.superPeer) {	
				int x = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "x"));
				int y = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "y"));
				int kick = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "kick"));
				
				//client.playerList.getIndexof(1) = engine.(x,y,kick);	superpper�쓽 playerlis �뾽�뜲�씠
			}
			break;
			
		case "update":
			if(!client.superPeer) {
				int id = ue.getID();
				int x = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "x"));
				int y = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "y"));
				int kick = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "kick"));
			
				client.setPlayerList(client.getPlayerID(), client.new Player(id, x, y));
			}		
			break;
		default:
			break;
		}
	}
	
	private void processSessionEvent(CMEvent cme)
	{
		CMSessionEvent se = (CMSessionEvent)cme;
		switch(se.getID())
		{
		case CMSessionEvent.REGISTER_USER_ACK:
			if( se.getReturnCode() == 1 )
			{
				// user registration succeeded
				System.out.println("User["+se.getUserName()+"] successfully registered at time["
							+se.getCreationTime()+"].");
			}
			else
			{
				// user registration failed
				System.out.println("User["+se.getUserName()+"] failed to register!");
			}
			break;
		case CMSessionEvent.UNEXPECTED_SERVER_DISCONNECTION:
			System.err.println("Unexpected disconnection from ["+se.getChannelName()
					+"] with key["+se.getChannelNum()+"]!");
			break;
		case CMSessionEvent.INTENTIONALLY_DISCONNECT:
			System.err.println("Intentionally disconnected all channels from ["
					+se.getChannelName()+"]!");
			break;
		default:
			return;
		}
	}
	
	private void processDummyEvent(CMEvent cme) {
		CMDummyEvent due = (CMDummyEvent) cme;
		
		// Parse Client-Server Request
		String inputStr[] = due.getDummyInfo().split(" ");
		
		switch (inputStr[0]) {
		case "okay":
			clientStub.leaveSession();
			clientStub.joinSession("Session2");
			
			clientStub.changeGroup(inputStr[1]);
			break;
		case "deny":
			//Room Access Deny
			client.updateRoomList();
			break;
		case "ingGame":
			CMDummyEvent response = new CMDummyEvent();
			response.setID(due.getID());
			if(client.isIngGame()) {
				due.setDummyInfo("deny");
			}else {
				due.setDummyInfo("okay");
			}
			clientStub.send(due,"SERVER");
			break;
		default:	
			return;
		}

		return;
	}
	
	private void processDataEvent(CMEvent cme)
	{
		CMDataEvent de = (CMDataEvent) cme;
		switch(de.getID())
		{
		case CMDataEvent.NEW_USER:
			System.out.println("["+de.getUserName()+"] enters group("+de.getHandlerGroup()+") in session("
					+de.getHandlerSession()+").");
			break;
		case CMDataEvent.REMOVE_USER:
			System.out.println("["+de.getUserName()+"] leaves group("+de.getHandlerGroup()+") in session("
					+de.getHandlerSession()+").");
			break;
		default:
			return;
		}
	}
	

}
