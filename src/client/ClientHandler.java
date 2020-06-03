package client;

import kr.ac.konkuk.ccslab.cm.event.CMDataEvent;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;

public class ClientHandler implements CMAppEventHandler {
	private Client client;
	private CMClientStub clientStub;
	
	public ClientHandler(CMClientStub stub)
	{
		clientStub = stub;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
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
		
	}
	
	private void processUserEvent(CMEvent cme) {
		
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
		String inputStr[] = due.getDummyInfo().split("@#$");
		
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
