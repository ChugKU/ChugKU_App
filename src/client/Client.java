package client;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import java.util.ArrayList;
import java.util.Vector;

public class Client {
	private CMClientStub clientStub;
	private ClientHandler clientHandler;
	private int state, cmd;
	private boolean ingGame;
	
	String session, group;
	ArrayList<String> roomList;

	Client(String session, String group){
		clientStub = new CMClientStub();
		clientHandler = new ClientHandler(clientStub);
		this.session = session;
		this.group = group;
		
		roomList = new ArrayList<String>();
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
		clientStub.multicast(cme, session, group);
	}
	
	public void start() {
		// TODO Auto-generated method stub		
		boolean ret;
		
		clientStub.startCM();
		
		//get client name!
		
		ret = clientStub.joinSession("Session 1"); // Enter Lobby Session
		if(ret)
			System.out.println("successfully sent the session-join request.");
		else
			System.err.println("failed the session-join request!");
		
	}

	public void setRoomList(String[] rooms) {
		roomList.clear();
		for(int i=0; i<rooms.length; i++) {
			roomList.add(rooms[i]);
		}
	}
	
	public ArrayList<String> getRoomList() {
		CMInteractionInfo interInfo = clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		CMSession session = interInfo.findSession(myself.getCurrentSession());
		Vector<CMGroup> roomGroup = session.getGroupList();
		ArrayList<String> roomList = new ArrayList<String>();
		
		for(int i=0; i<roomGroup.size(); i++) {
			roomList.add(roomGroup.elementAt(i).getGroupName());
		}
		
		return roomList;
	}
	
	public void createRoom(String roomName) {
		
		clientStub.leaveSession();
		
		clientStub.joinSession("Session2");
		
		
		CMInteractionInfo interInfo = clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		CMSession session = interInfo.findSession(myself.getCurrentSession());
		
		session.createGroup(roomName, myself.getHost(), myself.getUDPPort()); 
		clientStub.changeGroup(roomName);
	}
	
	public void enterRoom(String roomName) {
		CMDummyEvent due = new CMDummyEvent();
		due.setDummyInfo("Enter@#$" + roomName);
		//
		due.setSender(clientStub.getMyself().getName());
		
		clientStub.send(due, "SERVER");
	}
	
	public void exitRoom() {
		//session change
		clientStub.leaveSession();
		
		clientStub.joinSession("Session1");
	}

	public void updateRoomList() {
		// TODO Auto-generated method stub
		state = 1; // lobby panel
		cmd = 1; // update String types of room name arraylist
	}

	public boolean isIngGame() {
		// TODO Auto-generated method stub
		return ingGame;
	}
	
	
	
}
