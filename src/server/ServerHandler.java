package server;

import java.util.Iterator;
import java.util.Vector;
import java.util.Random;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ServerHandler implements CMAppEventHandler {
	private CMServerStub serverStub;
	CMSession gameSession;	
	Vector<Boolean> enterRoomList;
	
	public ServerHandler(CMServerStub serverstub){
		serverStub = serverstub;
	}
	
	public void init() {
		//CMInteractionInfo interInfo = serverStub.getCMInfo().getInteractionInfo();
		//gameSession = interInfo.findSession("session2");
		enterRoomList = new Vector<Boolean>();
		for(int i=0; i<4; i++)
			enterRoomList.add(true);
	}
	
	@Override
	public void processEvent(CMEvent event) {
		// TODO Auto-generated method stub
		switch(event.getType()) {
		case CMInfo.CM_SESSION_EVENT: //server in&out
			processSessionEvent(event);
			break;
		case CMInfo.CM_INTEREST_EVENT:
			processInterestEvent(event);
			break;
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(event);
			break;
		default:
			return;
		}
	}
	
	private void processSessionEvent(CMEvent event) {
		CMSessionEvent sessionEvent = (CMSessionEvent)event;
		
		switch(sessionEvent.getID()) {
		case CMSessionEvent.LOGIN:
			System.out.println("["+sessionEvent.getUserName()+"] requests login.");
			System.out.println("["+sessionEvent.getUserName()+"] logs in.");
			serverStub.replyEvent(sessionEvent, 1);
			break;
		case CMSessionEvent.LOGOUT:
			System.out.println("["+sessionEvent.getUserName()+"] logs out.");
			break;
		case CMSessionEvent.REQUEST_SESSION_INFO:
			System.out.println("["+sessionEvent.getUserName()+"] requests session information.");
			break;
		case CMSessionEvent.JOIN_SESSION:
			System.out.println("["+sessionEvent.getUserName()+"] requests to join session("+sessionEvent.getSessionName()+").");
			break;
		case CMSessionEvent.LEAVE_SESSION:
			System.out.println("["+sessionEvent.getUserName()+"] leaves a session("+sessionEvent.getSessionName()+").");
			break;
		case CMSessionEvent.UNEXPECTED_SERVER_DISCONNECTION:
			System.err.println("Unexpected disconnection from ["+sessionEvent.getChannelName()+"] with key["+sessionEvent.getChannelNum()+"]!");
			break;
		case CMSessionEvent.INTENTIONALLY_DISCONNECT:
			System.err.println("Intentionally disconnected all channels from ["+sessionEvent.getChannelName()+"]!");
			break;
		default:
			return;
		}
	}
	
	private void processInterestEvent(CMEvent event) {
		CMInterestEvent interestEvent = (CMInterestEvent)event;
		switch(interestEvent.getID())
		{
		case CMInterestEvent.USER_ENTER:
			System.out.println("["+interestEvent.getUserName()+"] enters group("+interestEvent.getCurrentGroup()+") in session("
					+interestEvent.getHandlerSession()+").");
			break;
		case CMInterestEvent.USER_LEAVE:
			System.out.println("["+interestEvent.getUserName()+"] leaves group("+interestEvent.getHandlerGroup()+") in session("
					+interestEvent.getHandlerSession()+").");
			break;
		default:
			System.err.println("UNEXPECTED EVENT"+event.getType());
			return;
		}
	}
	
	private void processDummyEvent(CMEvent event) {
		CMDummyEvent dummyEvent = (CMDummyEvent)event;
		String[] splited = dummyEvent.getDummyInfo().split(" ");
		if(splited.length==1) {
			return;
		}
		System.out.println("Dummy Event: " + dummyEvent.getDummyInfo());
		String command = splited[0];
		String roomName = splited[1];
		int index = 0;
		String cmd = null;
		
		if(roomName.contentEquals("g2")) {
			index=0;
		} else if(roomName.contentEquals("g3")) {
			index=1;
		} else if(roomName.contentEquals("g4")) {
			index=2;
		} else if(roomName.contentEquals("g5")) {
			index=3;
		}
		
		if(command.equals("enter")) {
			System.out.println("Enter?");
			CMDummyEvent newDummyEvent = new CMDummyEvent();
			
			newDummyEvent.setID(dummyEvent.getID());
			
			if(enterRoomList.get(index)) {
				cmd = "okay";
			} else {
				cmd = "deny";
			}
			newDummyEvent.setDummyInfo(cmd);
			serverStub.send(newDummyEvent, dummyEvent.getSender());
		}
		else if(command.equals("busy")){
			enterRoomList.set(index, false);
		} else if(command.equals("free")){
			enterRoomList.set(index, true);
		} else {
			System.err.println("Unknown Command");
		}
	}
}
