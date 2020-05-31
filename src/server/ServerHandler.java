package server;

import java.util.ArrayList;
import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ServerHandler implements CMAppEventHandler {
	private CMServerStub serverStub;
	private ArrayList<String> roomList = new ArrayList<>();
	
	public ServerHandler(CMServerStub serverstub){
		serverStub = serverstub;
	}
	
	private ArrayList<String> getRoomList() {
		return roomList;
	}

	@Override
	public void processEvent(CMEvent event) {
		// TODO Auto-generated method stub
		switch(event.getType()) {
		case CMInfo.CM_SESSION_EVENT: //server in&out
			processSessionEvent(event);
			break;
		case CMInfo.CM_USER_EVENT:
			processUserEvent(event);
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
	
	private void processUserEvent(CMEvent event) {
		CMUserEvent userEvent = (CMUserEvent)event;
		
		
	}
	
	private void processDummyEvent(CMEvent event) {
		CMDummyEvent dummyEvent = (CMDummyEvent)event;
		String[] splited = dummyEvent.getDummyInfo().split("@#$");
		String command = splited[0];
		String roomName = splited[1];
		
		if(command.equals("create")) {
			Iterator<String> itr = roomList.iterator();
			boolean isExisted = false;
			
			while(itr.hasNext()) {
				if(itr.next().equals(roomName)) {
					isExisted = true;
					break;
				}
			}
			
			if(isExisted) {
				//serverStub.replyEvent(dummyEvent, 0);
			}
			else {
				CMGroupInfo groupInfo = new CMGroupInfo();
				groupInfo.setGroupAddress("????");
				groupInfo.setGroupName(roomName);
				groupInfo.setGroupPort(0); //?
				CMSessionEvent temp = new CMSessionEvent();
				temp.addGroupInfo(groupInfo);
				
				roomList.add(roomName);
				serverStub.replyEvent(dummyEvent, 1);
			}
		}
		else if(command.equals("enter")) {
			boolean isFull = false;
			boolean isStarted = false;
			
			//find if the room is full or started
			
			if(!isFull && !isStarted) {
				//enter the group
				CMSessionEvent next = new CMSessionEvent();
				next.setID(CMSessionEvent.JOIN_SESSION);
				next.setGroupNum(0); //????
				this.processSessionEvent(next);
			}
		}
		else if(command.equals("start game")) {
			//set the session started game
		}
		else if(command.equals("exit")) {
			//go back to the lobby
			CMSessionEvent next = new CMSessionEvent();
			next.setID(CMSessionEvent.JOIN_SESSION);
			next.setGroupNum(0); //????
			processSessionEvent(next);
		}
		else {
			System.err.println("Unknown command");
		}
	}
}
