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
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ServerHandler implements CMAppEventHandler {
	private CMServerStub serverStub;
	CMSession gameSession;
	
	Vector<CMGroup> roomList;
	
	public ServerHandler(CMServerStub serverstub){
		serverStub = serverstub;
		gameSession = new CMSession("sessionName", "sessonAddress", 0); //TODO should be set
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
		System.out.println("Dummy Event: " + dummyEvent.getDummyInfo());
		String[] splited = dummyEvent.getDummyInfo().split(" ");
		String command = splited[0];
		String roomName = splited[1];
		
		if(command.equals("enter")) {
			System.out.println("Enter?");
			roomList = gameSession.getGroupList();
			Iterator<CMGroup> itr = roomList.iterator();
			CMGroup dest = null;
			CMDummyEvent newDummyEvent = new CMDummyEvent();
			
			while(itr.hasNext()) {
				System.out.println(itr.next().getGroupName() + " " + roomName);
				if(itr.next().getGroupName().equals(roomName)) {
					dest = itr.next();
					break;
				}
			}
			
			if(dest==null) {//if room destroyed right after the request
				newDummyEvent.setDummyInfo("deny "+roomName);
			}
			else {
				Vector<CMUser> member = dest.getGroupUsers().getAllMembers();

				if(member.size()==1) newDummyEvent.setDummyInfo("okay "+roomName);
				else if(member.size()==4) newDummyEvent.setDummyInfo("deny "+roomName);
				else {
					//if ingGame==true, return deny / else return okay
					CMDummyEvent checkGame = new CMDummyEvent();
					checkGame.setDummyInfo("ingGame "+roomName);
					checkGame.setID(new Random().nextInt());
					
					CMDummyEvent received = (CMDummyEvent)serverStub.sendrecv(checkGame, member.get(0).getName(), CMInfo.CM_DUMMY_EVENT, checkGame.getID(), 1);
										
					if(received.getDummyInfo().equals("okay")) newDummyEvent.setDummyInfo("okay "+roomName);
					else newDummyEvent.setDummyInfo("deny "+roomName);
				}
				
				serverStub.send(newDummyEvent, dummyEvent.getSender());
			}
		}
		else {
			System.err.println("Unknown Command");
		}
	}
}
